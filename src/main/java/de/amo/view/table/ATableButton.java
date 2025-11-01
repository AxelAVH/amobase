package de.amo.view.table;

import javax.swing.*;

/**
 * Created by private on 17.01.2016.
 */
public abstract class ATableButton extends JButton {

    ATableModel aTableModel;

    /* Soll nur vom ATableModel beim Registrieren des Buttons aufgerufen werden

     */
    protected void setATableModel(ATableModel aTableModel) {
        this.aTableModel = aTableModel;
    }


    public abstract void execute();
}
