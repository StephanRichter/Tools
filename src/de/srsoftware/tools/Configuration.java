package de.srsoftware.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Configuration {
	private String configFileName;
	private String name;
	private static TreeMap<String,String> config;
	
	public Configuration(String name) throws IOException {
		this.name=name;
		configFileName = path()+name+".config";
		config=null;
		load();
	}
	
	public String path(){
		return System.getProperty("user.home")+"/.config/"+name+"/";
	}
	
	private void load() throws IOException {
		File configFile=new File(configFileName);
		if (!configFile.exists()) {
			System.err.println("No config file found, creating new config in "+configFileName);
			createDirectory(configFile.getParentFile());
			BufferedWriter bw=new BufferedWriter(new FileWriter(configFile));
			bw.close();
		}
		BufferedReader br=new BufferedReader(new FileReader(configFile));
		config=new TreeMap<String, String>(ObjectComparator.get());
		while (br.ready()){
			String line=br.readLine();
			int comment=line.indexOf('#');
			if (comment>-1) line=line.substring(0,comment);
			int equal=line.indexOf('=');
			if (equal>1){
				String key=line.substring(0,equal).trim();
				String value=line.substring(equal+1).trim();
				config.put(key, value);
			}
		}
		br.close();
  }
	
	public void set(String key,Object value){
		if (value==null) return;
		config.put(key,value.toString());
	}
	
	public String get(String key) throws IOException{
		if (config==null) load();
		return config.get(key);
	}
	
	public void save() throws IOException{
		File configFile=new File(configFileName);
		BufferedWriter bw=null;
		if (!configFile.exists()) {
			System.err.println("No config file found, creating new config in "+configFileName);
			createDirectory(configFile.getParentFile());
			bw=new BufferedWriter(new FileWriter(configFile));
		} else {
			bw=new BufferedWriter(new FileWriter(configFile,true));
		}
		for (Entry<String, String> entry :config.entrySet()){
			bw.write(entry.getKey()+" = "+entry.getValue()+"\n");
		}
		bw.close();
  }
	
	private static void createDirectory(File dir) {
	  if (dir.exists()) return;
	  dir.mkdirs();
  }

	public boolean containsKey(String key) {
	  return config.containsKey(key);
  }
}
