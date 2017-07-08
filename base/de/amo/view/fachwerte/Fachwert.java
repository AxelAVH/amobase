package de.amo.view.fachwerte;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Created by private on 18.01.2016.
 */
public abstract class Fachwert {

    String              attributName;
    Class               columnClass;
    TableCellRenderer   tableCellRenderer;
    TableCellEditor     tableCellEditor;
    boolean             isSummable;

    String columName;
    String label30;
    String label40;
    String label50;
    String label60;
    String label70;
    String label80;
    String label90;
    String label100;

    private int minWidth = -1;
    private int preferredWidth = -1;
    private int maxWidth = -1;


    public Fachwert(String attributName) {
        this.attributName = attributName;
    }

//    public void setAttributName(String attributName) {
//        this.attributName = attributName;
//    }

    public void setColumName(String columName) {
        this.columName = columName;
    }

//    public void setColumnClass(Class columnClass) {
//        this.columnClass = columnClass;
//    }

    public void setTableCellRenderer(TableCellRenderer tableCellRenderer) {
        this.tableCellRenderer = tableCellRenderer;
    }

    public void setTableCellEditor(TableCellEditor tableCellEditor) {
        this.tableCellEditor = tableCellEditor;
    }

    public void setLabel30(String label30) {
        this.label30 = label30;
    }

    public void setLabel40(String label40) {
        this.label40 = label40;
    }

    public void setLabel50(String label50) {
        this.label50 = label50;
    }

    public void setLabel60(String label60) {
        this.label60 = label60;
    }

    public void setLabel70(String label70) {
        this.label70 = label70;
    }

    public void setLabel80(String label80) {
        this.label80 = label80;
    }

    public void setLabel90(String label90) {
        this.label90 = label90;
    }

    public void setLabel100(String label100) {
        this.label100 = label100;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public void setPreferredWidth(int preferredWidth) {
        this.preferredWidth = preferredWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public String getLabel(int length) {
        if (length >= 100 && label100 != null) return label100;
        if (length >=  90 && label90  != null) return label90;
        if (length >=  80 && label80  != null) return label80;
        if (length >=  70 && label70  != null) return label70;
        if (length >=  60 && label60  != null) return label60;
        if (length >=  50 && label50  != null) return label50;
        if (length >=  40 && label40  != null) return label40;
        if (length >=  30 && label30  != null) return label30;

        return getAttributName();
    }

    public String getAttributName() {
        return attributName;
    }

    public String getColumName() {
        if (columName != null) {
            return columName;
        }
        return getAttributName();
    }

    public Class getColumnClass() {
        return columnClass;
    }

    public TableCellRenderer getTableCellRenderer() {
        return tableCellRenderer;
    }

    public TableCellEditor getTableCellEditor() {
        return tableCellEditor;
    }

    public int getMinWidth() {
        if (minWidth < 0) {
            return 100;
        }
        return minWidth;
    }

    public int getPreferredWidth() {
        if (preferredWidth < 100) {
            return 100;
        }
        return preferredWidth;
    }

    public int getMaxWidth() {

        if (maxWidth < 100) {
            return 100;
        }
        return maxWidth;
    }

    public boolean isSummable() {
        return isSummable;
    }

    public void setSummable(boolean summable) {
        isSummable = summable;
    }
}
