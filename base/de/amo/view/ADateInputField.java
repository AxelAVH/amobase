package de.amo.view;

import de.amo.tools.Datum;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by private on 05.01.2016.
 */
public class ADateInputField extends JFormattedTextField {

    public static int DATEINPUTLENGTH = 70;

    private ADateInputField(String value) {
        super(new SimpleDateFormat("dd.MM.yyy"));

        if (value != null && !"".equals(value)) {
            Date date = Datum.asDate(value);
            setValue(date);
        }
    }

    public static ADateInputField create(String value) {

        ADateInputField dateInputField = new ADateInputField(value);

/*
        AbstractFormatter formatter = new AbstractFormatter() {

            @Override
            public Object stringToValue(String text) throws ParseException {
                Date value = Datum.asDate(text);
                return value;
            }

            @Override
            public String valueToString(Object value) throws ParseException {
                if (value == null) {
                    return "11.11.1111";
                }
                Date dateValue = (Date) value;
                return Datum.asString(dateValue);
            }

        };

        dateInputField.setFormatter(formatter);
*/

        dateInputField.addKeyListener(new KeyListener() {
                                          @Override
                                          public void keyTyped(KeyEvent e) {
                                              String allowed = "0123456789.";
  //                                            allowed += "\b";      // Backspace
  //                                            allowed += "\u007F";  // Entf.
                                              Character typed = e.getKeyChar();
                                              if (allowed.indexOf(typed) < 0) {

                                                  int code = e.getKeyCode();

                                                  if (KeyEvent.VK_BACK_SPACE == code) {
                                                      System.out.println("Treffer - Backspace");
                                                  } else if (KeyEvent.VK_DELETE == code) {
                                                      System.out.println("Treffer - Delete");
                                                  }else if (KeyEvent.VK_TAB == code) {
                                                      System.out.println("Treffer - Backspace");
                                                  }

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
        return dateInputField;
    }


    public String getDateString() {
        Date value = (Date) super.getValue();

        if (value == null) {
            return null;
        }

        return Datum.asString(value);
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value);
    }

}
