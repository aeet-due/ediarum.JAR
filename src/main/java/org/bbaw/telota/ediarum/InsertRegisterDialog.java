package org.bbaw.telota.ediarum;

import org.korpora.aeet.ediarum.WrappableBulletList;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serial;
import java.util.HashMap;
import java.util.stream.IntStream;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 * open a dialog to select a register entry.
 * <p>
 * The class belongs to package org.bbaw.telota.ediarum for the modification of the Oxygen framework
 * for several projects at the Berlin-Brandenburgische Akademie der Wissenschaften (BBAW) to build a
 * framework for edition projects (Ediarum - die Editionsarbeitsumgebung).
 *
 * @author Martin Fechner
 * @version 1.1.3
 */
public class InsertRegisterDialog extends JDialog {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -190895918216985737L;

    /**
     * Dies sind die Parameter für die (maximale) Fenstergröße des Dialogs.
     */
    private final static int H_SIZE = 600;
    private final static int V_SIZE = 2048;

    /**
     * Dies ist das Auswahlfeld mit den Registereinträgen.
     */
    private final JList<String> registerListe;
    /**
     * enthält alle Registereinträge.
     */
    private final String[] registerItems;
    /**
     * Enthält die IDs zu den Registereinträgen.
     */
    private final String[] registerIDs;
    /**
     * enthält die ID des ausgewählten Eintrags.
     */
    private String registerID = "";
    private String[] selectedRegisterIDs;
    /**
     * enthält nur gefilterte Einträge mit ihren Originalpositionen.
     */
    private final HashMap<Integer, Integer> filterVerweise = new HashMap<>();
    private Boolean setFilter = false;
    private final JTextField globalEingabeFeld;

