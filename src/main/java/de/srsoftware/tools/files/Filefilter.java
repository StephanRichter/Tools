package de.srsoftware.tools.files;

import java.io.File;
import java.io.FileFilter;


public class Filefilter implements FileFilter {
	private String name=null;
	public  Filefilter(String filename){
		name=filename;
	}

	public boolean accept(File arg0) {
		return arg0.getName().startsWith(name);
	}

}
