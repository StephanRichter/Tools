package tools.srsoftware;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;


public class SuggestField extends JTextField implements KeyListener, ActionListener{
	
	private static PrefixTree dictionary=null;
	private static Path dictionaryFile=Paths.get(System.getProperty("user.home")+"/.config/dictionary");
	private static Charset charset =Charset.forName("UTF-8");
	private JPopupMenu suggestionList;
	private int selectionIndex=-1;
	
	public SuggestField() {
		this(true);
	}
	
	public SuggestField(boolean ignoreCase) {
		super();
		try {
			if (dictionary==null) loadSuggestions(ignoreCase);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addKeyListener(this);
		suggestionList=new JPopupMenu();
	}

	private void loadSuggestions(boolean ignoreCase) throws IOException {
		dictionary=new PrefixTree(ignoreCase);
		if (Files.exists(dictionaryFile)){
			BufferedReader br = Files.newBufferedReader(dictionaryFile, charset );
	    String line = null;
	    while ((line = br.readLine()) != null) dictionary.add(line);
	    br.close();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		//System.out.println("keyReleased in "+e.getSource().getClass().getSimpleName());
		switch (e.getKeyCode()){
		case 38:
			suggestionList.requestFocus();
			selectionIndex--;
			if (selectionIndex<0) selectionIndex=suggestions.size()-1;
			//System.out.println(selectionIndex);
			return;
		case 40:
			suggestionList.requestFocus();
			selectionIndex++;
			if (selectionIndex==suggestions.size()) selectionIndex=0;
			//System.out.println(selectionIndex);
			return;
		case 32:
			useSuggestion();
		}
		String text=getText();
		if (text!=null && text.length()>0){
			if (text.endsWith(" ")) {
				hidePopup();
				String newWord=trim(lastWord(text));
				dictionary.add(newWord);
			} else {
				suggestFor(lastWord(text));		
			}
		}
	}

	private String trim(String lastWord) {
		boolean changed=true;
		while (changed){
			changed=false;
			if (lastWord.endsWith("?") || lastWord.endsWith("!") || lastWord.endsWith(".") || lastWord.endsWith(",") || lastWord.endsWith(";") || lastWord.endsWith(":")) {
				lastWord=lastWord.substring(0, lastWord.length()-1);
				changed=true;
			}
			lastWord=lastWord.trim();
		}
		return lastWord;
	}

	private void useSuggestion() {
		if (!suggestionList.isVisible()) return;
		if (selectionIndex>-1){
			String text=getText().trim();
			int len=lastWord(text).length();
			setText(text+suggestions.get(selectionIndex).substring(len)+" ");
			hidePopup();
		}
	}

	private String lastWord(String text) {
		int pos=text.trim().lastIndexOf(' ');
		if (pos>0) return text.substring(pos).trim();
		return text.trim();
	}
	
	private Point pos=null;
	private Vector<String> suggestions;
	
	private void hidePopup(){
		suggestionList.setVisible(false);		
	}

	private void suggestFor(String text) {
		if (text.length()<3) return;
		suggestions = dictionary.get(text);
		if (suggestions.isEmpty()){
			hidePopup();
		} else {
			suggestionList.removeAll();
			for (String suggestion:suggestions)	{
				JMenuItem item = new JMenuItem(text+suggestion.substring(text.length()));
				item.addActionListener(this);
				item.addKeyListener(this);
				suggestionList.add(item);
			}
			selectionIndex=-1;
			pos = getCaret().getMagicCaretPosition();
			suggestionList.show(this, pos.x-10, pos.y+20);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	public static void save() throws IOException {
		if (dictionary==null) return;
		BufferedWriter bw = Files.newBufferedWriter(dictionaryFile, charset);
		for (String line:dictionary.getAll()){
			bw.write(line+"\n");
		}
		bw.close();
		//System.out.println("Suggestsions saved.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//System.out.println("action in "+e.getSource().getClass().getSimpleName());
		if (e.getSource() instanceof JMenuItem){
			JMenuItem item=(JMenuItem) e.getSource();
			String text=getText();
			int len=lastWord(text).length();
			setText(text.substring(0,text.length()-len)+item.getText());
			hidePopup();
		}
	}
}
