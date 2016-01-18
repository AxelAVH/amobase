package de.amo.view.cellrenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Created by private on 06.09.2015.
 */
public class ADatumCellrenderer extends DefaultTableCellRenderer {

    public ADatumCellrenderer() {
        super();
    }

    public void setValue(Object value) {

        // Sicher ist sicher .... (kann aber eigentlich wieder weg
        if (value != null && !(value instanceof String)) {
            super.setValue("");
            return;
        }

        String datum = (String) value;

        if (datum == null) {
            super.setValue("");
        } else if (datum.length() == 8) {
            datum = datum.substring(6, 8) + "." + datum.substring(4, 6) + "." + datum.substring(0, 4);
            super.setValue(datum);
        } else {
            super.setValue(datum);
        }
    }


    @Override
    public int getHorizontalAlignment() {
        return SwingConstants.CENTER;
    }
}
