package de.srsoftware.tools;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.keawe.tools.translations.Translation;

/**
 * stellt ein grafisches Programmelement zur Verfügung, dass einen kleinen, aus Buttons bestehenden Kalender darstellt, aus dem ein Tag gewählt werden kann.
 * 
 * @author Stephan Richter
 * 
 */
public class DateChooser extends JPanel implements ActionListener, MouseListener {
	private static final long serialVersionUID = 8240006199572579622L;
	private static String _(String text) { 
		return Translation.get(DateChooser.class,text);
	}
	private Vector<JButton> dateButtons = new Vector<JButton>(); // für jeden Tag im Monat und die Überlappungen wird später je ein Button erzeugt, diese Buttons werden in der Liste verwaltet
	private TreeSet<ActionListener> actionListeners = new TreeSet<ActionListener>(); // die Menge der Objekte, die für eine Benachrichtigung über DatumÄnderiungen vorgesehen sind
	private JButton selectedButton = null;// speichert einen Zeiger auf den aktuell ausgewählten Button
	private JPanel buttonPanel, yearPanel; // Gruppieren die Tages-Auswahl-Buttons sowie die Buttonf für das Jahr
	private JLabel yearLabel, monthLabel; // Textfelder für die Anzeige des augewählten Jahres und Monats
	private int year, month, firstDay; // speichert intern das gewählte Jahr, den gewählen Monat und den gewählten Tag
	private String toolTipText; // Speichert den Hilfetext

	private JButton lastMonth, nextMonth, lastYear, nextYear; // Knöpfe zum Eintellen von Monat und Jahr

	private String helpText = null;

