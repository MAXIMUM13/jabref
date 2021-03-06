package net.sf.jabref.gui.bibtexkeypattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;

import net.sf.jabref.Globals;
import net.sf.jabref.gui.BasePanel;
import net.sf.jabref.gui.undo.NamedCompound;
import net.sf.jabref.gui.undo.UndoableKeyChange;
import net.sf.jabref.gui.worker.AbstractWorker;
import net.sf.jabref.logic.bibtexkeypattern.BibtexKeyPatternUtil;
import net.sf.jabref.logic.l10n.Localization;
import net.sf.jabref.model.database.BibDatabase;
import net.sf.jabref.model.entry.BibEntry;

/**
 * Function for resolving duplicate BibTeX keys.
 */
public class SearchFixDuplicateLabels extends AbstractWorker {

    private final BasePanel panel;
    private Map<String, List<BibEntry>> dupes;


    public SearchFixDuplicateLabels(BasePanel panel) {

        this.panel = panel;
    }

    @Override
    public void run() {
        // Find all multiple occurrences of BibTeX keys.
        dupes = new HashMap<>();

        Map<String, BibEntry> foundKeys = new HashMap<>();
        BibDatabase db = panel.getDatabase();
        for (BibEntry entry : db.getEntries()) {
            entry.getCiteKeyOptional().filter(key -> !key.isEmpty()).ifPresent(key -> {
                // See whether this entry's key is already known:
                if (foundKeys.containsKey(key)) {
                    // Already known, so we have found a dupe. See if it was already found as a dupe:
                    if (dupes.containsKey(key)) {
                        // Already in the dupe map. Add this entry as well:
                        dupes.get(key).add(entry);
                    } else {
                        // Construct a list of entries for this key:
                        List<BibEntry> al = new ArrayList<>();
                        // Add both the first one we found, and the one we found just now:
                        al.add(foundKeys.get(key));
                        al.add(entry);
                        // Add the list to the dupe map:
                        dupes.put(key, al);
                    }
                } else {
                    // Not already known. Add key and entry to map:
                    foundKeys.put(key, entry);
                }
            });
        }
    }

    @Override
    public void init() throws Throwable {
        panel.output(Localization.lang("Resolving duplicate BibTeX keys..."));

    }

    @Override
    public void update() {
        List<BibEntry> toGenerateFor = new ArrayList<>();
        for (Map.Entry<String, List<BibEntry>> dupeEntry : dupes.entrySet()) {
            ResolveDuplicateLabelDialog rdld = new ResolveDuplicateLabelDialog(panel, dupeEntry.getKey(), dupeEntry.getValue());
            rdld.show();
            if (rdld.isOkPressed()) {
                List<JCheckBox> cbs = rdld.getCheckBoxes();
                for (int i = 0; i < cbs.size(); i++) {
                    if (cbs.get(i).isSelected()) {
                        // The checkbox for entry i has been selected, so we should generate a new key for it:
                        toGenerateFor.add(dupeEntry.getValue().get(i));
                    }
                }
            } else if (rdld.isCancelPressed()) {
                break;
            }
        }

        // Do the actual generation:
        if (!toGenerateFor.isEmpty()) {
            NamedCompound ce = new NamedCompound(Localization.lang("Resolve duplicate BibTeX keys"));
            for (BibEntry entry : toGenerateFor) {
                String oldKey = entry.getCiteKeyOptional().orElse(null);
                BibtexKeyPatternUtil.makeLabel(panel.getBibDatabaseContext().getMetaData()
                        .getCiteKeyPattern(Globals.prefs.getBibtexKeyPatternPreferences().getKeyPattern()),
                        panel.getDatabase(), entry,
                        Globals.prefs.getBibtexKeyPatternPreferences());
                ce.addEdit(new UndoableKeyChange(entry, oldKey, entry.getCiteKeyOptional().get()));
            }
            ce.end();
            panel.getUndoManager().addEdit(ce);
            panel.markBaseChanged();
        }
        panel.output(Localization.lang("Finished resolving duplicate BibTeX keys. %0 entries modified.",
                String.valueOf(toGenerateFor.size())));
    }
}
