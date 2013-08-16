package de.srsoftware.tools.language;

import java.util.TreeMap;

import javax.swing.Action;

import de.srsoftware.tools.ObjectComparator;



public abstract class LanguagePack {
	Action EXPORT_TO_ONE_FILE = null;
	protected TreeMap<String,String> map;
	
	public LanguagePack() {
		map=new TreeMap<String, String>(ObjectComparator.get());
	}
	
	public String get(String key){
		return map.get(key);
	}
	
	public String get(String key,String replacement){
		String result=get(key).replace("##", replacement);
		if (result==null) return key;
		return result;
	}
}
