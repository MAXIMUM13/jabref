package org.bibsonomy.plugin.jabref.worker;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import net.sf.jabref.gui.JabRefFrame;
import net.sf.jabref.model.database.BibDatabase;
import net.sf.jabref.model.entry.BibEntry;
import net.sf.jabref.model.entry.FieldName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.plugin.jabref.BibsonomyProperties;
import org.bibsonomy.plugin.jabref.gui.CompareDialog;
import org.bibsonomy.plugin.jabref.util.BibtexEntryUtil;
import org.bibsonomy.plugin.jabref.util.JabRefModelConverter;
import org.bibsonomy.plugin.jabref.util.WorkerUtil;

/**
 * Basic synchronization. The user decides which version of the post to keep
 *
 * @author Waldemar Biller <wbi@cs.uni-kassel.de>
 */
public class SynchronizationWorker extends AbstractPluginWorker {

    private static final Log LOGGER = LogFactory.getLog(SynchronizationWorker.class);

    private boolean keepAllLocal = false;

    private boolean keepAllRemote = false;

    private int status = 0;

    public SynchronizationWorker(final JabRefFrame jabRefFrame) {
        super(jabRefFrame);
    }

    public void run() {

        // Database Object. All operations are performed on this
        final BibDatabase bibDatabase = this.jabRefFrame.getCurrentBasePanel().getDatabase();

        // Set for the entries we have fetched from Bibsonomy
        final HashSet<BibEntry> newEntries = new HashSet<>();

        // Set of id to be removed from the database
        final HashSet<BibEntry> removeIds = new HashSet<>();

        LogicInterface logic = getLogic();
        // Iterate over all entries in the database
        for (final BibEntry entry : bibDatabase.getEntries()) {

            final String intrahash = entry.getField(FieldName.INTRAHASH).get();

            // check if intrahash is present, otherwise go to next entry
            if ((intrahash == null) || (intrahash.length() == 0)) {
                // TODO: new entries shall not be added to bibsonomy?
                continue;
            }

            try {
                // get the entry with the specific intrahash
                final Post<? extends Resource> post = logic.getPostDetails(intrahash, BibsonomyProperties.getUsername());

                if (!BibtexEntryUtil.areEqual(entry, JabRefModelConverter.convertPost(post))) {

                    // show the compare dialog to let the user choose which
                    // entry wants to keep
                    if (!this.keepAllLocal && !this.keepAllRemote) {
                        this.status = CompareDialog.showCompareDialog(this.jabRefFrame, entry, post);
                    }

                    switch (this.status) {
                        // upload the entry if the user chose "keep local" on the
                        // compare dialog
                        case CompareDialog.KEEP_LOCAL_ALWAYS:
                            this.keepAllLocal = true;
                            this.keepAllRemote = false;
                        case CompareDialog.KEEP_LOCAL:

                            //We have to take intrahash of the incoming Post from Bibsonomy to export entries
                            entry.setField(FieldName.INTRAHASH, post.getResource().getIntraHash());
                            entry.setField(FieldName.INTERHASH, post.getResource().getInterHash());

                            final List<BibEntry> entries = new LinkedList<BibEntry>();
                            entries.add(entry);

                            ExportWorker worker = new ExportWorker(this.jabRefFrame, entries);
                            WorkerUtil.performAsynchronously(worker);

                            break;

                        // fetch the entry if the user choose "keep remote"
                        case CompareDialog.KEEP_REMOTE_ALWAYS:
                            this.keepAllLocal = false;
                            this.keepAllRemote = true;
                        case CompareDialog.KEEP_REMOTE:

                            // collect ids of entry to be removed
                            removeIds.add(entry);
                            // collect the fetched entries
                            newEntries.add(JabRefModelConverter.convertPost(post));

                            break;

                        case JOptionPane.CANCEL_OPTION:
                        default:
                            return;
                    }
                }
            } catch (Throwable throwable) {
                LOGGER.error("error during synchronization", throwable);
            }

            this.jabRefFrame.output("Synchronized " + entry.getCiteKey());

        }

        // remove the entries not needed anymore from the database
        removeIds.forEach(bibDatabase::removeEntry);

        // add the new entries
        newEntries.forEach(bibDatabase::insertEntry);

        this.keepAllRemote = false;
        this.keepAllLocal = false;

        this.jabRefFrame.output("Done.");
    }
}
