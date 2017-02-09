package de.amo.money;

import de.amo.view.AmoStyle;
import de.amo.view.ErrorMessageDialog;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by private on 09.02.2017.
 */
public class MoneyMultiView extends JFrame {

    SortedMap<String,MoneyController> konten = new TreeMap<>();
    File umsatzdateienDownloadDir;
    String kontodir;

    public  JPanel              mainPanel;
    public  JPanel              buttonPanel;

    public  JPanel              tabAllgemeinPanel;

    JTabbedPane tabbedPane;

    JButton bankDatenEinlesenButton;

    private MyActionListener actionListener;

    public MoneyMultiView(File downloaddir, String kontodir) {

        this.kontodir                 = kontodir;
        this.umsatzdateienDownloadDir = downloaddir;

        setTitle("Bankkontenverwaltung");

        actionListener = new MyActionListener();


        mainPanel = new JPanel();
        mainPanel.setBackground(Color.cyan);
        mainPanel.setLayout(new javax.swing.BoxLayout(mainPanel, javax.swing.BoxLayout.Y_AXIS));

        Dimension dim = new Dimension(1200,800);
        mainPanel.setPreferredSize(dim);

        setContentPane(mainPanel);

        tabbedPane = new JTabbedPane();
        tabAllgemeinPanel = new JPanel();
        tabbedPane.add("Allgemein",tabAllgemeinPanel);

        mainPanel.add(tabbedPane);
        buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        pack();
        setVisible(true);

    }

    public void addKontoTab(MoneyController kontoController) {
        MoneyView moneyView = kontoController.getMoneyView();
        JRootPane rootPane = moneyView.getRootPane();
        tabbedPane.add("konto",rootPane);
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

//        JPanel b0 = new JPanel();
        JPanel b1 = new JPanel();
//        JPanel b2 = new JPanel();
//        b0.add(createAuswertungsButton());
        b1.add(createBankDatenEinlesenButton());
//        b2.add(createSaveButton());

//        buttonPanel.add(b0);
        buttonPanel.add(b1);
//        buttonPanel.add(b2);
        buttonPanel.setVisible(true);
        return buttonPanel;
    }

    private JButton createBankDatenEinlesenButton() {
        bankDatenEinlesenButton = new JButton("ING-DiBa-Dateien lesen");
        bankDatenEinlesenButton.setVisible(true);
        bankDatenEinlesenButton.addActionListener(actionListener);
        return bankDatenEinlesenButton;
    }

    class MyActionListener implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String titel = "Fehler ";
            try {
                if (actionEvent.getSource() == bankDatenEinlesenButton) {
                    titel += "beim Einlesen der Umsatzdateien";
//                    addMessage("Bank-Dateien-Einlesen Event erhalten");


                    File[] files = umsatzdateienDownloadDir.listFiles();

                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        if (file.isDirectory()) {
                            continue;
                        }
                        if (!file.getName().toLowerCase().endsWith(".csv")) {
                            continue;
                        }
                        String kontoNrTmp = new UmsatzReader_INGDIBA().ermittleKontonummer(file);

                        MoneyController moneyController;
                        if (konten.containsKey(kontoNrTmp)) {
                            moneyController                     = konten.get(kontoNrTmp);
                        } else {
                            MoneyTransient moneyTransient       = new MoneyTransient (umsatzdateienDownloadDir);
                            MoneyDatabase   moneyDatabase       = new MoneyDatabase  (kontodir);
                            moneyController                     = new MoneyController(moneyTransient, moneyDatabase);
                            MoneyView       moneyView           = new MoneyView      (moneyController);
                            moneyController.moneyView           = moneyView;

                            konten.put(kontoNrTmp, moneyController);
                            tabbedPane.add(kontoNrTmp, moneyController.getMoneyView().getRootPane());
                        }

                        moneyController.getMoneyDatabase().umsatzDateienEinlesen(moneyController.getMoneyTr());

                        moneyController.getMoneyView().updateGui();

                        System.out.println(kontoNrTmp);
                    }

                }
            } catch (Exception e) {
                new ErrorMessageDialog(titel, e.getMessage(), e);
            }
        }
    }

}
