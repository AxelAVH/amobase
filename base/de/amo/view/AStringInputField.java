package de.amo.view;

import javax.swing.*;

/**
 * Created by private on 05.01.2016.
 */
public class AStringInputField extends JTextField {

    protected AStringInputField(String text) {
        super(text);
    }

    public static AStringInputField create(String defaultValue) {
        return new AStringInputField(defaultValue);
    }

    public void setValue(String text) {
        setText(text);
    }

    public String getValue() {
        return getText();
    }
}
