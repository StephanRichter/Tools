package de.srsoftware.tools;
/** This class provides services to handle filenames, such as absolute-relative path conversion **/
import java.io.File;

import de.keawe.tools.translations.Translation;
public class FileName{
  /**
  * sucht nach der Datei &lt;filename&gt; in allen Unterverzeichnissen des angegebenen Pfades
  **/
  public static String searchFile(String filename){
    FileName f=new FileName(filename);
    String result=searchFile(f.getFileNameFromPath().toUpperCase(),f.getPathNameFromPath());
    return result;
  }
  /**
  * Takes the path from the fully specified filename, goes &lt;levels&gt; directory levels up
  * and searches the file beginning from there.<br>
  * Result:<br>
  * The filname including a path, if successfull, null otherwise
  **/
  public static String searchFileUpward(String filename,int levels){
    System.out.print(Translation.get(FileName.class,"searching for file {}",filename));
    FileName f=new FileName(filename);
    String dir=f.getPathNameFromPath();
    int i=dir.lastIndexOf(sc);
    while (levels>0){
      i--;
      while ((i>0)&&(dir.charAt(i)!=sc)) {
        i--;
      }
      levels--;
    }
    if (i<=0) i=dir.indexOf(sc);
    String result=searchFile(f.getFileNameFromPath().toUpperCase(),dir.substring(0,i+1));
    System.out.println();
    return result;
  };

  /** extracts the equal part of the given paths **/
  private static String getPathMatch(String path1, String path2){
    int i1=path1.indexOf(sc);
    int i2=path2.indexOf(sc);
    String matchPath="";
    while ((i1>-1)&&(i2>-1)&&(path2.substring(0,i2).equals(path1.substring(0,i1)))){
      matchPath=path1.substring(0,i1+1);
      i1=path1.indexOf(sc,i1+1);
      i2=path2.indexOf(sc,i2+1);
    }
    return matchPath;
  }
  
  private static String searchFile(String filename,String directory){
    System.out.print('.');
    String[]list=new File(directory).list();
    if (list==null) return null;
    for (int i=0; i<list.length; i++){
      if (new File(directory+list[i]).isDirectory()){
        String subDirResult=searchFile(filename,directory+list[i]+sc);
        if (subDirResult!=null) return subDirResult;
      } else {
        if (list[i].toUpperCase().equals(filename)) return directory+list[i];
      }
    }
    return null;
  }
  
  private String absoluteFilename;
  
  private static char sc=File.separatorChar;
  
  /** deletes rudiments of relative paths in a filename **/
  private static String CollapseFilename(String f){
    int i=f.indexOf(".."+sc);
    while (i>-1){
      int j=f.substring(0,i-1).lastIndexOf(sc);
      if (i>-1) f=f.substring(0,j+1)+f.substring(i+3);
      i=f.indexOf(".."+sc);
    }
    return f;
  }
  
  /** resets the path-separators dependent on the OS **/
  private static String fixPathSeparators(String path){
    if (sc=='\\'){
      path=path.replace('/',sc);
    } else {
      path=path.replace('\\',sc);
    }
    return path;
  }
  
  /** Constructs a new, empty FileName object **/
  public FileName(){
    absoluteFilename=null;
    sc=File.separatorChar;
  }

  /** Constructs a new FileName object using the given filename **/
  public FileName(String filename){
    int i=filename.indexOf(':');
    if (i>-1){ // found
    	
    	  
      if (filename.startsWith("http://")){
    	filename=filename.substring(0,5)+filename.substring(5).replace(':','#');
      } else if (filename.startsWith("ftp://")){
    	filename=filename.substring(0,4)+filename.substring(4).replace(':','#');
      } else filename=filename.substring(0,2)+filename.substring(2).replace(':','#'); // repace, if not drive descriptor 
    }
    filename=filename.replace('?','#');
    filename=filename.replace('*','#');
    filename=filename.replace('"','#');
    filename=filename.replace('>','#');
    filename=filename.replace('<','#');
    filename=filename.replace('|','#');
    absoluteFilename=CollapseFilename(fixPathSeparators(filename));
    sc=File.separatorChar;
  }
  
  /** returns the filename without any path information **/
  public String getFileNameFromPath(){
    int i=absoluteFilename.lastIndexOf(sc);
    if (i>-1) return absoluteFilename.substring(i+1); else return absoluteFilename;
  }
  /** returns the path without trailing filename **/
  public String getPathNameFromPath(){
    int i=absoluteFilename.lastIndexOf(sc);
    if (i>-1) return absoluteFilename.substring(0,i+1); else return "";
  }
  
  /** returns the filename relative to the File <i>baseFile</i> **/
  public String getPathRelativeToFile(String baseFile){
    FileName baseFileName=new FileName(baseFile);
    String targetFile=getFileNameFromPath();
    String targetPath=getPathNameFromPath();
    String basePath=baseFileName.getPathNameFromPath();
    String matchPath=getPathMatch(basePath,targetPath);
    int matchLength=matchPath.length(); // L�nge der �bereinstimmenden Anteile
    basePath=basePath.substring(matchLength);
    targetPath=targetPath.substring(matchLength);
    String relDir="";
    if (matchLength>0){
      int index=basePath.indexOf(sc);
      while (index>-1){
        relDir=relDir+".."+sc;
        basePath=basePath.substring(index+1);
        index=basePath.indexOf(sc);
      }
    }
    return relDir+targetPath+targetFile;
  }
  
	/** sets the current FileName's filename parameter **/
  public void setFileName(String newFileName){
    absoluteFilename=CollapseFilename(fixPathSeparators(newFileName));
  }
  
  /** returns the absolute filename **/
  public String toString(){
    return absoluteFilename;
  }
}
