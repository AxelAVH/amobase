package de.amo.view.fachwerte;

import de.amo.view.cellrenderer.ADoubleCellEditor;
import de.amo.view.cellrenderer.ADoubleCellRenderer;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Created by private on 18.01.2016.
 */
public class FachwertDouble extends Fachwert {

    public FachwertDouble(String attributName) {
        super(attributName);
    }

    @Override
    public Class getColumnClass() {
        return Double.class;
    }

    @Override
    public TableCellRenderer getTableCellRenderer() {
        return new ADoubleCellRenderer();
    }

    @Override
    public TableCellEditor getTableCellEditor() {
        return new ADoubleCellEditor();
    }
}
