package de.srsoftware.tools.files;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class GenericFileFilter extends FileFilter {
	String[] extensions = null;
	String desc;

	public GenericFileFilter(String description, String type) {
		super();
		desc = description;
		if (type == null) {
			extensions = null;
		} else {
			extensions = type.split(";");
			for (int i = 0; i < extensions.length; i++) {
				if (extensions[i].startsWith("*")) extensions[i] = extensions[i].substring(1);
				if (extensions[i].startsWith(".")) extensions[i] = extensions[i].substring(1);
				extensions[i] = extensions[i].toLowerCase();
			}
		}
	}

	public boolean accept(File f) {
		if ((f.isDirectory()) || (extensions == null)) return true;
		for (int i = 0; i < extensions.length; i++) {
			if (f.getName().toLowerCase().endsWith(extensions[i])) return true;
		}
		return false;
	}
	
	public String extension(int index) {
		return extensions[index];
	}
	
	public String[] extensions() {
		return extensions;
	}
	
	public String getDescription() {
		return desc;
	}
}
