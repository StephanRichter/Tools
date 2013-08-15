package tools.srsoftware;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

public class Tools {
	public static String lastSelectedFile = ""; // speichert, welche Datei zuletzt mit einem Dialog geöffnet wurde, um beim nächsten Aufruf des Dialogs im gleichen Ordner zu starten
	public static TreeSet<String> oldMessages = new TreeSet<String>();
	private static JFileChooser FileDialog;
	private static long searchTime = 0;
	public static String language="German";

	public static void notImplemented(String method) {
		System.out.println(Messages.notImplemented.replace("%method", method));
	}

	public static String insertTab(String text) {
		return "  " + text.replace("\n", "\n  ");
	}

	/**
	 * Zeigt einen Datei-Öffnen-Dialog mit Titel <i>title</i>, Startverzeichnis <i>defDir</i> und Dateityp <i>fileType</i> an *
	 */
	public static URL showSelectFileDialog(String title, String fileName, GenericFileFilter fileType, Component owner) {
		if (fileName == null) fileName = lastSelectedFile;
		URL result = null;
		JFileChooser FileDialog = new JFileChooser();
		FileDialog.setDialogTitle(title);
		FileDialog.setFileFilter(fileType);
		FileDialog.setSelectedFile(new File(fileName));
		int returnVal = FileDialog.showOpenDialog(owner);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				result = new URL("file:" + FileDialog.getSelectedFile().getPath());
				lastSelectedFile = FileDialog.getSelectedFile().getPath();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				System.out.println("Die angegebene Zeichenkette ist keine gültige URL!");
			}
		}
		return result;
	}

	public static URL showUrlInputDialog(Component owner, String text) {
		// TODO Auto-generated method stub
		URL result = null;
		try {
			result = new URL(JOptionPane.showInputDialog(owner, text, ""));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("Die angegebene Zeichenkette ist keine gültige URL!");
		}
		return result;
	}

	public static boolean fileIsLocal(URL fileUrl) {
		return fileUrl.toString().startsWith("file:");
	}

	public static boolean fileExists(URL fileUrl) {
		String s = fileUrl.toString();
		if (fileIsLocal(fileUrl)) {
			int i = s.indexOf(":");
			s = s.substring(i + 1);
			File f = new File(s);
			return f.exists();
		} else {
			try {
				HttpURLConnection connection = (HttpURLConnection)fileUrl.openConnection();
				return (connection.getResponseCode()==200);
			} catch (IOException e) {}
			return false;
		}
	}

	public static boolean fileIsIntelliMindFile(URL fileUrl) {
		// TODO Auto-generated method stub
		return (fileUrl.toString().toUpperCase().endsWith(".IMF") || fileUrl.toString().toUpperCase().endsWith(".IMF.OLD"));
	}

	public static boolean fileIsFreeMindFile(URL fileUrl) {
		// TODO Auto-generated method stub
		return fileUrl.toString().toUpperCase().endsWith(".MM");
	}

	public static void recodeFile(URL fileUrl) {
	// TODO Auto-generated method stub
	}

	public static Color lookupColor(String name) {
		if (name.equals("clAqua")) return new Color(0, 255, 255);
		if (name.equals("clBlack")) return Color.BLACK;
		if (name.equals("clBlue")) return Color.BLUE;
		if (name.equals("clMaroon")) return new Color(128, 0, 0);
		if (name.equals("clGreen")) return Color.GREEN;
		if (name.equals("clOlive")) return new Color(128, 128, 128);
		if (name.equals("clNavy")) return new Color(0, 0, 128);
		if (name.equals("clPurple")) return new Color(128, 0, 128);
		if (name.equals("clTeal")) return new Color(128, 0, 128);
		if (name.equals("clGray")) return new Color(128, 128, 128);
		if (name.equals("clSilver")) return new Color(192, 192, 192);
		if (name.equals("clRed")) return Color.RED;
		if (name.equals("clLime")) return new Color(0, 255, 0);
		if (name.equals("clYellow")) return Color.YELLOW;
		if (name.equals("clFuchsia")) return new Color(255, 0, 255);
		if (name.equals("clWhite")) return Color.WHITE;
		return Color.BLACK;
	}

	public static void message(String msg) {
		if (!oldMessages.contains(msg)) {
			oldMessages.add(msg);
			System.out.println(msg);
		}
	}

	public static URL fixUrl(URL fileUrl) {
		String s = fileUrl.toString();
		if (!s.startsWith("file:")) return fileUrl;
		s = s.substring(5);

		File f = new File(s);
		if (f.exists()) return fileUrl;
		while (f.getParentFile() != null && !f.exists()) {
			s = f.getAbsolutePath();
			f = f.getParentFile();
		}
		if (f.exists()) {
			File[] subs = f.listFiles();
			for (int i = 0; i < subs.length; i++) {
				String dir = subs[i].toString();
				if (subs[i].isDirectory() && dir.toLowerCase().equals(s.toLowerCase())) {
					s = dir + fileUrl.toString().substring(5 + dir.length());
					try {
						//System.out.println("corrected " + s);
						return fixUrl(new URL("file:" + s));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return fileUrl;
	}

	public static String lcs(String a, String b) {
		if (a == null || b == null) return "";
		int l1 = a.length();
		int l2 = b.length();
		if (l1 == 0 || l2 == 0) return "";
		int maxLen = 0;
		String result = "";
		for (int i = 0; i < l1; i++) {
			for (int j = 0; j < l2; j++) {
				if (a.charAt(i) == b.charAt(j)) {
					int n = 0;
					while (i + n < l1 && j + n < l2 && a.charAt(i + n) == b.charAt(j + n)) {
						n++;
					}
					if (n > maxLen) {
						maxLen = n;
						result = a.substring(i, i + n);
					}
				}
			}
		}
		return result;
	}

	public static File guessRightCase(String filename) {
		File f = new File(filename);
		if (!f.exists()) {
			File parent = guessRightCase(f.getParent());
			String searchTerm = f.getName();
			if (parent.exists()) {
				File[] list = parent.listFiles();
				for (int i = 0; i < list.length; i++) {
					if (list[i].getName().equalsIgnoreCase(searchTerm)) return list[i];
				}
			}
		}
		return f;
	}

	public static URL guessRightCase(URL file) throws MalformedURLException {
		String filename = file.toString();
		if (fileIsLocal(file)) {
			if (filename.startsWith("file:")) filename = filename.substring(5);
			while (filename.startsWith("//"))
				filename = filename.substring(1);
			return new URL("file://" + guessRightCase(filename).toString());
		}
		return file;
	}

	public static String saveDialog(Component owner, String title, String filename, GenericFileFilter fileType) {
		Vector<FileFilter> filter=new Vector<FileFilter>();
		filter.add(fileType);
		return saveDialog(owner,title,filename,filter);
	}

	public static String getRelativePath(URL baseUrl, URL link) {
		String lnk = link.toString();
		String base = baseUrl.toString();
		int i = 0;
		int l = Math.min(base.length(), lnk.length());
		int lastSlash = 0;
		while (i < l && base.charAt(i) == lnk.charAt(i)) {
			if (base.charAt(i) == '/') lastSlash = i;
			i++;
		}
		base = base.substring(lastSlash);
		lnk = lnk.substring(lastSlash);
		while (base.startsWith("/"))
			base = base.substring(1);
		while (lnk.startsWith("/"))
			lnk = lnk.substring(1);
		if (i > 0) for (i = 0; i < base.length(); i++)
			if (base.charAt(i) == '/') lnk = "../" + lnk;
		return lnk;
	}

	/*
	 * creates an absolute url for a relative link situated in a document e.g: baseDocument = file:///dir1/dir2/document outgoingLink = ../Link result => file:///dir1/Link
	 */
	public static URL getURLto(String baseDocument, String outgoingLink) throws MalformedURLException {
		// TODO Auto-generated method stub
		// Tools.message("Trying to construct URL for " + outgoingLink);
		String calculatedLink = outgoingLink;
		URL result = null;
		if (!outgoingLink.contains(":") && !outgoingLink.startsWith("/")) {
			calculatedLink = baseDocument.substring(0, baseDocument.lastIndexOf("/") + 1) + outgoingLink;
			int i = calculatedLink.indexOf("..");
			while (i >= 0) {
				int j = calculatedLink.substring(0, i - 2).lastIndexOf("/");
				if (j >= 0) {
					calculatedLink = calculatedLink.substring(0, j) + calculatedLink.substring(i + 2);
					i = calculatedLink.indexOf("..");
				} else
					i = j;
			}
		}
		try {
			result = new URL(calculatedLink);
		} catch (MalformedURLException e) {
			String lcs = Tools.lcs(outgoingLink, baseDocument);
			if (lcs != null && lcs.length() > 0) {
				outgoingLink = outgoingLink.substring(outgoingLink.lastIndexOf(lcs));
				calculatedLink = baseDocument.substring(0, baseDocument.lastIndexOf(lcs)) + outgoingLink;
				result = new URL(calculatedLink);
			}
		}
		if (!fileExists(result) && fileIsLocal(result)) {
			return guessRightCase(result);
		}// */
		return result;
	}

	private static FileFilter createFolderFilter() {
		FileFilter result = new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory();
			}

			public String getDescription() {
				return "Ordner";
			}
		};
		return result;
	}

	private static JFileChooser createFolderChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		FileFilter filter = createFolderFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setMultiSelectionEnabled(false);
		return chooser;
	}

	public static String selectFolder() {
		JFileChooser folderChooser = createFolderChooser();
		if (folderChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return folderChooser.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	static void windowsExecute(String command) throws IOException {
		Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + command);
	}

	static void linuxExecute(String command) throws IOException {
		command = "gnome-open " + command;
		Runtime.getRuntime().exec(command);
	}

	public static void execute(URL url) {
		if (Tools.fileIsLocal(url)) {
			execute(url.getFile());
		} else
			execute(url.toString());
	}

	public static boolean isWindows() {
		String osName = ((String) System.getProperty("os.name"));
		return (osName.startsWith("Windows"));
	}

	public static void execute(String command) {
		//System.out.println("execute: " + command);
		if (command.charAt(0) == '"') {
			int i = command.indexOf('"', 1);
			command = command.substring(1, i);
			String param = (command.length() > i) ? command.substring(i + 1) : null;
			if (!command.startsWith("http") && !new File(command).exists()) {
				command = FileName.searchFileUpward(command, 6);
			}
			command = '"' + command + '"';
			if (param != null) command += param;
		} else {
			if (!command.startsWith("http") && !new File(command).exists()) {
				command = FileName.searchFileUpward(command, 6);
			}
		}
		try {
			if (isWindows()) {
				windowsExecute(command);
			} else {
				int pos = 0;
				while (pos < command.length()) {
					if (command.charAt(pos) == ' ') {
						command = command.substring(0, pos) + "%20" + command.substring(pos + 1);
						pos++;
					}
					pos++;
				}
				if (command.startsWith("\"http://")) {
					command = command.substring(1, command.length() - 1);
				} else if (!command.startsWith("http://")) {
					command = "file://" + command;
				}
				linuxExecute(command);
			}
		} catch (Exception ex) {}
	}

	public static String colorToString(Color c) {
		String dummy = Integer.toHexString(c.getBlue());
		if (dummy.length() < 2) dummy = "0" + dummy;
		String result = dummy;
		dummy = Integer.toHexString(c.getGreen());
		if (dummy.length() < 2) dummy = "0" + dummy;
		result = result + dummy;

		dummy = Integer.toHexString(c.getRed());
		if (dummy.length() < 2) dummy = "0" + dummy;
		result = result + dummy;

		return "$00" + result.toUpperCase();
	}

	public static String colorToXmlString(Color c) {
		String dummy = Integer.toHexString(c.getRed());
		if (dummy.length() < 2) dummy = "0" + dummy;
		String result = dummy;
		dummy = Integer.toHexString(c.getGreen());
		if (dummy.length() < 2) dummy = "0" + dummy;
		result = result + dummy;

		dummy = Integer.toHexString(c.getBlue());
		if (dummy.length() < 2) dummy = "0" + dummy;
		result = result + dummy;

		return "#" + result.toUpperCase();
	}

	public static String shorten(String s) {
		if (s == null) return s;
		int i = 80;
		int l = s.length();
		while (i < l) {
			s = s.substring(0, i) + "\n" + s.substring(i);
			i += 80;
		}
		return s;
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
				result.append(reader.readLine().trim() + '\n'); // connect content
				// line by line
			} else {
				result.append(reader.readLine().trim()); // connect content
			}
			if (!reader.ready()) { // if reading online content, this delay shall
				// prevent aborting before end of file is reached
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
			}
		}
		return result.toString();
	}

	public static String readNextTag(BufferedReader fileReader) throws IOException {
		// TODO Auto-generated method stub
		char c;
		do {
			if (!fileReader.ready()) return null;
			c = (char) fileReader.read();
		} while (c != '<');
		boolean quote1 = false;
		boolean quote2 = false;
		String result = "";
		while (fileReader.ready() && (c != '>' || quote1 || quote2)) {
			if (c == '"') quote1 = !quote1;
			if (c == '\'') quote2 = !quote2;
			result += c;
			c = (char) fileReader.read();
		}
		return result + '>';
	}

	public static String getTagProperty(String tag, String key) {
		int i = tag.indexOf(" " + key + "=");
		if (i < 0) return null;
		i += key.length() + 2;
		if (i >= tag.length()) return "";
		boolean quote1 = false;
		boolean quote2 = false;
		String result = "";
		char c;
		do {
			c = tag.charAt(i);
			if (c == '"') quote1 = !quote1;
			if (c == '\'') quote2 = !quote2;
			result += c;
			i++;
		} while (i < tag.length() && (c != ' ' || quote1 || quote2));
		result = result.trim();
		if (result.endsWith(">")) result = result.substring(0, result.length() - 1);
		result = result.trim();
		if (result.endsWith("/")) result = result.substring(0, result.length() - 1);
		result = result.trim();
		if (result.startsWith("\"") && result.endsWith("\"")) result = result.substring(1, result.length() - 1);
		return result;
	}

	public static String htmlToUnicode(String text) {
		// TODO Auto-generated method stub
		if (text.startsWith("&lt;html&gt;")) text = text.substring(12);
		text = text.replace("&lt;b&gt;", "\\bold{");
		text = text.replace("&lt;i&gt;", "\\it{");
		text = text.replace("&lt;u&gt;", "\\underline{");
		text = text.replace("&lt;/i&gt;", "}");
		text = text.replace("&lt;/u&gt;", "}");
		text = text.replace("&lt;br&gt;&#xa;", "\\n ");
		text = text.replace("&lt;br&gt;", "\\n ");
		text = text.replace("&lt;/b&gt;", "}");
		text = text.replace("&lt;sup&gt;", "\\^{");
		text = text.replace("&lt;/sup&gt;", "}");
		text = text.replace("&#xdf;", "ß");
		text = text.replace("&#xe4;", "ä").replace("&#xdc;", "Ü").replace("&quot;", "\"");
		text = text.replace("&#xf6;", "ö").replace("&#xa;", "\\n ").replace("&#xfc;", "ü").replace("&lt;", "<").replace("&gt;", ">");

		text = text.replace("&amp;#1108;", "\\in ");
		text = text.replace("&amp;#1108", "\\in ");

		text = text.replace("&amp;#8594;", "\\-> ");
		text = text.replace("&amp;#8594", "\\-> ");

		text = text.replace("&amp;#8800;", "\\neq ");
		text = text.replace("&amp;#8800", "\\neq ");

		text = text.replace("&amp;#8596;", "\\<-> ");
		text = text.replace("&amp;#8596", "\\<-> ");

		text = text.replace("&amp#8593;", "\\uparrow ");
		text = text.replace("&amp#8593", "\\uparrow ");

		text = text.replace("&amp;lt;", "<");
		text = text.replace("&amp;gt;", ">");
		text = text.replace("&apos;", "'");
		text = text.replace("<font face=\"Arial\">", "");
		text = text.replace("<font color=\"red\">", "\\rgb{ff0000,");
		text = text.replace("<font color=\"black\">", "\\rgb{000000,");
		text = text.replace("<font color=\"blue\">", "\\rgb{0000ff,");
		text = text.replace("<font color=\"green\">", "\\rgb{009900,");
		text = text.replace("<font face=\"Courier new\">", "\\type{");
		text = text.replace("</font>", "}");
		text = text.replace("&amp;", "&");
		text = text.replace("&nbsp;", " ");
		if (Tools.count('{', text) > Tools.count('}', text)) text += ' ';
		return text;

	}

	private static int count(char c, String text) {
		// TODO Auto-generated method stub
		int result = 0;
		for (int i = 0; i < text.length(); i++)
			if (text.charAt(i) == c) result++;
		return result;
	}

	public static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	public static String deleteNonFilenameChars(String string) {
		// TODO Auto-generated method stub
		return string.replaceAll(":", "").replaceAll("/", "-").replaceAll("\\?", " ").replaceAll(" \\.", ".").replaceAll("\\. ", ".");
	}

	@SuppressWarnings("deprecation")
  private static URL internSearchFiles(String[] names, String path) {
		if (checkSearchTime()) {
			System.out.print(".");
			File f = new File(path);
			if (f.exists()) {
				File[] files = f.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file != null && file.isDirectory()) {
							URL result = internSearchFiles(names, file.getPath());
							if (result != null) return result;
						} else
							for (String filename : names) {
								if (file.getName().toLowerCase().equals(filename.toLowerCase())) {
									try {
										searchTime = 0;
										return file.toURL();
									} catch (MalformedURLException e) {
										// TODO Auto-generated catch block
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

	public static String listStringArray(String[] array) {
		String result = "(";
		for (int i = 0; i < array.length; i++)
			result += array[i] + ",";
		return result.substring(0, result.length() - 1) + ")";
	}

	public static URL searchFiles(String[] names, String path) {
		searchTime = 0;
		return internSearchFiles(names, path);
	}

	private static boolean checkSearchTime() {
		// TODO Auto-generated method stub
		if (searchTime == -1) return false;
		if (searchTime == 0) {
			searchTime = (new Date()).getTime();
		} else {
			if ((new Date()).getTime() - searchTime > 20000) {
				if (JOptionPane.showConfirmDialog(null, "Die Suche scheint länger zu dauern. Suche Abbrechen?\nSeems like your search will last longer. Cancel search?", "Hinweis", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					searchTime = -1;
					return false;
				}
				searchTime = (new Date()).getTime();
			}
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	public static String Uhrzeit() {
		Date d = new Date();
		return d.toLocaleString();
	}

	public static void pause(int secs) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(1000 * secs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static double round(double d, int nachkommastellen) {
		double factor = Math.pow(10, nachkommastellen);
		return Math.round(factor * d) / factor;
	}

	private static void addCharEntity(Integer aIdx, StringBuilder aBuilder) {
		String padding = "";
		if (aIdx <= 9) {
			padding = "00";
		} else if (aIdx <= 99) {
			padding = "0";
		}
		String number = padding + aIdx.toString();
		aBuilder.append("&#" + number + ";");
	}

	public static String escapeHtmlEntities(String text) {
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(text);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '&') {
				result.append("&amp;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\t') {
				addCharEntity(9, result);
			} else if (character == '!') {
				addCharEntity(33, result);
			} else if (character == '#') {
				addCharEntity(35, result);
			} else if (character == '$') {
				addCharEntity(36, result);
			} else if (character == '%') {
				addCharEntity(37, result);
			} else if (character == '\'') {
				addCharEntity(39, result);
			} else if (character == '(') {
				addCharEntity(40, result);
			} else if (character == ')') {
				addCharEntity(41, result);
			} else if (character == '*') {
				addCharEntity(42, result);
			} else if (character == '+') {
				addCharEntity(43, result);
			} else if (character == ',') {
				addCharEntity(44, result);
			} else if (character == '-') {
				addCharEntity(45, result);
			} else if (character == '.') {
				addCharEntity(46, result);
			} else if (character == '/') {
				addCharEntity(47, result);
			} else if (character == ':') {
				addCharEntity(58, result);
			} else if (character == ';') {
				addCharEntity(59, result);
			} else if (character == '=') {
				addCharEntity(61, result);
			} else if (character == '?') {
				addCharEntity(63, result);
			} else if (character == '@') {
				addCharEntity(64, result);
			} else if (character == '[') {
				addCharEntity(91, result);
			} else if (character == '\\') {
				addCharEntity(92, result);
			} else if (character == ']') {
				addCharEntity(93, result);
			} else if (character == '^') {
				addCharEntity(94, result);
			} else if (character == '_') {
				addCharEntity(95, result);
			} else if (character == '`') {
				addCharEntity(96, result);
			} else if (character == '{') {
				addCharEntity(123, result);
			} else if (character == '|') {
				addCharEntity(124, result);
			} else if (character == '}') {
				addCharEntity(125, result);
			} else if (character == '~') {
				addCharEntity(126, result);
			} else {
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();

	}

	public static String saveDialog(Component owner, String title, String filename, Vector<FileFilter> fileFilter) {
		FileDialog=new JFileChooser();
		FileDialog.setDialogTitle(title);
		if (fileFilter.size()>0){
			FileDialog.setFileFilter(fileFilter.get(0));			
			for (int i=1; i<fileFilter.size(); i++)	FileDialog.addChoosableFileFilter(fileFilter.get(i));
		}
		FileDialog.setSelectedFile(new File(filename));
		int returnVal = FileDialog.showSaveDialog(owner);
		String result = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) result = FileDialog.getSelectedFile().getPath();
		if (result != null){
			if  (result.equals("nullnull")) {
				result = null;
			} else if (result.indexOf('.')<0){
				result+="."+((GenericFileFilter)FileDialog.getFileFilter()).ending[0];
			}
		}
		return result;
	}

	public static Color colorComplement(Color bg) {
		//int min=Math.min(bg.getRed(), Math.min(bg.getGreen(), bg.getBlue()));
		//int max=Math.max(bg.getRed(), Math.max(bg.getGreen(), bg.getBlue()));
		//max+=min;
		int treshold=200;
		int red=(bg.getRed()>treshold)?0:255;
		int green=(bg.getGreen()>treshold)?0:255;
		int blue=(bg.getBlue()>treshold)?0:255;
		return new Color(red,green,blue);
		//		return new Color(max-bg.getRed(),max-bg.getGreen(),max-bg.getBlue());
	}

	public static String removeHtml(String line) {
		int open=line.indexOf("<");
		while (open>-1){
			int close=line.indexOf(">",open);
			if ((close>-1) && (close+1<line.length())){
				line=line.substring(0,open)+line.substring(close+1);
			} else line=line.substring(0,open);
			open=line.indexOf("<");
		}
	  return line.replace("&lt;", "<");
  }

	public static void createDirectory(String fname) {
		File f=new File(fname);
		if (!f.exists()) f.mkdirs();	  
  }

	public static void printStack() {
		try {
			Thread.dumpStack();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static String spaces(int i) {
		StringBuffer sb=new StringBuffer();
		for (;i>0;i--) sb.append(' ');
	  return sb.toString();
  }

	public static void trace(Object source, String method, Object data) {
		System.out.println(source.getClass().toString().substring(6)+"."+method+" => "+data);	  
  }

	public static void printArray(Object[] lines) {
	  System.out.println(lines+":");
	  for (int i=0; i<lines.length; i++) System.out.println(lines[i]+" ");
	  
  }

	public static String shortest(String[] strings) {
		String result=strings[0];
		int len=result.length();
		for (int i=1; i<strings.length; i++){
			if (strings[i].length()<len){
				result=strings[i];
				len=result.length();
			}
		}
	  return result;
  }

	public static int screenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
  }

	public static int screenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	public static boolean fileIsKeggUrl(URL fileUrl) {
		String dummy=fileUrl.toString();
		if (dummy.startsWith("http://rest.kegg.jp/get/")) return true;
		return dummy.startsWith("http://www.genome.jp/dbget-bin");
	}

	public static TreeSet<String> stringSet() {
		return new TreeSet<String>(ObjectComparator.get());
	}
}
