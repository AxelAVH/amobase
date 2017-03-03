package de.amo.view.table;

import de.amo.tools.StringFormatter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Renderer für Spaltenköpfe/Tabellenüberschriften im <b>Formsheet</b>-Bereich.<br/>
 * Wird standardmäßig für die LTable benutzt.
 */
public class ATableHeaderRenderer implements TableCellRenderer, SwingConstants, PropertyChangeListener {

    private static Icon mainSortDescIcon = null;
    private static Icon mainSortAscIcon = null;
    private static Icon subSortDescIcon = null;
    private static Icon subSortAscIcon = null;

    private TableHeaderComponent renderer = null;

//    private int mainSortColumn    = TableModelSorter.INVALID_COLUMN;
//    private int subSortColumn     = TableModelSorter.INVALID_COLUMN;
//    private int mainSortDirection = TableModelSorter.ASCENDING;
//    private int subSortDirection  = TableModelSorter.ASCENDING;

    private ATableModel aTableModel;

    boolean doSum = true;

    private Object[] filter = new Object[0];


    /**
     * Default-Konstruktor
     */
    public ATableHeaderRenderer(ATableModel aTableModel) {
        this.aTableModel = aTableModel;
        this.renderer    = new TableHeaderComponent();
        initIcons();
    }

    public ATableHeaderRenderer(ATableModel aTableModel, boolean doSum) {
        this.aTableModel = aTableModel;
        this.renderer    = new TableHeaderComponent();
        this.doSum       = doSum;
        initIcons();
    }

    /**
     * Initialisiert die Icons für die Sortieranzeige
     */
    private void initIcons() {
        if (mainSortAscIcon == null) {
            // Asc/Desc bewusst vertauscht, da IconManager-Implementierung falsch
//            mainSortAscIcon  = IconManager.getSortDescendingIcon();
//            mainSortDescIcon = IconManager.getSortAscendingIcon();
//            subSortAscIcon   = IconManager.getSubsortDescendingIcon();
//            subSortDescIcon  = IconManager.getSubsortAscendingIcon();
        }
    }

    /**
     * Liefert das Icon für die angegebene Spalte
     */
    private Icon getIcon(int column) {
//        if (column==mainSortColumn) {
//            if (mainSortDirection == TableModelSorter.ASCENDING) {
//                return mainSortAscIcon;
//            }
//            else {
//                return mainSortDescIcon;
//            }
//        }
//        else if (column==subSortColumn) {
//            if (subSortDirection == TableModelSorter.ASCENDING) {
//                return subSortAscIcon;
//            }
//            else {
//                return subSortDescIcon;
//            }
//        }
        return null;
    }

    /**
     * Liefert das aktuelle Renderer-Objekt
     */
    protected TableHeaderComponent getRenderer() {
        return renderer;
    }

    /**
     * Implementierung des Interfaces TableCellRenderer.
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        // übergebene Spalte in die "echte" Spalte umrechnen (Spalten-Umsortierung!)
        int realColumn = getRealColumnAt(column, table);

        // Attribute und Werte setzen
//	    Font font = hasFilterValue(column) ? filterFont : defaultFont;

        if (value == null) {
            value = "";
        }
        getRenderer().getCaptionLabel().setText(value.toString());
        getRenderer().getCaptionLabel().setIcon(getIcon(realColumn));
//        ScaleFactorProvider scaleFactorProvider = ScalingUtil.getScaleFactorProvider(table);
//        getRenderer().getCaptionLabel().setFont(ScalingUtil.deriveFont(font, scaleFactorProvider.getScaleFactor()));
        getRenderer().setToolTipText(getToolTipText(table, realColumn));

        getRenderer().setSumLabelVisible(doSum);

//        Font scaledRendererFont = ScalingUtil.deriveFont(defaultFont, scaleFactorProvider.getScaleFactor());
        if (doSum) {
            // ACHTUNG: hier muss der "originale" Spalten-Index verwendet werden, nur dann geht's
            String sumText = " ";
            Icon sumIcon = null;
            if (aTableModel.isSummable(realColumn)) {
                Class columnClass = table.getColumnClass(column);
                sumText = calculateSum(table, column, columnClass);
//                if (StringUtil.isNotEmpty(sumText)) {
//                    sumIcon = ScalingUtil.scaleIcon(IconManager.getSummeIcon(), scaleFactorProvider);
//                }
            }
            getRenderer().getSumLabel().setIcon(sumIcon);
            getRenderer().getSumLabel().setText(sumText);
//            getRenderer().getSumLabel().setFont(scaledRendererFont);
        }

//        getRenderer().setFont(scaledRendererFont);
        // den Renderer selbst zurückgeben (hier eine Instanz von JLabel)
        return getRenderer();
    }


    public void calculateSum(JTable table, int col) {
        TableColumn column = table.getColumnModel().getColumn(col);
        if (aTableModel.isSummable(col)) {
            TableCellRenderer headerRenderer = column.getHeaderRenderer();
            if (headerRenderer instanceof ATableHeaderRenderer) {
                Class columnClass = table.getColumnClass(col);
                String sumText = ((ATableHeaderRenderer) headerRenderer).calculateSum(table, col, columnClass);
                getRenderer().getSumLabel().setText(sumText);
            }
        }
    }

    public int getRealColumnAt(int visibleColumn, JTable table) {
        try {
            for (int col = 0; col < table.getColumnCount(); ++col) {
                String header = table.getModel().getColumnName(col);
                int idx = table.getColumnModel().getColumnIndex(header);
                if (idx == visibleColumn) {
                    return col;
                }
            }
            return visibleColumn;
        } catch (Throwable t) {
            return visibleColumn;
        }
    }

    /**
     * Reaktion auf Änderungen von Filter und Sorter  =>  Anzeige-Attribut-Werte ändern
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
//        if (TableModelSorter.MAIN_SORT_COLUMN_PROPERTY.equals(evt.getPropertyName())) {
//            mainSortColumn = ((Integer)evt.getNewValue()).intValue();
//            return;
//        }
//        if (TableModelSorter.SUB_SORT_COLUMN_PROPERTY.equals(evt.getPropertyName())) {
//            subSortColumn = ((Integer)evt.getNewValue()).intValue();
//            return;
//        }
//        if (TableModelSorter.MAIN_SORT_DIRECTION_PROPERTY.equals(evt.getPropertyName())) {
//            mainSortDirection= ((Integer)evt.getNewValue()).intValue();
//            return;
//        }
//        if (TableModelSorter.SUB_SORT_DIRECTION_PROPERTY.equals(evt.getPropertyName())) {
//            subSortDirection = ((Integer)evt.getNewValue()).intValue();
//            return;
//        }
//
//        if (TableModelFilter.ROW_FILTER_PROPERTY.equals(evt.getPropertyName())) {
//            int col = ((Integer)evt.getOldValue()).intValue();
//            Object value = evt.getNewValue();
//            setFilterValue(col,value);
//            return;
//        }
    }

    /**
     *
     */
    private boolean hasFilterValue(int col) {
        if (col >= filter.length) {
            return false;
        } else {
            return (filter[col] != null);
        }
    }

