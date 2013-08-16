package tools.srsoftware;
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class GenericFileFilter extends FileFilter{
  String [] ending=null;
  String desc;
  
  public GenericFileFilter(String description,String type){
    super();
    desc=description;
    if (type==null){
      ending=null;
    } else {
      ending = type.split(";");
      for (int i=0; i<ending.length; i++){
       	if (ending[i].startsWith("*")) ending[i]=ending[i].substring(1);
      	if (ending[i].startsWith(".")) ending[i]=ending[i].substring(1);
      	ending[i]=ending[i].toLowerCase();
      }
    }
  }
  
  public boolean accept(File f){
    if ((f.isDirectory())||(ending==null)) return true;
    for (int i=0; i<ending.length; i++){
      if (f.getName().toLowerCase().endsWith(ending[i])) return true;
    }
    return false;
  }
  
  public String getDescription(){
    return desc;
  }
}
