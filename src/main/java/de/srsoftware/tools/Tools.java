package de.srsoftware.tools;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.TreeSet;

import de.srsoftware.tools.translations.Translation;
import de.srsoftware.tools.files.FileName;
import de.srsoftware.tools.files.FileTools;

public class Tools {
	public static TreeSet<String> oldMessages = new TreeSet<String>();
	
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
	
	private static int count(char c, String text) {
		int result = 0;
		for (int i = 0; i < text.length(); i++)
			if (text.charAt(i) == c) result++;
		return result;
	}

	public static void execute(String command) {
		// System.out.println("execute: " + command);
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
	
	public static void execute(URL url) {
		execute(FileTools.isLocal(url) ? url.getFile() : url.toString());
	}
	
	public static String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
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

	public static String insertTab(String text) {
		return "  " + text.replace("\n", "\n  ");
	}

	public static boolean isWindows() {
		String osName = ((String) System.getProperty("os.name"));
		return (osName.startsWith("Windows"));
	}

	public static String listStringArray(String[] array) {
		String result = "(";
		for (int i = 0; i < array.length; i++)
			result += array[i] + ",";
		return result.substring(0, result.length() - 1) + ")";
	}
	
	static void linuxExecute(String command) throws IOException {
		command = "gnome-open " + command;
		Runtime.getRuntime().exec(command);
	}

	public static void message(String msg) {
		if (!oldMessages.contains(msg)) {
			oldMessages.add(msg);
			System.out.println(msg);
		}
	}

	public static void notImplemented(String method) {
		System.out.println(Translation.get(Tools.class, "The method \"{}\" has not been implemented, yet.", method));
	}

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(String url) {
		try {
			openWebpage((new URL(url)).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static void pause(int secs) {
		try {
			Thread.sleep(1000 * secs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void printArray(Object[] lines) {
		System.out.println(lines + ":");
		for (int i = 0; i < lines.length; i++)
			System.out.println(lines[i] + " ");

	}

	public static void printStack() {
		try {
			Thread.dumpStack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readNextTag(BufferedReader fileReader) throws IOException {
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

	public static String removeHtml(String line) {
		int open = line.indexOf("<");
		while (open > -1) {
			int close = line.indexOf(">", open);
			if ((close > -1) && (close + 1 < line.length())) {
				line = line.substring(0, open) + line.substring(close + 1);
			} else
				line = line.substring(0, open);
			open = line.indexOf("<");
		}
		return line.replace("&lt;", "<");
	}

	public static double round(double d, int nachkommastellen) {
		double factor = Math.pow(10, nachkommastellen);
		return Math.round(factor * d) / factor;
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

	public static String shortest(String[] strings) {
		String result = strings[0];
		int len = result.length();
		for (int i = 1; i < strings.length; i++) {
			if (strings[i].length() < len) {
				result = strings[i];
				len = result.length();
			}
		}
		return result;
	}

	public static String spaces(int i) {
		StringBuffer sb = new StringBuffer();
		for (; i > 0; i--)
			sb.append(' ');
		return sb.toString();
	}

	public static void trace(Object source, String method, Object data) {
		if (source == null) {
			System.out.println("(static) " + method + " => " + data);
		} else {
			System.out.println(source.getClass().toString().substring(6) + "." + method + " => " + data);
		}
	}

	@SuppressWarnings("deprecation")
	public static String Uhrzeit() {
		Date d = new Date();
		return d.toLocaleString();
	}

	static void windowsExecute(String command) throws IOException {
		Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + command);
	}
}
