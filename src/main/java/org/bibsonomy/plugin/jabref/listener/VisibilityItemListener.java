package org.bibsonomy.plugin.jabref.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.bibsonomy.plugin.jabref.BibsonomyProperties;
import org.bibsonomy.plugin.jabref.gui.GroupingComboBoxItem;

/**
 * {@link VisibilityItemListener} saves the current value of "import posts from..." combo box
 *
 * @author Waldemar Biller <biller@cs.uni-kassel.de>
 */
public class VisibilityItemListener implements ItemListener {

    public void itemStateChanged(ItemEvent e) {
        GroupingComboBoxItem item = (GroupingComboBoxItem) e.getItem();
        BibsonomyProperties.setSidePaneVisibilityType(item.getKey());
        BibsonomyProperties.setSidePaneVisibilityName(item.getValue());

        BibsonomyProperties.save();
    }

}
