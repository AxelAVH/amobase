package de.amo.money;

import de.amo.tools.ConditionChecker;
import de.amo.view.*;
import de.amo.view.table.ATableForm;
import de.amo.view.table.ATableRowSorter;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Created by amo on 22.08.2015.
 */
public class MoneyView extends JFrame {

    public  JPanel              mainPanel;
    public  JPanel              buttonPanel;
    public  JPanel              summaryPanel;

    private ANumberInputField   saldoInput;
    private ANumberInputField   psaldoInput;
    private ANumberInputField   forecastInput;
    private ANumberInputField   pSaldoAmInput;
    private ANumberInputField   zahlungAmInput;
    private AStringInputField   datenbankNameInput;
    private AStringInputField   umsatzDateienUploadDir;

    private AFieldPane zahlungAmPane;
    private AFieldPane psaldoAmPane;
    private AFieldPane umsatzDateienUploadPane;

    public AStringInputField    summaryLabelDatenbankSaved;

    // wirklich nur eine Zeile zum aktuellen Zustand
    private JLabel              statusZeile;

    // hier sollen sich die lfd. Ausgaben hindurch scrollen
    private  JTextArea          messageTextArea;


    AStringInputField filter_kategorie;
    AStringInputField filter_kommentar;
    AStringInputField filter_verwendungszweck;
    AStringInputField filter_buchungstext;
    AStringInputField filter_quelleZiel;
    ANumberInputField filter_betragBis;
    ANumberInputField filter_betragVon;
    ADateInputField   filter_datumBis;
    ADateInputField   filter_datumVon;

    private MyActionListener actionListener;

    private BuchungszeilenTableModel model;

    JButton saveButton, bankDatenEinlesenButton, viewRefreshButton, resetFilterButton, auswertungsButton;

    JTabbedPane tabbedPane;

    private MoneyController     moneyController;


    public MoneyView( MoneyController moneyController) {

        setTitle("Money - Umsatzverwaltung für ein Bankkonto");

        tabbedPane = new JTabbedPane();

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.yellow);
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        Dimension dim = new Dimension(1200,800);
        mainPanel.setPreferredSize(dim);
        actionListener = new MyActionListener();

        mainPanel.add(createSummaryPanel());

        model = new BuchungszeilenTableModel(Fachwerte.getAlleFachwerte());
        model.setEditable(false);

        mainPanel.add(createTablePanel(model));

        mainPanel.add(createSouthPanel());

        this.moneyController = moneyController;

        addWindowListener(new MyWindowListener());

        tabbedPane.add("Umsätze",mainPanel);
        tabbedPane.add("Nachrichten", createMessagePanel());

        tabbedPane.add("Kategorien", new KategorieEditor().createEditorPanel(this));
        setContentPane(tabbedPane);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public JPanel createTablePanel(BuchungszeilenTableModel model) {

        ATableForm tablePane = createTable(model);
        tablePane.setBorder(new TitledBorder("Liste der Umsätze"));

        statusZeile = new JLabel();
        statusZeile.setText("Keine Datenbank geladen");
        tablePane.setStatusLabel(statusZeile);

        return tablePane;
    }

