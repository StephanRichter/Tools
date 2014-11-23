package de.srsoftware.tools;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class SuggestField extends JTextField implements KeyListener, ActionListener {

	private static final long serialVersionUID = 4183066803864491291L;
	private static PrefixTree dictionary = null;
	private static File dictionaryFile = new File(System.getProperty("user.home") + "/.config/dictionary");
	private static Charset charset = Charset.forName("UTF-8");

	public static void save() throws IOException {
		if (dictionary == null) return;
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictionaryFile), charset));
		for (String line : dictionary.getAll()) {
			bw.write(line + "\n");
		}
		bw.close();
		// System.out.println("Suggestsions saved.");
	}

	private JPopupMenu suggestionList;
	private int selectionIndex = -1;

	private static int maxNumberOfSuggestions = 20;

	private Point pos = null;

	private Vector<String> suggestions;

	public SuggestField() {
		this(true);
	}

	public SuggestField(boolean ignoreCase) {
		super();
		try {
			if (dictionary == null) loadSuggestions(ignoreCase);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addKeyListener(this);
		suggestionList = new JPopupMenu();
		// suggestionList.addKeyListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		// System.out.println("action in "+e.getSource().getClass().getSimpleName());
		if (e.getSource() instanceof JMenuItem) {
			JMenuItem item = (JMenuItem) e.getSource();
			String text = getText();
			int len = lastWord(text).length();
			setText(text.substring(0, text.length() - len) + item.getText());
			hidePopup();
		}
	}

	public void keyPressed(KeyEvent e) {}

	public void keyReleased(KeyEvent e) {
		char keyChar = e.getKeyChar();
		if (keyChar == KeyEvent.CHAR_UNDEFINED) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case 38: // up
				suggestionList.requestFocus();
				selectionIndex--;
				if (selectionIndex < 0) selectionIndex = suggestions.size() - 1;
				break;
			case 40: // down
				suggestionList.requestFocus();
				selectionIndex++;
				if (selectionIndex == suggestions.size()) selectionIndex = 0;
				break;
			}
		} else {
			if (selectionIndex < 0) {
				hidePopup();
			} else {
				useSuggestion(keyChar);
			}
			String lastword = lastWord(getText());
			suggestFor(lastword);
		}
		if (getCaretPosition() < getText().length()) {
			hidePopup();
		}
	}

	public void keyTyped(KeyEvent e) {}

	private void hidePopup() {
		suggestionList.setVisible(false);
	}

	private String lastWord(String text) {
		if (text==null) return null;
		text = text.trim();		
		int i = text.length();
		if (i<1) return null;
		while (i-- > 1) {
			char c = text.charAt(i - 1);
			if (!Character.isLetter(c) && c != '-') {
				break;
			}
		}
		return text.substring(i).trim();
	}

	private void loadSuggestions(boolean ignoreCase) throws IOException {
		dictionary = new PrefixTree(ignoreCase);
		if (dictionaryFile.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFile), charset));
			String line = null;
			while ((line = br.readLine()) != null)
				dictionary.add(line);
			br.close();
		}
	}

	private void suggestFor(String text) {
		if (text==null){
			return;
		}
		TreeMap<Integer, Vector<String>> map = new TreeMap<Integer, Vector<String>>(); // maps from lengths l to suggestions with length l
		suggestions = dictionary.get(text);
		int minLength = text.length() + 1;
		for (String suggestion : suggestions) {
			suggestion = suggestion.trim();
			int len = suggestion.length();
			if (len < minLength) continue;
			Vector<String> list = map.get(len);
			if (list == null) {
				list = new Vector<String>();
				map.put(len, list);
			}
			list.add(suggestion);
		}

		TreeSet<String> filtered = Tools.stringSet();
		for (Vector<String> suggestionsOfSameLength : map.values()) {
			for (String s : suggestionsOfSameLength) {
				filtered.add(s);
				if (filtered.size() >= maxNumberOfSuggestions) break;
			}
			if (filtered.size() >= maxNumberOfSuggestions) break;
		}

		suggestions.clear();
		suggestions.addAll(filtered);

		if (suggestions.isEmpty()) {
			hidePopup();
		} else {
			suggestionList.removeAll();
			for (String suggestion : suggestions) {
				JMenuItem item = new JMenuItem(text + suggestion.substring(text.length()));
				item.addActionListener(this);
				item.addKeyListener(this);
				suggestionList.add(item);
			}
			selectionIndex = -1;
			pos = getCaret().getMagicCaretPosition();
			if (pos != null) {
				suggestionList.show(this, pos.x - 10, pos.y + 20);
			}
		}
	}

	private void useSuggestion(char c) {
		System.out.println("useSuggestion(" + c + ")");
		if (!suggestionList.isVisible()) return;
		if (selectionIndex > -1) {
			String text = getText();
			text = text.substring(0, text.length() - 1);
			int len = lastWord(text).length();
			String ins = suggestions.get(selectionIndex).substring(len) + c;
			setText(text + ins);
			hidePopup();
		}
	}
}
