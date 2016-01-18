package de.amo.view;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by private on 05.01.2016.
 */
public class ANumberInputField extends JFormattedTextField {

    int scale = 0;

    private ANumberInputField(BigDecimal bigDecimal, DecimalFormat format) {
        super(format);
        setValue(bigDecimal);
    }


    public static ANumberInputField create(int integer, int nachkommastellen) {
        BigDecimal bigDecimal = null;

        if (Integer.MIN_VALUE != integer) {
            bigDecimal = new BigDecimal(integer).movePointLeft(nachkommastellen);
        }

        DecimalFormat format = new DecimalFormat();

        String pattern = "#.##0";
        if (nachkommastellen > 0) {
            pattern += ",";
            for (int i = 0; i < nachkommastellen; i++) {
                pattern += "0";
            }
        }

        format.applyLocalizedPattern(pattern);
        ANumberInputField inputField = new ANumberInputField(bigDecimal, format);
        inputField.scale = nachkommastellen;
        inputField.setHorizontalAlignment(JTextField.RIGHT);

        // Begrenzung der zulässigen Eingaben:
        inputField.addKeyListener(new KeyListener() {
                                      @Override
                                      public void keyTyped(KeyEvent e) {
                                          String allowed = "0123456789,.";
                                          Character typed = e.getKeyChar();
                                          if (allowed.indexOf(typed) < 0) {
                                              e.setKeyChar(e.CHAR_UNDEFINED);
                                          }
                                      }
                                      @Override
                                      public void keyPressed(KeyEvent e) {
                                      }

                                      @Override
                                      public void keyReleased(KeyEvent e) {
                                      }
                                  }
        );
        return inputField;
    }


    public void setValue(int integer, int nachkommastellen) {

        if (Integer.MIN_VALUE == integer) {
            return;
        }

        BigDecimal bigDecimal = new BigDecimal(integer).movePointLeft(nachkommastellen);
        setValue(bigDecimal);
    }

    public int getIntValue() {
        BigDecimal bigDecimal = null;
        Object value = getValue();
        if (value == null) {
            return Integer.MIN_VALUE;
        }
        if (value instanceof BigDecimal) {
            bigDecimal = (BigDecimal) value;
        } else if (value instanceof Double) {
            Double d = (Double) value;
            bigDecimal = new BigDecimal(d);
        } else if (value instanceof Long) {
            Long l = (Long) value;
            bigDecimal = new BigDecimal(l);
        } else {
            throw new RuntimeException("Unerwarteter Rückgabe-Value : " + value.getClass());
        }
        bigDecimal = bigDecimal.movePointRight(scale);
        return bigDecimal.intValue();
    }

}
