package de.amo.view.cellrenderer;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;

/**
 * Created by private on 18.01.2016.
 */
public class ADecimalIntegerCellEditor extends DefaultCellEditor {

    JFormattedTextField ftf;
    DecimalFormat doubleFormat;
    private Double minimum, maximum;
    private boolean DEBUG = false;
    String pattern = "###,##0.00";

    public ADecimalIntegerCellEditor(double min, double max) {

        super(new JFormattedTextField());
        ftf = (JFormattedTextField) getComponent();
        minimum = new Double(min);
        maximum = new Double(max);

        Locale loc = Locale.GERMANY;
        doubleFormat  = (DecimalFormat) NumberFormat.getNumberInstance(loc);
        doubleFormat.setParseBigDecimal(true);
        doubleFormat.applyPattern(pattern);

        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter .setValueClass(BigDecimal.class);
        doubleFormatter .setFormat(doubleFormat);
        doubleFormatter .setMinimum(minimum);
        doubleFormatter .setMaximum(maximum);

        ftf.setFormatterFactory(new DefaultFormatterFactory(doubleFormatter ));
        ftf.setValue(minimum);
        ftf.setHorizontalAlignment(JTextField.TRAILING);
        ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        //JFormattedTextField's focusLostBehavior property.)
        ftf.getInputMap().put(KeyStroke.getKeyStroke(
                KeyEvent.VK_ENTER, 0),
                "check");
        ftf.getActionMap().put("check", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (!ftf.isEditValid()) { //The text is invalid.
                    if (userSaysRevert()) { //reverted
                        ftf.postActionEvent(); //inform the editor
                    }
                } else try {              //The text is valid,
                    ftf.commitEdit();     //so use it.
                    ftf.postActionEvent(); //stop editing
                } catch (ParseException exc) {
                }
            }
        });
    }

    //Override to invoke setValue on the formatted text field.
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
        ftf.setValue(value);
        return ftf;
    }

    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        Object o = ftf.getValue();
        if (o instanceof Double) {
            return o;
        } else if (o instanceof Number) {
            return new Integer(((Number) o).intValue());
        } else {
            if (DEBUG) {
                System.out.println("getCellEditorValue: o isn't a Number");
            }
            try {
                return doubleFormat.parseObject(o.toString());
            } catch (ParseException exc) {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }

    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it's OK for the editor to go
    //away, we need to invoke the superclass's version
    //of this method so that everything gets cleaned up.
    public boolean stopCellEditing() {
        JFormattedTextField ftf = (JFormattedTextField) getComponent();
        if (ftf.isEditValid()) {
            try {
                ftf.commitEdit();
            } catch (ParseException exc) {
            }

        } else { //text is invalid
            if (!userSaysRevert()) { //user wants to edit
                return false; //don't let the editor go away
            }
        }
        return super.stopCellEditing();
    }

    /**
     * Lets the user know that the text they entered is
     * bad. Returns true if the user elects to revert to
     * the last good value.  Otherwise, returns false,
     * indicating that the user wants to continue editing.
     */
    protected boolean userSaysRevert() {
        Toolkit.getDefaultToolkit().beep();
        ftf.selectAll();
        Object[] options = {"Edit",
                "Revert"};
        int answer = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(ftf),
                "The value must be an integer between "
                        + minimum + " and "
                        + maximum + ".\n"
                        + "You can either continue editing "
                        + "or revert to the last valid value.",
                "Invalid Text Entered",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[1]);

        if (answer == 1) { //Revert!
            ftf.setValue(ftf.getValue());
            return true;
        }
        return false;
    }


    class MyDecimalFormat extends DecimalFormat {

        int scale = 2;
        int faktor = 100;


        @Override
        public Number parse(String text, ParsePosition pos) {
            Number number = super.parse(text, pos);
            if (number instanceof Double) {
                Double d = (Double) number;
                d = d * faktor;
                double floor = Math.floor(d);
                BigDecimal bd = new BigDecimal(floor);
                return new Integer(bd.intValue());
            }
            return super.parse(text, pos);
        }
    }
}