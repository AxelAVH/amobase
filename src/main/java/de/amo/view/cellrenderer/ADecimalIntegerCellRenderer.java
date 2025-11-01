package de.amo.view.cellrenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by private on 18.01.2016.
 */
public class ADecimalIntegerCellRenderer extends DefaultTableCellRenderer {

    String pattern = "###,##0.00";

    public ADecimalIntegerCellRenderer() {
    }

    public ADecimalIntegerCellRenderer(String pattern) {
        this.pattern = pattern;
    }

    public void setValue(Object value) {
        if (value == null) {
            super.setValue(null);
            return;
        }

        double doubleValue = (double) value;

        Locale loc = Locale.GERMANY;
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(pattern);

        super.setText(df.format(doubleValue));
    }



    @Override
    public int getHorizontalAlignment() {
        return SwingConstants.RIGHT;
    }


    public static void main(String[] args) {

        String pattern = "###,##0.00";
        Locale loc = Locale.GERMANY;
        NumberFormat nf = NumberFormat.getNumberInstance(loc);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern(pattern);

        System.out.println(pattern + " " + df.format(1234567.890) + " " + loc.toString());
        System.out.println(pattern + " " + df.format(1234567.8) + " " + loc.toString());
        System.out.println(pattern + " " + df.format(1234567) + " " + loc.toString());
        System.out.println(pattern + " " + df.format(0.04) + " " + loc.toString());
    }

}
