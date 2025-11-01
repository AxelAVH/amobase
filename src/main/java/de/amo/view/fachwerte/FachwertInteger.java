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
        tableCellEditor = new AIntegerCellEditor();
        tableCellRenderer = new AIntegerCellRenderer();
        columnClass = Integer.class;
    }

}
