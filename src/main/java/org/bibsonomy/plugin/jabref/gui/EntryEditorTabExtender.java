package org.bibsonomy.plugin.jabref.gui;

import net.sf.jabref.logic.l10n.Localization;
import net.sf.jabref.preferences.JabRefPreferences;

import org.bibsonomy.plugin.jabref.BibsonomyGlobals;
import org.bibsonomy.plugin.jabref.BibsonomyProperties;


/**
 * {@link EntryEditorTabExtender} extends the {@link net.sf.jabref.gui.entryeditor.EntryEditor EntryEditor} with custom tabs.
 * @author Waldemar Biller <biller@cs.uni-kassel.de>
 *
 */
public class EntryEditorTabExtender {

	public static void extend() {
		
		boolean generalTab = false, bibsonomyTab = false, extraTab = false;
		int lastTabId = 0, extraTabID = -1;
		
		JabRefPreferences preferences = JabRefPreferences.getInstance();
		if(preferences.hasKey(JabRefPreferences.CUSTOM_TAB_NAME)) {
			
			
			while(preferences.hasKey(JabRefPreferences.CUSTOM_TAB_NAME + lastTabId)) {
				//TODO: check for alternatives - zellerdev
				if(preferences.get(JabRefPreferences.getCustomTypeName() + lastTabId).equals(Localization.lang("General")))
					generalTab = true;
				
				if(preferences.get(JabRefPreferences.CUSTOM_TAB_NAME + lastTabId).equals(BibsonomyGlobals.PLUGIN_NAME))
					bibsonomyTab = true;
				
				if("Extra".equals(preferences.get(JabRefPreferences.CUSTOM_TAB_NAME + lastTabId))) {
					extraTab = true; extraTabID = lastTabId;
				}
				
				lastTabId++;
			}
		}
		
		if(!generalTab) {
			
			preferences.put(JabRefPreferences.CUSTOM_TAB_FIELDS + lastTabId, "crossref;file;doi;url;citeseerurl;comment;owner;timestamp");
			preferences.put(JabRefPreferences.CUSTOM_TAB_NAME + lastTabId, Localization.lang("General"));
			lastTabId++;
		}
		
		if (!bibsonomyTab) {
			preferences.put(JabRefPreferences.CUSTOM_TAB_FIELDS + lastTabId, "interhash;intrahash;keywords;groups;privnote");
			preferences.put(JabRefPreferences.CUSTOM_TAB_NAME + lastTabId, "Bibsonomy");
			lastTabId++;
		}

		if (!extraTab) {
			preferences.put(JabRefPreferences.CUSTOM_TAB_FIELDS + lastTabId, BibsonomyProperties.getExtraTabFields());
			preferences.put(JabRefPreferences.CUSTOM_TAB_NAME + lastTabId, "Extra");
		}

		if (extraTab) {
			if (!preferences.get(JabRefPreferences.CUSTOM_TAB_FIELDS + extraTabID).equals(
					BibsonomyProperties.getExtraTabFields())) {
				preferences.put(JabRefPreferences.CUSTOM_TAB_FIELDS + extraTabID,
						BibsonomyProperties.getExtraTabFields());
			}
		}
	}
}
