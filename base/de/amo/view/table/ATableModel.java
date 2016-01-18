package de.amo.view.table;

import de.amo.view.fachwerte.Fachwert;

import javax.swing.table.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by private on 17.01.2016.
 * <p/>
 * geklaut bei http://www.javalobby.org/articles/jtable/?source=archives
 */
public abstract class ATableModel extends DefaultTableModel {

    private int hiddenIndex;

    protected String[] columnNames;
    protected String[] attributNames;
    protected TableCellRenderer[] cellRenderers;
    protected TableCellEditor[] tableCellEditors;
    protected Class[] columnClasses;

    List<ATableButton> buttons;
    MyActionListener actionListener;

    int[] minWidth;
    int[] preferredWidth;
    int[] maxWidth;

    public ATableModel(List<Fachwert> fachwerte) {

        this.columnNames        = new String[fachwerte.size() + 1];
        this.attributNames      = new String[fachwerte.size() + 1];
        this.cellRenderers      = new TableCellRenderer[fachwerte.size() + 1];
        this.tableCellEditors   = new TableCellEditor[fachwerte.size() + 1];
        this.columnClasses      = new Class[fachwerte.size() + 1];
        this.minWidth           = new int[fachwerte.size() + 1];
        this.preferredWidth     = new int[fachwerte.size() + 1];
        this.maxWidth           = new int[fachwerte.size() + 1];

        for (int i = 0; i < fachwerte.size(); i++) {
            Fachwert fachwert   = fachwerte.get(i);
            attributNames[i]    = fachwert.getAttributName();
            columnNames[i]      = fachwert.getColumName();
            cellRenderers[i]    = fachwert.getTableCellRenderer();
            tableCellEditors[i] = fachwert.getTableCellEditor();
            columnClasses[i]    = fachwert.getColumnClass();
            minWidth[i]         = fachwert.getMinWidth();
            maxWidth[i]         = fachwert.getMaxWidth();
            preferredWidth[i]   = fachwert.getPreferredWidth();
        }

        hiddenIndex = fachwerte.size();
        columnNames[hiddenIndex] = "hidden";

    }

    public void addButton(ATableButton button) {
        if (buttons == null) {
            buttons = new ArrayList<>();
        }
        buttons.add(button);
        if (actionListener == null) {
            actionListener = new MyActionListener();
        }
        button.addActionListener(actionListener);
        button.setATableModel(this);
    }

    public int getHiddenIndex() {
        return hiddenIndex;
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    public boolean isCellEditable(int row, int column) {
        if (column == hiddenIndex) return false;
        else return true;
    }

    public Class getColumnClass(int column) {
        if (column == hiddenIndex) return Object.class;
        // ToDo: Checken, ob nicht präziser besser wäre
        return String.class;
    }

    public abstract Object getValueAt(Object record, String attributname);

    public Object getValueAt(int row, int colum) {
        Object record = dataVector.get(row);
        Object ret = getValueAt(record, attributNames[colum]);

        /*if (ret == null) {
            ret = new Object();
        }
        */
        return ret;
    }

    public abstract void setValueAt(Object value, Object record, String attributName);

    public void setValueAt(Object value, int row, int colum) {
        Object record = dataVector.get(row);
        setValueAt(value, record, attributNames[colum]);
        fireTableCellUpdated(row, colum);
    }

    public int getColumnCount() {
        return attributNames.length;
    }

    public abstract boolean isRecordEmpty(Object record);

    public boolean hasEmptyRow() {
        if (dataVector.size() == 0) return false;
        return isRecordEmpty(dataVector.get(dataVector.size() - 1));
    }

    public abstract Object createEmptyRecord();

    public void addEmptyRow() {

        dataVector.add(createEmptyRecord());

        fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
    }

    public TableCellRenderer getTableCellRenderer(int column) {
        if (cellRenderers != null) {
            return cellRenderers[column];
        }
        return new DefaultTableCellRenderer();
    }

    public TableCellEditor getTableCellEditor(int column) {
        if (tableCellEditors != null) {
            return tableCellEditors[column];
        }
        return null;
    }

    public int getMinWidth(int column) {
        return minWidth[column];
    }

    public int getPreferredWidth(int column) {
        return preferredWidth[column];
    }

    public int getMaxWidth(int column) {
        return maxWidth[column];
    }

    public List<ATableButton> getButtons() {
        return buttons;
    }

    // *****************************************************************************************************************
    private class MyActionListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (buttons == null) {
                return;
            }
            for (ATableButton button : buttons) {
                if (actionEvent.getSource() == button) {
                    button.execute();
                }
            }
        }
    }
}