	/**
	 * Erzeugt eine neue Instanz, die dann der grafischen Oberfläche eines Programmes hinzugefügt werden kann
	 */
	@SuppressWarnings("deprecation")
	public DateChooser() {
		Date date = new Date(); // aktuelles Datum bestimmen
		year = date.getYear() + 1900; // Date zählt ab dem 1.0.1900, also Differenz hinzuaddieren
		month = date.getMonth() + 1; // Monat wird von 0-11 gezählt

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // die Komponenten yearPanel und buttonPanel sollen untereinander angeordnet werden

		yearPanel = new JPanel(); // erzeugt ein leeres Panel, dem gleich Buttons für Jahres- und Monatsauswahl hinzugefügt werden

		lastMonth = new JButton("<"); // Button um einen Monat zurückzuspringen anlegen, registrieren und zur Oberfläche hinzufügen
		lastMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				decreaseMonth();
			}
		});
		yearPanel.add(lastMonth);

		monthLabel = new JLabel(getMonth(month)); // Textfeld für die Ausgabe des gewählten Monats erzeugen und hinzufügen
		yearPanel.add(monthLabel);

		nextMonth = new JButton(">"); // Button um einen Monat weiter zu springen anlegen, registrieren und zur Oberfläche hinzufügen
		nextMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				increaseMonth();
			}
		});
		yearPanel.add(nextMonth);

		lastYear = new JButton("<"); // Button um eine Jahr zurückzuspringen anlegen, registrieren und zur Oberfläche hinzufügen
		lastYear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				year--;
				resetMonth();
			}
		});
		yearPanel.add(lastYear);

		yearLabel = new JLabel(String.valueOf(year)); // Textfeld für die Ausgabe des gewählten Jahres erzeugen und hinzufügen
		yearPanel.add(yearLabel);

		nextYear = new JButton(">"); // Button um ein Jahr weiter zu springen anlegen, registrieren und zur Oberfläche hinzufügen
		nextYear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				year++;
				resetMonth();
			}
		});
		yearPanel.add(nextYear);

		add(yearPanel); // Panel mit den Buttons für die Jahres- und Monatsauswahl zur Oberfläche hinzufügen

		buttonPanel = new JPanel(); // leeres Panel für die Knöpfe zur Auswahl eines Tages erzeugen
		buttonPanel.setLayout(new GridLayout(7, 7)); // die Knöpfe, die später hinzugefügt werden, sollen in 7 Zeilen zu 6 Spalten angeordnet werden
		addDays();
		add(buttonPanel); // Panel für die Knöpfe hinzufügen

		setMonth(); // die Buttons entsprechend dem in der variable Month gespeicherten Monat zum buttonPanel hinzufügen
		selectButton(dateButtons.get(date.getDate() + firstDay - 2)); // den Button auswählen, der dem aktuell gewählten Tag entspricht

		this.setBorder(BorderFactory.createEtchedBorder()); // einen Rahmen um das gesamte Element erzeugen
	}

	/**
	 * diese Methode wird ausgelöst, wenn ein Button des aktuellen Monats geklickt wird und bewirkt die Auswahl des geklickten Buttons und das Setzen des entsprechenden Datums
	 */
	public void actionPerformed(ActionEvent arg0) {
		JButton sender = (JButton) arg0.getSource(); // bestimmt den geklickten Button
		selectButton(sender); // veranlasst die optische Markierung des Buttons und das neusetzen des gewählten Datums
	}

	/**
	 * registriert eine Komponente zur Benachrichtigung bei Änderung des gewählten Datums
	 * 
	 * @param l die Komponente, die benachrichtigt werden soll, wenn sich das gewählte Datum ändert
	 */
	public void addActionListener(ActionListener l) {
		actionListeners.add(l);
	}
	
	/**
	 * liefert zur Auswahl das entsprechende Date-Objekt
	 * 
	 * @return das Date-Objet, welches das gewählte Datum repräsentiert
	 */
	@SuppressWarnings("deprecation")
	public Date getSelectedDate() {
		if (selectedButton == null) return null; // wenn kein Tages-Button ausgewählt wurde, wird auch kein Datum zurückgegeben
		return new Date(year - 1900, month - 1, Integer.parseInt(selectedButton.getText())); // anderenfalls wird das dem gewählten Knopf entsprechende Datum erzuegt und ausgegeben
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3 && helpText != null) JOptionPane.showMessageDialog(this, helpText);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	/**
	 * setzt den Kalender auf das angegebene Datum
	 * 
	 * @param day der Tag
	 * @param month der Monat
	 * @param year das Jahr
	 */
	public void setDate(int day, int month, int year) {
		this.month = month; // setzt die internen Werte
		this.year = year;
		resetMonth(); // erzeugt die dem Monat und Jahr entsprechenden Buttons
		selectButton(dateButtons.get(day + firstDay - 2)); // wählt den Button, der dem gegebenen Tag entspricht
	}

	public void setHelpText(String message) {
		helpText = message;
		addMouseListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setToolTipText(java.lang.String)
	 */
	public void setToolTipText(String text) {
		buttonPanel.setToolTipText(text);
		yearPanel.setToolTipText(text);
		yearLabel.setToolTipText(text);
		monthLabel.setToolTipText(text);
		for (int i = 0; i < dateButtons.size(); i++) {
			dateButtons.get(i).setToolTipText(text);
		}
		toolTipText = text;
		lastMonth.setToolTipText(text);
		nextMonth.setToolTipText(text);
		lastYear.setToolTipText(text);
		nextYear.setToolTipText(text);
	}

	/**
	 * fügt einen Button für einen Tag im aktuellen Monat zum buttonPanel hinzu, oder falls der aktuelle Monat nicht mit einem Montag begann auch für einen "Füll"-Tag des vorhergehenden Monats
	 * 
	 * @param dateButton der Tages-Button
	 * @param relativeMonth 0, falls der Tag dem aktuell gewählten Monat angehört, -1, falls es sich um einen "Füll"-Tag des vorhergehenden Monats handelt und 1, falls es ein "Füll"-Tag des folgenden Monats ist
	 */
	private void addDateButton(JButton dateButton, int relativeMonth) {
		if (relativeMonth == 0) {
			dateButton.addActionListener(this); // für Tage des aktuellen Monats ist nur die Registrierung bei der Ereignisüberwachung notwendig
		} else { // für Tage vor oder nach dem aktuellen Monat:
			dateButton.setForeground(Color.GRAY); // andere Farbe festlegen
			if (relativeMonth < 0) { // für Tage des vorhergehenden Monats: funktion hinzufügen, um beim Auslösen einen Monat zurückzuspringen
				dateButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int day = Integer.parseInt(((JButton) e.getSource()).getText());
						decreaseMonth();
						selectButton(dateButtons.get(day + firstDay - 2));
					}
				});
			} else { // für Tage des folgenden Monats: funktion hinzufügen, um beim Auslösen einen Monat weiter zu springen
				dateButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int day = Integer.parseInt(((JButton) e.getSource()).getText());
						increaseMonth();
						selectButton(dateButtons.get(day + firstDay - 2));
					}
				});
			}
		}
		if (toolTipText != null) dateButton.setToolTipText(toolTipText); // Hilfetexte hinzufügen
		buttonPanel.add(dateButton); // Button der Oberfläche hinzufügen
		dateButtons.add(dateButton); // Button in einer Liste speichern, um ihn später wieder löschen zu können
	}

	private void addDay(String d) {
		JLabel l = new JLabel(d);
		buttonPanel.add(l);
	}

	private void addDays() {
		addDay(_(" Mo"));
		addDay(_(" Tu"));
		addDay(_(" We"));
		addDay(_(" Th"));
		addDay(_(" Fr"));
		addDay(_(" Sa"));
		addDay(_(" Su"));
	}

	/**
	 * Gibt zu einer Monatszahl den entsprechenden Monatsnamen zurück
	 * 
	 * @param month die Nummer des Monats (1 - 12)
	 * @return den Namen des Monats
	 */
	private String getMonth(int month) {
		switch (month) {
		case 1:
			return _("January");
		case 2:
			return _("February");
		case 3:
			return _("March");
		case 4:
			return _("April");
		case 5:
			return _("May");
		case 6:
			return _("June");
		case 7:
			return _("July");
		case 8:
			return _("August");
		case 9:
			return _("September");
		case 10:
			return _("October");
		case 11:
			return _("November");
		}
		return _("Dezember");
	}

	/**
	 * entfernt alle Buttons aus dem Tages-Auswahlfeld und veranlasst die zum aktuellen Monat gehörenden Buttons hinzuzufügen
	 */
	private void resetMonth() {
		yearLabel.setText(String.valueOf(year)); // Textfeld mit dem Jahr aktualisieren
		monthLabel.setText(getMonth(month)); // Textfeld mit dem Monat aktualisieren
		for (int i = dateButtons.size() - 1; i >= 0; i--)
			buttonPanel.remove(dateButtons.get(i)); // alle Buttons aus dem Tages-Auswahl-Feld entfernen
		dateButtons.clear();
		setMonth(); // neue Buttons hinzufügen
	}

	/**
	 * ändert die optische Hervorhebung des aktuell ausgewählten Buttons und setzt das Datum entsprechend
	 * 
	 * @param newSelected der Knopf der per Klick ausgewählt wurde
	 */
	private void selectButton(JButton newSelected) {
		newSelected.setEnabled(false); // ein gewählter Knopf kann nicht mehr gewählt werden, wird deshalb deaktiviert
		if (selectedButton != null) { // ein zuvor gewählter Knopf wird reaktiviert und dessen Hervorhebung entfernt
			selectedButton.setBackground(newSelected.getBackground());
			selectedButton.setEnabled(true);
		}
		newSelected.setBackground(Color.BLUE); // der neu gewählte Button wird blau gefärbt
		selectedButton = newSelected; // der neu gewählte Button wird gemerkt, um ihn später wieder zu reaktivieren

		ActionEvent ae = new ActionEvent(this, 0, null); // eine Benachrichtigung über die Änderung des gewählten Datums wird erzeugt und an alle registrierten Komponenten weitergeleitet
		Iterator<ActionListener> it = actionListeners.iterator();
		while (it.hasNext())
			it.next().actionPerformed(ae);
	}

	/**
	 * erzuegt für den in der Variable month gesetzten Monat die entsprechenden Tages-Auswahl-Buttons
	 */
	@SuppressWarnings("deprecation")
	private void setMonth() {
		Date date = new Date(year - 1900, month - 1, 1); // erzeugt ein Datumsobjekt zum ersten Tag es Monats, in dem der ausgewählte Tag liegt
		int diff = 86400000; // Zahl der Millisekunden eines Tages = Differenz der Millisekunden zweier benachbarter Tage
		int prefix = -1; // 
		int counter = 0;
		while (date.getDay() != 1)
			// bestimmt den ersten Montag vor dem ersten Tag des Monats in welchem der gewählte Tag liegt. Der erste Knopf im Tages-Auswahl-Bereich soll immer ein Montag sein
			date = new Date(date.getTime() - diff);
		for (int i = 0; i < 42; i++) { // fügt einen Button für den gefundenen Montag und die 41 folgenden Tage (also inklusive dem gewählten Monat) zum Panel hinzu
			counter++;
			int day = date.getDate();
			if (day == 1) {
				prefix++;
				if (prefix == 0) firstDay = counter;
			}
			addDateButton(new JButton(String.valueOf(day)), prefix); // hier wird der Button für den bestimmten Tag erzeugt und hinzugefügt
			date.setDate(day + 1);
		}
	}

	/**
	 * diese Methode wird beim Klicken des Buttons ausgelöst, mit welchem ein Monat zurück gesprungen wird
	 */
	protected void decreaseMonth() {
		month--; // den Monats-Zähler um 1 verringern
		if (month < 1) { // wenn dadurch der Monat "0" erreicht wird zum Monat 12 springen und den Jahreszähler verringern
			month = 12;
			year--;
		}
		resetMonth(); // die Buttons entsprechend dem gewählten Monat anzeigen
	}

	/**
	 * diese Methode wird beim Klicken des Buttons ausgelöst, mit welchem ein Monat vorwärts gesprungen wird
	 */
	protected void increaseMonth() {
		month++; // den Monats-Zähler um 1 erhöhen
		if (month > 12) { // wenn dadurch der Monat "13" erreicht wird zum Monat 1 springen und den Jahreszähler erhöhen
			month = 1;
			year++;
		}
		resetMonth(); // die Buttons entsprechend dem gewählten Monat anzeigen
	}

}
