package de.amo.view.table;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by private on 17.01.2016.
 * <p/>
 * geklaut bei http://www.javalobby.org/articles/jtable/?source=archives
 */
public abstract class ATableModel extends AbstractTableModel {

    private int hiddenIndex;

    protected String[] columnNames;
    protected Vector dataVector;

    List<ATableButton> buttons;
    MyActionListener actionListener;

    /**
     * An die vom Auftraggeber definierten Spalten wir noch eine Hidden-Spalte angefügt
     *
     * @param aktColumnNames
     */
    public ATableModel(String[] aktColumnNames) {
        this.columnNames = new String[aktColumnNames.length + 1];
        for (int i = 0; i < aktColumnNames.length; i++) {
            columnNames[i] = aktColumnNames[i];
        }
        hiddenIndex = aktColumnNames.length;
        columnNames[hiddenIndex] = "hidden";

        dataVector = new Vector();
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
        return String.class;
    }

    public abstract Object getValueAt(Object record, int column);

    public Object getValueAt(int row, int column) {
        Object record = dataVector.get(row);
        Object ret = getValueAt(record, column);


        /*if (ret == null) {
            ret = new Object();
        }
        */
        return ret;
    }

    public abstract void setValueAt(Object value, Object record, int colums);

    public void setValueAt(Object value, int row, int column) {
        Object record = dataVector.get(row);
        setValueAt(value, record, column);
        fireTableCellUpdated(row, column);
    }

    public int getRowCount() {
        return dataVector.size();
    }

    public int getColumnCount() {
        return columnNames.length;
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
        return new DefaultTableCellRenderer();
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