    /**
     *
     */
    private void setFilterValue(int col, Object value) {
        if (col >= filter.length) {
            Object[] newFilter = new Object[col + 1];
            for (int i = 0; i < filter.length; i++) {
                newFilter[i] = filter[i];
            }
            filter = newFilter;
        }
        filter[col] = value;
    }

    /**
     *
     */
    private String getToolTipText(JTable table, int col) {
        String text = table.getModel().getColumnName(col);
        if (hasFilterValue(col)) {
            text += " : " + filter[col].toString();
        }
        return text;
    }

    private String calculateSum(JTable table, int column, Class columnClass) {
        double sum = 0.0;
        int[] selectedRows = table.getSelectedRows();
        int maxRow = selectedRows.length;
        if (maxRow == 0 || table.getCellSelectionEnabled()) {
            maxRow = table.getRowCount();
            selectedRows = new int[maxRow];
            for (int i = 0; i < maxRow; i++) {
                selectedRows[i] = i;
            }
        }
        for (int r = 0; r < maxRow; r++) {
            try {
                Number value = (Number) table.getValueAt(selectedRows[r], column);
                if (value == null) {
                    continue;
                }
                double dValue = value.doubleValue();
                if (Double.isNaN(dValue)) {
                    continue;
                }
                sum += dValue;
            } catch (Exception exc) {
                return "";
            }
        }
        if (columnClass == Short.class || columnClass == Integer.class || columnClass == Long.class) {
            return String.valueOf((long) sum);
        } else {
            return StringFormatter.formatDouble(sum, 2, 2);
        }
    }


    // ############################################################################

    /**
     * Renderer-Klasse für den "Doppel-Header"
     */
    class TableHeaderComponent extends JList {

        JLabel captionLabel;
        JLabel sumLabel;
        boolean sumLabelVisible;

        TableHeaderComponent() {
            captionLabel = new JLabel();
            captionLabel.setHorizontalAlignment(CENTER);
            captionLabel.setHorizontalTextPosition(LEADING);
            sumLabel = new JLabel();
            sumLabel.setHorizontalAlignment(CENTER);
            sumLabel.setHorizontalTextPosition(TRAILING);
            sumLabel.setForeground(Color.DARK_GRAY);
            setListData(new Object[]{captionLabel});
            setSumLabelVisible(false);
            setCellRenderer(new ListCellRenderer() {
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    return (Component) value;
                }
            });
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        }

        public JLabel getSumLabel() {
            return sumLabel;
        }

        public JLabel getCaptionLabel() {
            return captionLabel;
        }

        public void setSumLabelVisible(boolean sumLabelVisible) {
            if (this.sumLabelVisible == sumLabelVisible) {
                return;
            }
            this.sumLabelVisible = sumLabelVisible;
            if (this.sumLabelVisible) {
                setListData(new Object[]{captionLabel, sumLabel});
            } else {
                setListData(new Object[]{captionLabel});
            }
        }

        @Override
        public void updateUI() {
            super.updateUI();
            LookAndFeel.installColorsAndFont(this, "TableHeader.background",
                    "TableHeader.foreground",
                    "TableHeader.font");
        }
    }

}