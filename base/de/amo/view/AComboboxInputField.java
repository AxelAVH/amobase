package de.amo.view;

import javax.swing.*;
import java.util.List;
import java.util.Vector;

/**
 * Created by private on 13.01.2016.
 */
public class AComboboxInputField extends JComboBox {

    public static AComboboxInputField create(String aktValue, List<String> values) {

        ComboBoxModel model = createComboboxModel(values, aktValue);

        AComboboxInputField comboboxInputField = new AComboboxInputField();

        comboboxInputField.setModel(model);

        return comboboxInputField;
    }

    public String getText() {
        return (String) getModel().getSelectedItem();
    }

    private static ComboBoxModel createComboboxModel(List<String> values, String aktValue) {
        Vector v = new Vector();
        v.addAll(values);
        ComboBoxModel model = new DefaultComboBoxModel(v);
        model.setSelectedItem(aktValue);
        return model;

    }

    private void mist() {
        AbstractListModel m = null;
    }
}
