package de.amo.view.fachwerte;

import de.amo.view.cellrenderer.AStringCellEditor;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * Created by private on 18.01.2016.
 */
public class FachwertString extends Fachwert {

    public FachwertString(String attributName) {
        super(attributName);
        tableCellEditor     = new AStringCellEditor();
        tableCellRenderer   = new DefaultTableCellRenderer();
        columnClass         = String.class;
    }

}
