package de.amo.view.fachwerte;

import de.amo.view.cellrenderer.ADatumCellrenderer;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.Date;

/**
 * Created by private on 18.01.2016.
 */
public class FachwertDatum extends Fachwert {

    public FachwertDatum(String attributName) {
        super(attributName);
    }

    @Override
    public Class getColumnClass() {
        return Date.class;
    }

    @Override
    public TableCellRenderer getTableCellRenderer() {
        return new ADatumCellrenderer();
    }

    @Override
    public TableCellEditor getTableCellEditor() {
        return null;        // das bewirkt wohl den Default
    }
}
