package de.amo.view;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Created by private on 28.01.2016.
 */

public class AToolTipHeader extends JTableHeader {

    Map<String,String> toolTips;

    /**
     *
     * @param model
     * @param toolTips key: der Spaltenname, Value der auszugebende Tooltip-Text
     */
    public AToolTipHeader(TableColumnModel model, Map<String,String> toolTips) {
        super(model);
        this.toolTips = toolTips;
    }

    public String getToolTipText(MouseEvent e) {

        int         col         = columnAtPoint(e.getPoint());
        int         modelCol    = getTable().convertColumnIndexToModel(col);
        TableColumn column      = getColumnModel().getColumn(modelCol);
        String      retStr      = toolTips.get(column.getIdentifier());

        if (retStr == null) {
            retStr = "Keine Tooltip-Unterstützung";
        }

        return retStr;
    }
}