    /**
     * Der Konstruktor der Klasse erzeugt einen Dialog zum Auswählen eines Registereintrags.
     *
     * @param parent  Das übergeordnete Fenster
     * @param eintrag Ein Array, das alle Registereinträge enthält
     * @param id      Ein Array, das die IDs zu den Registereinträgen enthält
     */
    public InsertRegisterDialog(Frame parent, String[] eintrag, String[] id, boolean multipleSelection) {
        // Calls the parent telling it this dialog is modal(i.e true)
        super(parent, true);
        // Für den Dialog wird das Layout (North, South, .., Center) ausgewählt und der Titel gesetzt.
        setLayout(new BorderLayout());
        setTitle("Eintrag auswählen");

        Font fontWithSpecialCharacters = new Font("Noto Serif", Font.PLAIN, 14);

        // Oben wird ein Eingabefeld erzeugt, mit welchem man zu den Einträgen springen kann.
        JTextField eingabeFeld = new JTextField();
        globalEingabeFeld = eingabeFeld;
        eingabeFeld.getDocument().addDocumentListener(new eingabeFeldListener());
        eingabeFeld.setColumns(28);
        eingabeFeld.requestFocus();
        JToggleButton doFilteringButton = new JToggleButton();
        doFilteringButton.setText("Filtern");
        doFilteringButton.addChangeListener(new FilterChangeListener());
        Panel panelNorth = new Panel();
        panelNorth.setLayout(new BorderLayout());
        panelNorth.add(eingabeFeld, BorderLayout.WEST);
        panelNorth.add(doFilteringButton, BorderLayout.EAST);
        add("North", panelNorth);

        // Die Einträge werden initialisiert.
        registerItems = eintrag;
        registerListe = new WrappableBulletList<>(new DefaultListModel<>());
        filterRegisterListe("");
        // In der Mitte wird das Auswahlfeld mit den Registereinträgen erzeugt, ..
        if (multipleSelection) {
            registerListe.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        } else {
            registerListe.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        registerListe.setFont(fontWithSpecialCharacters);
        // Hier wird ein Listener eingefügt, der bei Doppelklick bestätigt.
        registerListe.addMouseListener(new RegisterListeMouseListener());
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(registerListe);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add("Center", scrollPane);
        // .. während die zugehörigen IDs in der entsprechenden Variable hinterlegt werden.
        registerIDs = id;

        // Unten gibt es die zwei Knöpfe "Ok" (als Default) und "Abbrechen".
        Panel panel = new Panel();
        JButton ok = new JButton("Ok");
        ok.addActionListener(arg0 -> okAction());
        panel.add(ok);
        JButton cancel = new JButton("Abbrechen");
        cancel.addActionListener(arg0 -> cancelAction());
        panel.add(cancel);
        add("South", panel);
        getRootPane().setDefaultButton(ok);

//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Die Eigenschaften des Dialogfensters werden angepasst: die Größe, der Ort in der Bildschirmmitte, die Schließaktion und die Sichtbarkeit.
        setSize(H_SIZE, V_SIZE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Bei "Ok" wird der aktuelle Registereintrag gemerkt und das Fenster geschlossen.
     */
    public void okAction() {
        registerID = registerIDs[filterVerweise.get(registerListe.getSelectedIndex() - 1)];
        int[] selectedIndices = registerListe.getSelectedIndices();
        selectedRegisterIDs = new String[selectedIndices.length];
        for (int i = 0; i < selectedIndices.length; i++) {
            selectedRegisterIDs[i] = registerIDs[filterVerweise.get(selectedIndices[i] - 1)];
        }
        dispose();
    }

    /**
     * Bei "Cancel" wird das Fenster nur geschlossen.
     */
    public void cancelAction() {
        dispose();
    }

    /**
     * Diese Klasse ist dem Eingabefeld zugeordnet.
     *
     * @author fechner
     */
    class eingabeFeldListener implements DocumentListener {

        @Override
        public void changedUpdate(DocumentEvent e) {
            handleTextChange(e);
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            handleTextChange(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            handleTextChange(e);
        }

        /**
         * Wenn etwas im Textfeld eingegeben wird, wird diese Methode aufgerufen.
         */
//		@Override
        private void handleTextChange(DocumentEvent e) {
            // Das Textfeld und sein momentaner Inhalt werden gelesen.
            try {
                String eingabe;
                eingabe = e.getDocument().getText(0, e.getDocument().getLength()).toLowerCase();
                // Wenn gefiltert werden soll ..
                if (setFilter) {
                    // .. wird dies getan, ..
                    filterRegisterListe(eingabe);
                } else {
                    // .. sonst wird zum entsprechenden Eintrag gesprungen.
                    goToItem(eingabe);
                }
            } catch (BadLocationException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    class FilterChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent changeEvent) {
            AbstractButton abstractButton = (AbstractButton) changeEvent.getSource();
            ButtonModel buttonModel = abstractButton.getModel();
//	        boolean armed = buttonModel.isArmed();
//	        boolean pressed = buttonModel.isPressed();
            boolean selected = buttonModel.isSelected();
            setFilter = selected;
            String eingabe;
            try {
                eingabe = globalEingabeFeld.getDocument().getText(0, globalEingabeFeld.getDocument().getLength())
                        .toLowerCase();
                if (!selected) {
                    filterRegisterListe("");
                    goToItem(eingabe);
                } else {
                    filterRegisterListe(eingabe);
                }
            } catch (BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            globalEingabeFeld.requestFocus();
        }

    }

    class RegisterListeMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                okAction();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

    }

    /**
     * Gibt die ID des ausgewählten Eintrags zurück, nachdem der Dialog mit "Ok" beendet wurde.
     *
     * @return Die ausgewählte ID
     */
    public String getSelectedID() {
        return registerID;
    }

    /**
     * Gibt die IDs der ausgewählten Einträge zurück, nachdem der Dialog mit "Ok" beendet wurde.
     *
     * @return Die ausgewählten IDs
     */
    public String[] getSelectedIDs() {
        return selectedRegisterIDs;
    }

    // Alternativer Ansatz
    private void filterRegisterListe(String eingabe) {
        filterVerweise.clear();
        DefaultListModel<String> registerListModel = (DefaultListModel<String>) registerListe.getModel();
        registerListModel.clear();
        for (int j = 0; j < registerItems.length; j++) {
            if (registerItems[j].toLowerCase().contains(eingabe.toLowerCase())) {
                filterVerweise.put(filterVerweise.size() - 1, j);
                registerListModel.addElement(registerItems[j]);
            }
        }
    }

    private void goToItem(String eingabe) {
        // Registereintrag suchen, dessen Anfang mit dem Text übereinstimmt
        int index = IntStream.range(0, registerListe.getModel().getSize())
                .filter(i -> registerListe.getModel().getElementAt(i).toLowerCase().startsWith(eingabe)).findFirst()
                .orElse(-1);
        // Falls ein Eintrag gefunden wurde, wird dieser ausgewählt, sonst wird nichts ausgewählt.
        registerListe.setSelectedIndex(index);
        registerListe.ensureIndexIsVisible(index);
    }
}
