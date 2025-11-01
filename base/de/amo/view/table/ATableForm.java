package de.amo.view.table;

/**
 * Created by private on 17.01.2016.
 */

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;

public class ATableForm extends JPanel {

    protected JTable table;
    private JPanel statusPanel;
    protected JScrollPane scroller;
    protected ATableModel tableModel;

    public ATableForm(ATableModel aTableModel) {
        tableModel = aTableModel;
        initComponent();
    }

    public JTable getTable() {
        return table;
    }

    public void setStatusLabel(JLabel statusLabel) {
        statusPanel.add(statusLabel);
        statusPanel.setVisible(true);
    }

    public void initComponent() {

        tableModel.addTableModelListener(new ATableForm.InteractiveTableModelListener());
        table = new JTable();
        table.setModel(tableModel);
        table.setSurrendersFocusOnKeystroke(true);
        if (!tableModel.hasEmptyRow()) {
            tableModel.addEmptyRow();
        }

        pimpTable();

        scroller = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 300));

        for (int column = 0; column < tableModel.getColumnCount(); column++) {

            TableColumn tableColumn = table.getColumnModel().getColumn(column);

            tableColumn.setCellRenderer(tableModel.getTableCellRenderer(column));

            tableColumn.setCellEditor(tableModel.getTableCellEditor(column));

            if (tableModel.getHiddenIndex() == column) {
                tableColumn.setMinWidth(2);
                tableColumn.setPreferredWidth(2);
                tableColumn.setMaxWidth(2);
                tableColumn.setCellRenderer(new InteractiveRenderer(tableModel.getHiddenIndex()));
            } else {
                tableColumn.setMinWidth(tableModel.getMinWidth(column));
                tableColumn.setMaxWidth(tableModel.getMaxWidth(column));
                tableColumn.setPreferredWidth(tableModel.getPreferredWidth(column));
            }
        }

        setLayout(new BorderLayout());
        add(scroller, BorderLayout.CENTER);

        statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.setVisible(false);
        add(statusPanel, BorderLayout.SOUTH);

//        java.util.List<ATableButton> buttons = tableModel.getButtons();
//        if (buttons != null) {
//            JPanel buttonPanel = new JPanel();
//            //setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
//
//            for (ATableButton button : buttons) {
//                JPanel bP = new JPanel();
//                bP.add(button);
//                buttonPanel.add(bP);
//            }
//
//            add(buttonPanel, BorderLayout.SOUTH);
//        }
    }

    private void pimpTable() {

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    calculateSum(table);
                }
            }
        });

        table.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.isShiftDown()) {
                    calculateSum(table);
                }
            }
        });

        ATableHeaderRenderer headerRenderer = new ATableHeaderRenderer(tableModel, true);

        for (int c = 0; c < table.getColumnModel().getColumnCount(); c++) {
            TableColumn column = table.getColumnModel().getColumn(c);
            column.setHeaderRenderer(headerRenderer);
        }

    }

    private void calculateSum(JTable table) {
        boolean changed = false;
        for (int c = 0; c < table.getColumnModel().getColumnCount(); c++) {
            TableColumn column = table.getColumnModel().getColumn(c);
            if (tableModel.isSummable(c)) {
                TableCellRenderer headerRenderer = column.getHeaderRenderer();
                if (headerRenderer instanceof ATableHeaderRenderer) {
                    ((ATableHeaderRenderer) headerRenderer).calculateSum(table, c);
                }
                changed = true;
            }
        }
        if (changed) {
            table.getTableHeader().repaint();
        }

    }

    public void highlightLastRow(int row) {
        int lastrow = tableModel.getRowCount();
        if (row == lastrow - 1) {
            table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
        } else {
            table.setRowSelectionInterval(row + 1, row + 1);
        }

        table.setColumnSelectionInterval(0, 0);
    }

    class InteractiveRenderer extends DefaultTableCellRenderer {

        protected int interactiveColumn;

        public InteractiveRenderer(int interactiveColumn) {
            this.interactiveColumn = interactiveColumn;
        }

        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus, int row,
                                                       int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == interactiveColumn && hasFocus) {
                if ((ATableForm.this.tableModel.getRowCount() - 1) == row &&
                        !ATableForm.this.tableModel.hasEmptyRow()) {
                    ATableForm.this.tableModel.addEmptyRow();
                }

                highlightLastRow(row);
            }

            return c;
        }
    }

    public class InteractiveTableModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent evt) {
            if (evt.getType() == TableModelEvent.UPDATE) {
                int column = evt.getColumn();
                int row = evt.getFirstRow();
                System.out.println("row: " + row + " column: " + column);
                table.setColumnSelectionInterval(column + 1, column + 1);
                if ((row) < table.getRowCount()) {
                    table.setRowSelectionInterval(row, row);
                }
            }
        }
    }


}