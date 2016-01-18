package de.amo.view.cellrenderer;

import de.amo.tools.StringFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Created by private on 18.01.2016.
 */
public class AIntegerCellRenderer extends DefaultTableCellRenderer {

    public void setValue(Object value) {
        if (value == null) {
            super.setValue(null);
            return;
        }

        int intValue = (int) value;

        super.setValue("" + intValue);
    }



    @Override
    public int getHorizontalAlignment() {
        return SwingConstants.RIGHT;
    }


}
