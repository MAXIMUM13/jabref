package org.bibsonomy.plugin.jabref.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;

import net.sf.jabref.gui.JabRefFrame;
import net.sf.jabref.logic.l10n.Localization;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.plugin.jabref.BibsonomyProperties;
import org.bibsonomy.plugin.jabref.gui.GroupingComboBoxItem;
import org.bibsonomy.plugin.jabref.worker.RefreshTagListWorker;
import org.bibsonomy.plugin.jabref.worker.UpdateVisibilityWorker;

/**
 * {@link RefreshTagListAction} runs the {@link RefreshTagListWorker} to refresh the tag cloud
 *
 * @author Waldemar Biller <biller@cs.uni-kassel.de>
 */
public class RefreshTagListAction extends AbstractBibsonomyAction {

    private static List<GroupingComboBoxItem> defaultGroupings;

    private JEditorPane tagCloud;

    private JComboBox<? super GroupingComboBoxItem> groupingComboBox;

    public void actionPerformed(ActionEvent e) {
        // refresh the tag cloud
        RefreshTagListWorker worker = new RefreshTagListWorker(getJabRefFrame(), tagCloud, ((GroupingComboBoxItem) groupingComboBox.getSelectedItem()).getKey(), ((GroupingComboBoxItem) groupingComboBox.getSelectedItem()).getValue());
        performAsynchronously(worker);

        // update the "import posts from.." combo box
        UpdateVisibilityWorker visWorker = new UpdateVisibilityWorker(getJabRefFrame(), groupingComboBox, getDefaultGroupings());
        performAsynchronously(visWorker);
    }

    public RefreshTagListAction(JabRefFrame jabRefFrame, JEditorPane tagCloud, JComboBox<? super GroupingComboBoxItem> groupingComboBox) {
        super(jabRefFrame, Localization.lang("Refresh"), new ImageIcon(RefreshTagListAction.class.getResource("/images/images/arrow-circle-225.png")));
        this.tagCloud = tagCloud;

        this.groupingComboBox = groupingComboBox;
    }

    private static List<GroupingComboBoxItem> getDefaultGroupings() {
        if (defaultGroupings == null) {
            defaultGroupings = new ArrayList<>();
            defaultGroupings.add(new GroupingComboBoxItem(GroupingEntity.ALL, "all users"));
            defaultGroupings.add(new GroupingComboBoxItem(GroupingEntity.USER, BibsonomyProperties.getUsername()));
        }
        return defaultGroupings;
    }
}