    private ATableForm createTable(final BuchungszeilenTableModel model) {

        ATableForm tableForm = new ATableForm(model);

        final JTable table = tableForm.getTable();

        ATableRowSorter sorter = new ATableRowSorter();
        table.setRowSorter( sorter );
        sorter.setModel( model );

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int[] selectedRows = table.getSelectedRows();
                        if (selectedRows.length != 1) {
                            return;
                        }
                        Buchungszeile[] buchungszeiles = new Buchungszeile[selectedRows.length];
                        for (int i = 0; i < selectedRows.length; i++) {
                            int selectedRow = selectedRows[i];
                            buchungszeiles[i] = (Buchungszeile) model.getData(  table.convertRowIndexToModel(selectedRow));

                        }
                        BuchungszeilenEditor editor = new BuchungszeilenEditor(moneyController, buchungszeiles, false);
                        editor.setVisible(true);
                        updateGui();

                        table.setRowSelectionInterval(row,row);
                    }
                }
            }
        });

        table.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

                if (e.getKeyChar() != 'k') {
                    return;
                }

                int row = table.getSelectedRow();
                if (row >= 0) {
                    int[] selectedRows = table.getSelectedRows();
                    if (selectedRows.length == 0) {
                        return;
                    }
                    Buchungszeile[] buchungszeiles = new Buchungszeile[selectedRows.length];
                    for (int i = 0; i < selectedRows.length; i++) {
                        int selectedRow = selectedRows[i];
                        buchungszeiles[i] = (Buchungszeile) model.getData(table.convertRowIndexToModel(selectedRow));

                    }
                    BuchungszeilenEditor editor = new BuchungszeilenEditor(moneyController, buchungszeiles, true);
                    editor.setVisible(true);
                    updateGui();
                    table.setRowSelectionInterval(row,row);
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        if (AmoStyle.isGuiTestMode()) {
            // der wirkt wirklich
            table.setBackground(Color.cyan);
        }

        return tableForm;
    }

    private JPanel createSummaryPanel() {
        summaryPanel = new JPanel();
        GridLayout mgr = new GridLayout(4, 2, 0, 0);
        summaryPanel.setLayout(mgr);
        summaryPanel.setBounds(0,0,600,300);

        AFieldPane aFieldPane;

        datenbankNameInput = AStringInputField.create("");
        datenbankNameInput.setEditable(false);
        aFieldPane = new AFieldPane("Datenbankfilename", datenbankNameInput, 20, 150, 300, 0);
        summaryPanel.add(aFieldPane);

        summaryLabelDatenbankSaved = AStringInputField.create("");
        summaryLabelDatenbankSaved.setEditable(false);
        aFieldPane = new AFieldPane("Gesichert", summaryLabelDatenbankSaved, 20, 150, 50, 0);
        summaryPanel.add(aFieldPane);

        saldoInput = ANumberInputField.create(0,2);
        saldoInput.setEditable(false);
        aFieldPane = new AFieldPane("Saldo", saldoInput, 20, 150, 100, 30);
        aFieldPane.getTrailingLabel().setText("EUR");
        summaryPanel.add(aFieldPane);

        psaldoInput = ANumberInputField.create(0,2);
        psaldoInput.setEditable(false);
        aFieldPane = new AFieldPane("P-Saldo", psaldoInput, 20, 150, 100, 30);
        aFieldPane.getTrailingLabel().setText("EUR");
        summaryPanel.add(aFieldPane);

        forecastInput = ANumberInputField.create(0,2);
        forecastInput.setEditable(false);
        aFieldPane = new AFieldPane("Forecast", forecastInput, 20, 150, 100, 30);
        aFieldPane.getTrailingLabel().setText("EUR");
        summaryPanel.add(aFieldPane);

        pSaldoAmInput = ANumberInputField.create(0,2);
        pSaldoAmInput.setEditable(false);
        psaldoAmPane = new AFieldPane("P-Saldo am ", pSaldoAmInput, 20, 150, 100, 30);
        psaldoAmPane.getTrailingLabel().setText("EUR");
        summaryPanel.add(psaldoAmPane);

        zahlungAmInput = ANumberInputField.create(0,2);
        zahlungAmInput.setEditable(false);
        zahlungAmPane = new AFieldPane("Zahlung am ", zahlungAmInput, 20, 150, 100, 30);
        zahlungAmPane.getTrailingLabel().setText("EUR");
        summaryPanel.add(zahlungAmPane);

        umsatzDateienUploadDir = AStringInputField.create("");
        umsatzDateienUploadDir.setEditable(false);
        umsatzDateienUploadPane = new AFieldPane("Bankdaten Download", umsatzDateienUploadDir,20,150,400,20);
        summaryPanel.add(umsatzDateienUploadPane);


        if (AmoStyle.isGuiTestMode()) {

            summaryPanel.setBackground(Color.red);
        }

        return summaryPanel;
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel();

        southPanel.setLayout(new javax.swing.BoxLayout(southPanel, javax.swing.BoxLayout.Y_AXIS));
        southPanel.add(createFilterPanel());
        southPanel.add(createButtonPanel());
        return southPanel;
    }

    private JPanel createButtonPanel() {
        buttonPanel = new JPanel();
        if (AmoStyle.isGuiTestMode()) {
            buttonPanel.setBackground(Color.red);
        }

        buttonPanel.setLayout(new GridLayout(1,3));
        buttonPanel.setBorder(new TitledBorder("Datei-Operationen"));

        Dimension dimension = new Dimension(300,60);
        buttonPanel.setPreferredSize(dimension);

        JPanel b0 = new JPanel();
        JPanel b1 = new JPanel();
        JPanel b2 = new JPanel();
        b0.add(createAuswertungsButton());
        b1.add(createBankDatenEinlesenButton());
        b2.add(createSaveButton());

        buttonPanel.add(b0);
        buttonPanel.add(b1);
        buttonPanel.add(b2);
        buttonPanel.setVisible(true);
        return buttonPanel;
    }

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(new TitledBorder("Filterkriterien"));
        filterPanel.setLayout(new GridLayout(3,3,0,0));

        filterPanel.setSize(300, 130);
        AFieldPane aFieldPane;

        JPanel datumPanel = new JPanel();
        datumPanel.setLayout(new BoxLayout(datumPanel, BoxLayout.X_AXIS));
        filterPanel.add(datumPanel);

        filter_datumVon = ADateInputField.create(null);
        aFieldPane = new AFieldPane("Datum von", filter_datumVon, 25, 70, ADateInputField.DATEINPUTLENGTH, 0);
        datumPanel.add(aFieldPane);

        filter_datumBis = ADateInputField.create(null);
        aFieldPane = new AFieldPane("bis", filter_datumBis, 25, 30, ADateInputField.DATEINPUTLENGTH, 0);
        datumPanel.add(aFieldPane);

        filter_quelleZiel = AStringInputField.create(null);
        aFieldPane = new AFieldPane("Quelle/Ziel", filter_quelleZiel, 25, 100, 200, 0);
        filterPanel.add(aFieldPane);

        filter_verwendungszweck = AStringInputField.create(null);
        aFieldPane = new AFieldPane("Verwendungszweck", filter_verwendungszweck, 25, 120, 200, 0);
        filterPanel.add(aFieldPane);

        JPanel betragPanel = new JPanel();
        betragPanel.setLayout(new BoxLayout(betragPanel, BoxLayout.X_AXIS));
        filterPanel.add(betragPanel);

        filter_betragVon = ANumberInputField.create(Integer.MIN_VALUE, 2);
        aFieldPane = new AFieldPane("Betrag von", filter_betragVon, 25, 70, 70, 0);
        betragPanel.add(aFieldPane);

        filter_betragBis = ANumberInputField.create(Integer.MIN_VALUE, 2);
        aFieldPane = new AFieldPane("bis", filter_betragBis, 25, 30, 70, 0);
        betragPanel.add(aFieldPane);

        filter_buchungstext= AStringInputField.create(null);
        aFieldPane = new AFieldPane("Buchungstext", filter_buchungstext, 25, 100, 200, 0);
        filterPanel.add(aFieldPane);

        filter_kommentar = AStringInputField.create(null);
        aFieldPane = new AFieldPane("Kommentar", filter_kommentar, 25, 120, 200, 0);
        filterPanel.add(aFieldPane);

        filter_kategorie = AStringInputField.create(null);
        aFieldPane = new AFieldPane("Kategorie", filter_kategorie, 25, 70, 200, 0);
        filterPanel.add(aFieldPane);

        JPanel b1 = new JPanel();
        b1.add(createViewRefreshButton());
        JPanel b2 = new JPanel();
        b2.add(createFilterResetButton());
        filterPanel.add(b1);
        filterPanel.add(b2);

        return filterPanel;
    }

    private void resetFilter() {
        filter_kategorie.setValue(null);
        filter_kommentar.setValue(null);
        filter_verwendungszweck.setValue(null);
        filter_buchungstext.setValue(null);
        filter_quelleZiel.setValue(null);
        filter_betragBis.setValue(null);
        filter_betragVon.setValue(null);
        filter_datumBis.setValue(null);
        filter_datumVon.setValue(null);
    }

    private JPanel createMessagePanel() {

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridLayout(1,1));
        if (AmoStyle.isGuiTestMode()) {
            messagePanel.setBackground(Color.yellow);
        }

        Dimension dimension = new Dimension(300,100);
        messagePanel.setSize(300, 100);
        messagePanel.setPreferredSize(dimension);

        messageTextArea = new JTextArea();
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setAutoscrolls(true);
        messageTextArea.setTabSize(20);

        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messagePanel.add(scrollPane);

        return messagePanel;
    }

    private JButton createSaveButton() {
        saveButton = new JButton("Speichern der Datenbank");
        saveButton.setVisible(true);
        saveButton.addActionListener(actionListener);
        return saveButton;
    }

    private JButton createViewRefreshButton() {
        viewRefreshButton = new JButton("Aktualisieren der Liste gem. Filter");
        viewRefreshButton.setVisible(true);
        viewRefreshButton.addActionListener(actionListener);
        return viewRefreshButton;
    }

    private JButton createFilterResetButton() {
        resetFilterButton = new JButton("Rücksetzen der Filterkriterien");
        resetFilterButton.setVisible(true);
        resetFilterButton.addActionListener(actionListener);
        return resetFilterButton;
    }

    private JButton createBankDatenEinlesenButton() {
        bankDatenEinlesenButton = new JButton("ING-DiBa-Dateien lesen");
        bankDatenEinlesenButton.setVisible(true);
        bankDatenEinlesenButton.addActionListener(actionListener);
        return bankDatenEinlesenButton;
    }

    private JButton createAuswertungsButton() {
        auswertungsButton = new JButton("Auswertung erzeugen");
        auswertungsButton.setVisible(true);
        auswertungsButton.addActionListener(actionListener);
        return auswertungsButton;
    }


    class MyWindowListener extends WindowAdapter{

        @Override
        public void windowClosing(WindowEvent e) {

            if (moneyController.isSaved()) {
                System.exit(0);
            }

            int result = JOptionPane.showConfirmDialog(mainPanel, "Noch ist nix gespeichert, wirklich schließen?", "Sicherheitsabfrage", JOptionPane.YES_NO_OPTION, 0);

            if (result == 0) {
                System.exit(0);
            }
        }
    }

    class MyActionListener implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String titel = "Fehler ";
            try {
                if (actionEvent.getSource() == saveButton) {
                    titel += "beim Speichern der Datenbank";
                    addMessage("Speichern Event erhalten");
                    moneyController.saveDatabase();
                }
                if (actionEvent.getSource() == bankDatenEinlesenButton) {
                    titel += "beim Einlesen der Umsatzdateien";
                    addMessage("Bank-Dateien-Einlesen Event erhalten");
                    moneyController.umsatzDateienEinlesen();
                }
                if (actionEvent.getSource() == viewRefreshButton) {
                    titel += "beim Refresh der GUI";
                    addMessage("Refresh - Befehl erhalten");
                    moneyController.refreshView();
                }
                if (actionEvent.getSource() == resetFilterButton) {
                    titel += "beim Reset der Filter";
                    addMessage("Reset - Befehl erhalten");
                    resetFilter();
                }
                if (actionEvent.getSource() == auswertungsButton) {
                    titel += "beim Auswerten";
                    addMessage("Auswertebefehl erhalten");

                    new Reportgenerator(moneyController.getMoneyTr().getAktuelleDaten(), tabbedPane);
                }
            } catch (Exception e) {
                new ErrorMessageDialog(titel, e.getMessage(), e);
            }
        }
    }

    public void addMessage(String msg) {
        messageTextArea.append("\n"+msg);
        messageTextArea.repaint();
        messageTextArea.setCaretPosition(messageTextArea.getDocument().getLength());
    }

    private boolean isToList(Buchungszeile buchungszeile) {

        if (!ConditionChecker.check(buchungszeile.quelleZiel        , filter_quelleZiel.getText()))         return false;
        if (!ConditionChecker.check(buchungszeile.kategorie         , filter_kategorie.getText()))          return false;
        if (!ConditionChecker.check(buchungszeile.kommentar         , filter_kommentar.getText()))          return false;
        if (!ConditionChecker.check(buchungszeile.verwendungszweck  , filter_verwendungszweck.getText()))   return false;
        if (!ConditionChecker.check(buchungszeile.buchungstext      , filter_buchungstext.getText()))       return false;
        if (!ConditionChecker.check(buchungszeile.betrag            , filter_betragVon.getIntValue(), filter_betragBis.getIntValue()))   return false;
        if (!ConditionChecker.checkDateString(buchungszeile.datum   , filter_datumVon.getDateString(), filter_datumBis.getDateString())) return false;
        return true;
    }

    public void updateGui() {

        MoneyTransient moneyTr = moneyController.getMoneyTr();
        List<Buchungszeile> aktuelleDaten = moneyTr.getAktuelleDaten();

        model.getDataVector().clear();

        String erstesDatum              = "00000000";
        String letztesDatum             = "00000000";
        String erstesDatumSelected      = "00000000";
        String letztesDatumSelected     = "00000000";

        int countHauptbuchungen         = 0;
        int countUmbuchungen            = 0;
        int countHauptbuchungenSelected = 0;
        int countUmbuchungenSelectected = 0;

        for (Buchungszeile buchungszeile : aktuelleDaten) {

            if (buchungszeile.isUmbuchung()) {
                countUmbuchungen++;
            } else {
                countHauptbuchungen++;
            }

            if (erstesDatum == "00000000") {
                erstesDatum = buchungszeile.datum;
            }
            letztesDatum = buchungszeile.datum;

            if (!isToList(buchungszeile)) {
                continue;
            }

            if (buchungszeile.isUmbuchung()) {
                countUmbuchungenSelectected++;
            } else {
                countHauptbuchungenSelected++;
            }


            model.getDataVector().add(buchungszeile);

            if (erstesDatumSelected == "00000000") {
                erstesDatumSelected = buchungszeile.datum;
            }
            letztesDatumSelected = buchungszeile.datum;
        }

        String monatsAbgrenzDatum = moneyTr.getMonatsAbgrenzDatum();
        String datum  = monatsAbgrenzDatum.substring(6,8) + "." + monatsAbgrenzDatum.substring(4,6) + "." + monatsAbgrenzDatum.substring(0,4);
        zahlungAmPane.getLeadingLabel().setText("Zahlung am " + datum);
        psaldoAmPane.getLeadingLabel().setText("P-Saldo am " + datum);
        saldoInput                  .setValue(moneyTr.getSaldo(),2);
        psaldoInput                 .setValue(moneyTr.getPsaldo(),2);
        pSaldoAmInput               .setValue(moneyTr.getpSaldoMonatsanfang(),2);
        forecastInput               .setValue(moneyTr.getForecastSumme(),2);
        zahlungAmInput              .setValue(moneyTr.getpToNullBetragMonatsanfang(),2);
        datenbankNameInput          .setValue(moneyController.getMoneyDatabase().getKontodir());
        summaryLabelDatenbankSaved  .setValue(moneyTr.isSaved() ? "Ja" : "Nein");
        umsatzDateienUploadDir      .setValue(moneyTr.getUmsatzdateienDownloadDir().getAbsolutePath());
        umsatzDateienUploadPane.getTrailingLabel().setText("("+moneyTr.ermittleAnzahlUmsatzdateienImDownload()+")");

        erstesDatum             = erstesDatum.substring(6, 8) + "." + erstesDatum.substring(4, 6) + "." + erstesDatum.substring(0, 4);
        letztesDatum            = letztesDatum.substring(6, 8) + "." + letztesDatum.substring(4, 6) + "." + letztesDatum.substring(0, 4);
        erstesDatumSelected     = erstesDatumSelected.substring(6, 8) + "." + erstesDatumSelected.substring(4, 6) + "." + erstesDatumSelected.substring(0, 4);
        letztesDatumSelected    = letztesDatumSelected.substring(6, 8) + "." + letztesDatumSelected.substring(4, 6) + "." + letztesDatumSelected.substring(0, 4);


        statusZeile.setText("Total " + aktuelleDaten.size() + " Buchungssätze von " + erstesDatum + " - " + letztesDatum + "  " + countHauptbuchungen + "/" + countUmbuchungen
        + "  |  selektierte Buchungssätze von " + erstesDatumSelected + " - " + letztesDatumSelected + "  " + countHauptbuchungenSelected + "/" + countUmbuchungenSelectected);



        addMessage(moneyController.getMessage());


        model.fireTableDataChanged();
    }
}
