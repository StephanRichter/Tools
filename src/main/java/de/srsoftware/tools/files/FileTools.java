package de.srsoftware.tools.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.swing.JOptionPane;

import de.keawe.tools.translations.Translation;

public class FileTools {
	
	private static long searchTime = 0;
	
	private static boolean checkSearchTime() {
		if (searchTime == -1) return false;
		if (searchTime == 0) {
			searchTime = (new Date()).getTime();
		} else {
			if ((new Date()).getTime() - searchTime > 20000) {
				if (JOptionPane.showConfirmDialog(null, Translation.get(FileTools.class,"Seems like your search will last longer. Cancel search?"), Translation.get(FileTools.class,"Notification"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					searchTime = -1;
					return false;
				}
				searchTime = (new Date()).getTime();
			}
		}
		return true;
	}
	
	public static void createDirectory(String fname) {
		File f = new File(fname);
		if (!f.exists()) f.mkdirs();
	}
	
	public static String deleteNonFilenameChars(String string) {
		return string
				.replaceAll(":", "")
				.replaceAll("/", "-")
				.replaceAll("\\?", " ")
				.replaceAll(" \\.", ".")
				.replaceAll("\\. ", ".");
	}
	
	public static boolean fileExists(URL fileUrl) {
		String s = fileUrl.toString();
		if (isLocal(fileUrl)) {
			int i = s.indexOf(":");
			s = s.substring(i + 1);
			File f = new File(s);
			return f.exists();
		} else {
			try {
				System.out.print("Waiting for connection to " + fileUrl + "...");
				HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
				int code = connection.getResponseCode();
				if (code == 200) {
					System.out.println("established.");
					return true;
				}
				System.out.println("failed.");
				return false;
			} catch (IOException e) {
				System.out.println("failed.");
			}
			return false;
		}
	}
	
	@SuppressWarnings("deprecation")
	private static URL findIntern(String path,String...names) {
		if (checkSearchTime()) {
			File f = new File(path);
			if (f.exists()) {
				File[] files = f.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file != null && file.isDirectory()) {
							URL result = findIntern(file.getPath(),names);
							if (result != null) return result;
						} else
							for (String filename : names) {
								if (file.getName().toLowerCase().equals(filename.toLowerCase())) {
									try {
										searchTime = 0;
										return file.toURL();
									} catch (MalformedURLException e) {
										e.printStackTrace();
									}
								}
							}
					}
				}
			}
			return null;
		}
		return null;
	}
	
	/**
	 * Tries to read the file denoted by the given URL into a string
	 * 
	 * @param url the url to be read from
	 * @param breakLines
	 * @return the content of the document specified through url
	 * @throws IOException if connection cannot be established
	 */
	public static String getFileString(URL url, boolean breakLines) throws IOException {
		InputStream inputStream;
		BufferedReader reader;
		StringBuffer result = new StringBuffer();
		inputStream = url.openStream(); // Open connection to file fore reading
		reader = new BufferedReader(new InputStreamReader(inputStream));
		while (reader.ready()) {
			if (breakLines) {
				result.append(reader.readLine().trim() + '\n'); // connect content line by line
			} else {
				result.append(reader.readLine().trim()); // connect content
			}
			if (!reader.ready()) { // if reading online content, this delay shall prevent aborting before end of file is reached
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
		}
		reader.close();
		return result.toString();
	}
	

	
	public static boolean isFreeMindFile(URL fileUrl) {
		return fileUrl.toString().toUpperCase().endsWith(".MM");
	}

	public static boolean isIntelliMindFile(URL fileUrl) {
		return (fileUrl.toString().toUpperCase().endsWith(".IMF") || fileUrl.toString().toUpperCase().endsWith(".IMF.OLD"));
	}
	
	public static boolean isKeggUrl(URL fileUrl) {
		String dummy = fileUrl.toString();
		if (dummy.startsWith("http://rest.kegg.jp/get/")) return true;
		return dummy.startsWith("http://www.genome.jp/dbget-bin");
	}

	public static boolean isLocal(URL fileUrl) {
		return fileUrl.toString().startsWith("file:");
	}
	
	public static URL searchFiles(String path,String[] names) {
		searchTime = 0;
		return findIntern(path,names);
	}
}
