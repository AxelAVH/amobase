package de.amo.money;

import de.amo.view.AmoStyle;
import de.amo.view.table.ATableForm;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.prefs.BackingStoreException;

/**
 * Created by private on 16.01.2016.
 */
public class KategorieEditor {

    MyActionListener actionListener;
    JButton saveButton;
    JButton abortButton;
    JTable  kategorienTable;

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

        JPanel bp = new JPanel();
        bp.add(saveButton);
        buttonPanel.add(bp);

        bp = new JPanel();
        bp.add(abortButton);
        buttonPanel.add(bp);

        actionListener = new MyActionListener();

        saveButton.addActionListener(actionListener);
        abortButton.addActionListener(actionListener);

        return buttonPanel;
    }

    class MyActionListener implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            if (actionEvent.getSource() == saveButton) {
                moneyView.addMessage("Speichern Event erhalten");

                Kategoriefacade.get().getKategorien().clear();
                Kategoriefacade.get().getKategorien().addAll(tableModel.getDataVector());

                try {
                    Kategoriefacade.get().saveKategorien();
                } catch (BackingStoreException e) {
                    e.printStackTrace();
                }
                kategorienTable.repaint();
            }

            if (actionEvent.getSource() == abortButton) {
                moneyView.addMessage("Abbrechen Event erhalten");
                tableModel.getDataVector().clear();
                tableModel.getDataVector().addAll(Kategoriefacade.get().getKategorien());
                kategorienTable.repaint();
            }
        }
    }


    private JPanel createTablePanel() {

        tableModel = new KategorieTableModel(Kategorie.getAlleFachwerte());
        tableModel.getDataVector().addAll(Kategoriefacade.get().getKategorien());
        tableModel.setEditable(true);

        ATableForm tablePanel = new ATableForm(tableModel);
        kategorienTable = tablePanel.getTable();

        tablePanel.setBorder(new TitledBorder("Kategorien zum selber Editieren"));

        if (AmoStyle.isGuiTestMode()) {
            tablePanel.getTable().setBackground(Color.cyan);
        }

        return tablePanel;
    }
}