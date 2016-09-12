package org.bibsonomy.plugin.jabref.listener;

import java.awt.Component;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.jabref.gui.BasePanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * {@link TabbedPaneChangeListener} add a ChangeListener to the Database.
 * @author Waldemar Biller <biller@cs.uni-kassel.de>
 *
 */
public class TabbedPaneChangeListener implements ChangeListener {
	private static final Log log = LogFactory.getLog(TabbedPaneChangeListener.class);

	private PluginDataBaseChangeListener databaseChangeListener;

	public void stateChanged(ChangeEvent e) {
		if(e.getSource() instanceof JTabbedPane) {
			JTabbedPane pane = (JTabbedPane) e.getSource();
			Component[] components = pane.getComponents();
			for (Component component : components) {
				BasePanel basePanel = (BasePanel) component;
				if (basePanel.getDatabase() != null) {
					basePanel.getDatabase().registerListener(databaseChangeListener);
				} else {
					log.warn("found tab-component without database");
				}
			}
			if (components.length == 0) {
				log.info("pane has no tab-components");
			}
		}
	}
	
	public TabbedPaneChangeListener(PluginDataBaseChangeListener l) {
		this.databaseChangeListener = l;
	}

}
