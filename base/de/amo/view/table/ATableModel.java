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
public abstract class ATableModel extends AbstractTableModel {

    protected  Vector dataVector;

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
    boolean[] isSummable;

    boolean editable;

    public ATableModel(List<Fachwert> fachwerte) {

        this.columnNames        = new String[fachwerte.size() + 1];
        this.attributNames      = new String[fachwerte.size() + 1];
        this.cellRenderers      = new TableCellRenderer[fachwerte.size() + 1];
        this.tableCellEditors   = new TableCellEditor[fachwerte.size() + 1];
        this.columnClasses      = new Class[fachwerte.size() + 1];
        this.minWidth           = new int[fachwerte.size() + 1];
        this.preferredWidth     = new int[fachwerte.size() + 1];
        this.maxWidth           = new int[fachwerte.size() + 1];
        this.isSummable         = new boolean[fachwerte.size() + 1];

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
            isSummable[i]       = fachwert.isSummable();
        }

        hiddenIndex = fachwerte.size();
        columnNames[hiddenIndex] = "hidden";

        dataVector = new Vector();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
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
        else return editable;
    }

    public Class getColumnClass(int column) {
        // ToDo: Object wird von Oracle wegen Performance-Verschlechterungen NICHT empfohlen, klären, ob String o.ä. hier auch genügt.
        if (column == hiddenIndex) return Object.class;
        return columnClasses[column];
    }

    public abstract Object getValueAt(Object record, String attributName);

    public Object getValueAt(int row, int column) {
        /*
        row: 0 column: -1
    Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: Array index out of range: 0
	at java.util.Vector.get(Vector.java:744)
	at de.amo.view.table.ATableModel.getValueAt(ATableModel.java:104)
	at javax.swing.JTable.getValueAt(JTable.java:2717)
	at javax.swing.JTable.prepareRenderer(JTable.java:5719)
	at javax.swing.plaf.basic.BasicTableUI.paintCell(BasicTableUI.java:2114)
         */
        if(column == -1) {
            return null;
        }
        Object record = dataVector.get(row);
        Object ret = getValueAt(record, attributNames[column]);

        return ret;
    }

    public abstract void setValueAt(Object value, Object record, String attributName);

    public void setValueAt(Object value, int row, int column) {
        Object record = dataVector.get(row);
        setValueAt(value, record, attributNames[column]);
        fireTableCellUpdated(row, column);
    }

    public int getRowCount() {
        return dataVector.size();
    }

    public int getColumnCount() {
        return attributNames.length;
    }

    public abstract boolean isRecordEmpty(Object record);

    public boolean hasEmptyRow() {
        if (dataVector.size() == 0) return false;
        return isRecordEmpty(dataVector.get(dataVector.size() - 1));
    }

    public Vector getDataVector() {
        return dataVector;
    }

    public Object getData(int row) {
        return dataVector.get(row);
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

    public boolean isSummable(int column) { return isSummable[column];}

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