package de.srsoftware.tools.urls;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import de.srsoftware.tools.files.FileTools;

public class Urls {
	public static URL fix(URL fileUrl) {
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
						// System.out.println("corrected " + s);
						return fix(new URL("file:" + s));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return fileUrl;
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
	

	
	private static String lcs(String a, String b) {
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
	
	/*
	 * creates an absolute url for a relative link situated in a document e.g: baseDocument = file:///dir1/dir2/document outgoingLink = ../Link result => file:///dir1/Link
	 */
	public static URL getURLto(String baseDocument, String outgoingLink) throws MalformedURLException {
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
			String lcs = lcs(outgoingLink, baseDocument);
			if (lcs != null && lcs.length() > 0) {
				outgoingLink = outgoingLink.substring(outgoingLink.lastIndexOf(lcs));
				calculatedLink = baseDocument.substring(0, baseDocument.lastIndexOf(lcs)) + outgoingLink;
				result = new URL(calculatedLink);
			}
		}
		if (FileTools.isLocal(result) && !FileTools.fileExists(result)) {
			return guessRightCase(result);
		} // */
		return result;
	}
	

	public static URL guessRightCase(URL file) throws MalformedURLException {
		String filename = file.toString();
		if (FileTools.isLocal(file)) {
			if (filename.startsWith("file:")) filename = filename.substring(5);
			while (filename.startsWith("//"))
				filename = filename.substring(1);
			return new URL("file://" + guessRightCase(filename).toString());
		}
		return file;
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
}
