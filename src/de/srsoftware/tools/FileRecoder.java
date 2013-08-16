package tools.srsoftware;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
/**
  *
  * Beschreibung
  *
  * @version 1.0 vom 17.05.2007
  * @author Stephan Richter
  */

public class FileRecoder {
  /**
  * Sichert, dass die zu öffnende Datei in UTF-8 kodiert ist. Dazu wird geprüft, ob die
  * angegebene Datei schon ein Flag hat, dass anzeigt, wie die Datei kodiert ist.<br>
  * Ist das Flag nicht vorhanden, oder hat es nicht den Wert "UTF-8", so wird die Datei in
  * UTF-8 umgewandelt.<br>
  * <br>
  * Rückgabewerte:<br>
  * TRUE, falls die Operation erfolgreich war (Datei erfolgreich konvertiert oder war schon UTF-8)<br>
  * FALSE, falls die Datei nicht geöffnet werden konnte, oder ein anderer Fehler auftrat.
  **/
  public static boolean recode(String filename){
  	//System.out.println("trying to recode "+filename);
    boolean isUTF8=false;
    String tempFile=filename+".tmp";
    try {
      InputStreamReader ISR=new InputStreamReader(new FileInputStream(filename),"UTF-8");
      BufferedReader InFile=new BufferedReader(ISR);
      String Line=InFile.readLine();
      if (Line.equals("[Encoding]")){
        isUTF8=InFile.readLine().equals("UTF-8");
      }
      InFile.close();
      if (!isUTF8){
        System.out.print("Kovertiere "+filename+" zu UTF-8...");
        InFile=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"Cp1252"));
        BufferedWriter OutFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile),"UTF-8"));
        OutFile.write("[Encoding]");
        OutFile.newLine();
        OutFile.write("UTF-8");
        OutFile.newLine();
        while (InFile.ready()){
          OutFile.write(InFile.readLine());
          OutFile.newLine();
        }
        OutFile.close();
        InFile.close();
        new File(filename).delete();
        new File(tempFile).renameTo(new File(filename));
        //System.out.println("fertig.");
      }      
    } catch (Exception e){   	
      return false;
    }    	
    return true;
  }
  
  public static boolean recode(URL fileUrl){
  	if (Tools.fileIsLocal(fileUrl)){
  		String s=fileUrl.toString();
  		s=s.substring(s.indexOf(":")+1);
  		return recode(s);
  	}
  	return false;
  }
}

