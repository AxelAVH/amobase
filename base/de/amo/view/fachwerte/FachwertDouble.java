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

        tableCellRenderer   = new ADoubleCellRenderer();
        tableCellEditor     = new ADoubleCellEditor();
        columnClass         = Double.class;
    }

}
