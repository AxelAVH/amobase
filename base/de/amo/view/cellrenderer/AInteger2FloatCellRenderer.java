package de.amo.view.cellrenderer;

import de.amo.tools.StringFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Created by private on 06.09.2015.
 */
public class AInteger2FloatCellRenderer extends DefaultTableCellRenderer {

    public void setValue(Object value) {
        if (value == null) {
            super.setValue(null);
            return;
        }

        int intValue = (int) value;

        super.setValue(StringFormatter.getFloatStringFromIntString("" + intValue));
    }



    @Override
    public int getHorizontalAlignment() {
        return SwingConstants.RIGHT;
    }
}
