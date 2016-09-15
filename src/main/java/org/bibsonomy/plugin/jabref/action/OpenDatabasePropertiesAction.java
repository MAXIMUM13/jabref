package org.bibsonomy.plugin.jabref.action;

import java.awt.event.ActionEvent;

import net.sf.jabref.gui.JabRefFrame;
import net.sf.jabref.gui.dbproperties.DatabasePropertiesDialog;

public class OpenDatabasePropertiesAction extends AbstractBibsonomyAction {

    private static final long serialVersionUID = -5243052886812863636L;

    DatabasePropertiesDialog databasePropertiesDialog = null;

    public OpenDatabasePropertiesAction(JabRefFrame jabRefFrame) {
        super(jabRefFrame);
    }

    public void actionPerformed(ActionEvent e) {
        if (databasePropertiesDialog == null) {
            databasePropertiesDialog = new DatabasePropertiesDialog(getJabRefFrame());
            databasePropertiesDialog.setPanel(getJabRefFrame().getCurrentBasePanel());
        }
        databasePropertiesDialog.setVisible(true);
    }

}
