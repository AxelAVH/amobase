package de.amo.view.fachwerte;

import de.amo.view.cellrenderer.AIntegerCellEditor;
import de.amo.view.cellrenderer.AIntegerCellRenderer;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Created by private on 18.01.2016.
 */
public class FachwertInteger extends Fachwert {

    public FachwertInteger(String attributName) {
        super(attributName);

    }

    @Override
    public Class getColumnClass() {
        return Integer.class;
    }

    @Override
    public TableCellRenderer getTableCellRenderer() {
        return new AIntegerCellRenderer();
    }

    @Override
    public TableCellEditor getTableCellEditor() {
        return new AIntegerCellEditor();
    }
}
