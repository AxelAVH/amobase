package de.amo.view.table.sample;

import de.amo.view.fachwerte.Fachwert;
import de.amo.view.table.ATableButton;
import de.amo.view.table.ATableForm;
import de.amo.view.table.AudioRecord;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by private on 18.01.2016.
 */
public class SampleTable {

    public static void main(String[] args) {
        try {

            Fachwert fw_Artis = SampleFachwerte.getFachwert_Artist();
            Fachwert fw_Album = SampleFachwerte.getFachwert_Album();
            Fachwert fw_Title = SampleFachwerte.getFachwert_Title();
            Fachwert fw_Preis = SampleFachwerte.getFachwert_Preis();
            Fachwert fw_Datum = SampleFachwerte.getFachwert_Datum();
//            Fachwert fw_PreisInCent = SampleFachwerte.getFachwert_PreisInCent();

            List<Fachwert> fachwerte = new ArrayList<>();
            fachwerte.add(fw_Artis);
            fachwerte.add(fw_Album);
            fachwerte.add(fw_Title);
            fachwerte.add(fw_Datum);
//            fachwerte.add(fw_PreisInCent);
            fachwerte.add(fw_Preis);

            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            JFrame frame = new JFrame("Interactive Form");
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    System.exit(0);
                }
            });
            final SampleTableModel aTableModel = new SampleTableModel(fachwerte);

            ATableButton saveButton = new ATableButton(){
                @Override
                public void execute() {
                    System.out.println("Hallo, hier wird gesaved !!!!!!!!!!!!!!");

                    Vector dataVector = aTableModel.getDataVector();
                    for (int i = 0; i < dataVector.size(); i++) {
                        AudioRecord record = (AudioRecord) dataVector.get(i);

                        System.out.println("Artist: " + record.getArtist()
                                + "| Album: " + record.getAlbum()
                                + "| Title: " + record.getTitle()
                                + "| Preis: " + record.getPreis()
                                + "| Preis in Cent: " + record.getPreisInCent()
                                + "| Datum: " + record.getDatum());
                    }
                }
            };
            aTableModel.addButton(saveButton);
            saveButton.setText("Save");

            ATableForm comp = new ATableForm(aTableModel);
            comp.setStatusLabel(new JLabel("Das ist ein Status"));
            frame.getContentPane().add(comp);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
