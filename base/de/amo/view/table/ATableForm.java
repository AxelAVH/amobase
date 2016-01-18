package de.amo.view.table;

/**
 * Created by private on 17.01.2016.
 */

import de.amo.view.cellrenderer.AInteger2FloatCellEditor;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ATableForm extends JPanel {

    protected JTable table;
    protected JScrollPane scroller;
    protected ATableModel tableModel;

    public ATableForm(ATableModel aTableModel) {
        tableModel = aTableModel;
        initComponent();
    }

    public void initComponent() {

        tableModel.addTableModelListener(new ATableForm.InteractiveTableModelListener());
        table = new JTable();
        table.setModel(tableModel);
        table.setSurrendersFocusOnKeystroke(true);
        if (!tableModel.hasEmptyRow()) {
            tableModel.addEmptyRow();
        }

        scroller = new JScrollPane(table);
        table.setPreferredScrollableViewportSize(new java.awt.Dimension(500, 300));

        for (int column = 0; column < tableModel.getColumnCount(); column++) {
            TableCellRenderer tableCellRenderer = tableModel.getTableCellRenderer(column);
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            tableColumn.setCellRenderer(tableCellRenderer);

            if (tableModel.getHiddenIndex() == column) {
                tableColumn.setMinWidth(2);
                tableColumn.setPreferredWidth(2);
                tableColumn.setMaxWidth(2);
                tableColumn.setCellRenderer(new InteractiveRenderer(tableModel.getHiddenIndex()));
            }

            if (column ==4) {
                tableColumn.setCellEditor(new AInteger2FloatCellEditor(0,100));
            }
        }

        setLayout(new BorderLayout());
        add(scroller, BorderLayout.CENTER);

        java.util.List<ATableButton> buttons = tableModel.getButtons();
        if (buttons != null) {
            JPanel buttonPanel = new JPanel();
            //setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

            for (ATableButton button : buttons) {
                JPanel bP = new JPanel();
                bP.add(button);
                buttonPanel.add(bP);
            }

            add(buttonPanel, BorderLayout.SOUTH);
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
                                                       int column)
        {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == interactiveColumn && hasFocus) {
                if ((ATableForm.this.tableModel.getRowCount() - 1) == row &&
                        !ATableForm.this.tableModel.hasEmptyRow())
                {
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
                table.setRowSelectionInterval(row, row);
            }
        }
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            JFrame frame = new JFrame("Interactive Form");
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    System.exit(0);
                }
            });
            AudioRecordTableModel aTableModel = new AudioRecordTableModel();

            ATableButton saveButton = new ATableButton(){
                @Override
                public void execute() {
                    System.out.println("Hallo, hier wird gesaved !!!!!!!!!!!!!!");
                }
            };
            aTableModel.addButton(saveButton);
            saveButton.setText("Save");

            ATableForm comp = new ATableForm(aTableModel);
            frame.getContentPane().add(comp);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}