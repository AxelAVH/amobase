package de.amo.money;

import de.amo.view.AmoStyle;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.prefs.BackingStoreException;

/**
 * Created by private on 16.01.2016.
 */
public class KategorieEditor {

    MyActionListener actionListener;
    JButton saveButton;
    JButton abortButton;
    JButton addRowButton, delRowButton;

    MoneyView moneyView;

    KategorieTableModel tableModel;

    public JPanel createEditorPanel(MoneyView moneyView) {

        this.moneyView = moneyView;

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.add(createTablePanel());
        main.add(createButtonPanel());
        return main;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        saveButton = new JButton("Speichern");
        abortButton = new JButton("Abbrechen");
        addRowButton = new JButton("Zeile hinzufügen");
        delRowButton = new JButton("Zeile entfernen");

        JPanel bp = new JPanel();
        bp.add(addRowButton);
        buttonPanel.add(bp);

        bp = new JPanel();
        bp.add(delRowButton);
        buttonPanel.add(bp);

        bp = new JPanel();
        bp.add(saveButton);
        buttonPanel.add(bp);

        bp = new JPanel();
        bp.add(abortButton);
        buttonPanel.add(bp);

        actionListener = new MyActionListener();

        delRowButton.addActionListener(actionListener);
        addRowButton.addActionListener(actionListener);
        saveButton.addActionListener(actionListener);
        abortButton.addActionListener(actionListener);

        return buttonPanel;
    }

    class MyActionListener implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String titel = "Fehler ";
            //try {
            if (actionEvent.getSource() == saveButton) {
                titel += "beim Speichern der Kategorien";
                moneyView.addMessage("Speichern Event erhalten");
                //moneyController.saveDatabase();

                Kategoriefacade.get().getKategorien().clear();

                int rowCount = tableModel.getRowCount();
                for (int i = 0; i < rowCount; i++) {

                    String code = (String) tableModel.getValueAt(i,0);
                    String beschreibung = (String) tableModel.getValueAt(i,1);

                    moneyView.addMessage("Code: " + code + " - " + beschreibung);


                    Kategoriefacade.get().getKategorien().put(code, beschreibung);
                }

                try {
                    Kategoriefacade.get().saveKategorien();
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
            }
            if (actionEvent.getSource() == abortButton) {
                titel += "beim Abbrechen der Kategorien";
                moneyView.addMessage("Abbrechen Event erhalten");
                //moneyController.saveDatabase();
            }
            if (actionEvent.getSource() == addRowButton) {
                titel += "beim Hinzufügen einer Zeile der Kategorien";
                moneyView.addMessage(titel);
                tableModel.addRow(new Object[2]);
            }
            if (actionEvent.getSource() == delRowButton) {
                titel += "beim Entfernen einer Zeile der Kategorien";
                moneyView.addMessage(titel);
                // hier fehlt jetzt die Info über die Zeile
            }
            //}
        }
    }


    private JPanel createTablePanel() {

        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));

        tableModel = new KategorieTableModel();
        tableModel.addColumn("Code");
        tableModel.addColumn("Beschreibung");

        final JTable table = new JTable(tableModel) {
            public boolean isCellEditable(int x, int y) {
                return true;
            }

            public TableCellRenderer getCellRenderer(int row, int column) {

                if (column == 0) {
                    return new DefaultTableCellRenderer() {
                        @Override
                        public int getHorizontalAlignment() {
                            return SwingConstants.CENTER;
                        }
                    };
                }
                // else...
                return super.getCellRenderer(row, column);
            }
        };

        TableColumn column = null;
        column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);
        column.setMaxWidth(60);
        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(200);


        table.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

                if (e.getKeyChar() != KeyEvent.VK_TAB) {
                    return;
                }
                moneyView.addMessage("keyTyped() " + e.getKeyCode());
                /*
                int row = table.getSelectedRow();
                if (row >= 0) {
                    int[] selectedRows = table.getSelectedRows();
                    if (selectedRows.length == 0) {
                        return;
                    }
                    Buchungszeile[] buchungszeiles = new Buchungszeile[selectedRows.length];
                    for (int i = 0; i < selectedRows.length; i++) {
                        int selectedRow = selectedRows[i];
                        buchungszeiles[i] = modelIndex.get(selectedRow);

                    }
                    BuchungszeilenEditor editor = new BuchungszeilenEditor(moneyController, buchungszeiles, true);
                    editor.setVisible(true);
                    updateGui();
                }
*/
            }

            @Override
            public void keyPressed(KeyEvent e) {
                moneyView.addMessage("keyPressed() " + e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                moneyView.addMessage("keyReleased() " + e.getKeyCode());
            }
        });


        if (AmoStyle.isGuiTestMode()) {
            // der wirkt wirklich
            table.setBackground(Color.cyan);
        }
        table.setVisible(true);

        JScrollPane comp = new JScrollPane(table);

        Map<String, String> kategorien = Kategoriefacade.get().getKategorien();
        tablePanel.add(comp);
        comp.setBorder(new TitledBorder("Kategorien zum selber Editieren"));

        for (Map.Entry<String, String> stringStringEntry : kategorien.entrySet()) {
            Object[] rowData = new Object[2];
            rowData[0] = stringStringEntry.getKey();
            rowData[1] = stringStringEntry.getValue();
            tableModel.addRow(rowData);
        }

        return tablePanel;
    }


    class KategorieTableModel extends DefaultTableModel {
        public Class getColumnClass(int c) {
            return String.class;
        }
    }
}