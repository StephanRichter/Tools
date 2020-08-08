package de.srsoftware.tools.gui;

import java.awt.Component;
import java.awt.Toolkit;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import de.keawe.tools.translations.Translation;
import de.srsoftware.tools.Tools;
import de.srsoftware.tools.files.DirectoryFilter;
import de.srsoftware.tools.files.GenericFileFilter;

public class Gui {
	
	public static String lastSelectedFile = ""; // speichert, welche Datei zuletzt mit einem Dialog geöffnet wurde, um beim nächsten Aufruf des Dialogs im gleichen Ordner zu starten

	/**
	 * Zeigt einen Datei-Öffnen-Dialog mit Titel <i>title</i>, Startverzeichnis <i>defDir</i> und Dateityp <i>fileType</i> an *
	 */
	public static URL showSelectFileDialog(String title, String fileName, GenericFileFilter fileType, Component owner) {
		if (fileName == null) fileName = lastSelectedFile;
		URL result = null;
		JFileChooser FileDialog = new JFileChooser();
		FileDialog.setDialogTitle(title);
		if (fileType instanceof DirectoryFilter) {
			FileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			System.out.println("directories!");
		} else {
			FileDialog.setFileFilter(fileType);
			FileDialog.setSelectedFile(new File(fileName));
		}
		int returnVal = FileDialog.showOpenDialog(owner);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				result = new URL("file:" + FileDialog.getSelectedFile().getPath());
				lastSelectedFile = FileDialog.getSelectedFile().getPath();
			} catch (MalformedURLException e) {
				System.out.println(Translation.get(Tools.class,"The given text is not a valid URL!"));
			}
		}
		return result;
	}
	
	public static String selectFolder() {
		JFileChooser folderChooser = createFolderChooser();
		if (folderChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return folderChooser.getSelectedFile().getAbsolutePath();
		}
		return null;
	}
	
	public static URL showUrlInputDialog(Component owner, String text, String preset) {
		URL result = null;
		try {
			result = new URL(JOptionPane.showInputDialog(owner, text, preset));
		} catch (MalformedURLException e) {
			System.out.println(Translation.get(Tools.class,"The given text is not a valid URL!"));
		}
		return result;
	}
	
	public static String saveDialog(Component owner, String title, String filename, GenericFileFilter fileType) {
		Vector<FileFilter> filter = new Vector<FileFilter>();
		filter.add(fileType);
		return saveDialog(owner, title, filename, filter);
	}

	public static String saveDialog(Component owner, String title, String filename, Vector<FileFilter> fileFilter) {
		JFileChooser fileDialog = new JFileChooser();
		fileDialog.setDialogTitle(title);
		if (fileFilter.size() > 0) {
			fileDialog.setFileFilter(fileFilter.get(0));
			for (int i = 1; i < fileFilter.size(); i++)
				fileDialog.addChoosableFileFilter(fileFilter.get(i));
		}
		fileDialog.setSelectedFile(new File(filename));
		int returnVal = fileDialog.showSaveDialog(owner);
		String result = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) result = fileDialog.getSelectedFile().getPath();
		if (result != null) {
			if (result.equals("nullnull")) {
				result = null;
			} else if (result.indexOf('.') < 0) {
				result += "." + ((GenericFileFilter) fileDialog.getFileFilter()).extension(0);
			}
		}
		return result;
	}

	public static int screenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	public static int screenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}
	
	private static JFileChooser createFolderChooser() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		FileFilter filter = createFolderFilter();
		chooser.addChoosableFileFilter(filter);
		chooser.setMultiSelectionEnabled(false);
		return chooser;
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
}
