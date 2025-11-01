package de.amo.view.table.sample;

import de.amo.view.cellrenderer.*;
import de.amo.view.fachwerte.Fachwert;
import de.amo.view.fachwerte.FachwertDatum;
import de.amo.view.fachwerte.FachwertDouble;
import de.amo.view.fachwerte.FachwertString;

import javax.swing.table.DefaultTableCellRenderer;
import java.util.Date;

/**
 * Created by private on 18.01.2016.
 */
public class SampleFachwerte {

    public static Fachwert getFachwert_Artist() {
        Fachwert fw = new FachwertString("Artist");

        fw.setColumName("Artist");
        fw.setPreferredWidth(50);
        fw.setMinWidth(30);
        fw.setMaxWidth(100);
        fw.setLabel30("Artist");

        return fw;
    }

    public static Fachwert getFachwert_Album() {
        Fachwert fw = new FachwertString("Album");

        fw.setColumName("Album");
        fw.setPreferredWidth(50);
        fw.setMinWidth(30);
        fw.setMaxWidth(100);
        fw.setLabel30("Album");

        return fw;
    }


    public static Fachwert getFachwert_Title() {
        Fachwert fw = new FachwertString("Title");

        fw.setColumName("Title");
        fw.setPreferredWidth(100);
        fw.setMinWidth(70);
        fw.setMaxWidth(200);
        fw.setLabel30("Title");

        return fw;
    }

    public static Fachwert getFachwert_Preis() {

        Fachwert fw = new FachwertDouble("Preis");

        fw.setColumName("Preis");
        fw.setPreferredWidth(700);
        fw.setMinWidth(70);
        fw.setMaxWidth(70);
        fw.setLabel30("Preis");

        return fw;
    }

//    public static Fachwert getFachwert_PreisInCent() {
//
//        Fachwert fw = new Fachwert();
//
//        fw.setTableCellRenderer(new ADecimalIntegerCellRenderer());
//        fw.setTableCellEditor(new ADecimalIntegerCellEditor(0,10000));
//        fw.setColumnClass(Integer.class);
//
//        fw.setColumName("PreisInCent");
//        fw.setPreferredWidth(700);
//        fw.setMinWidth(70);
//        fw.setMaxWidth(70);
//        fw.setAttributName("PreisInCent");
//        fw.setLabel30("PreisInCent");
//
//        return fw;
//    }

    public static Fachwert getFachwert_Datum() {

        Fachwert fw = new FachwertDatum("Datum");

        fw.setColumName("Datum");
        fw.setPreferredWidth(700);
        fw.setMinWidth(70);
        fw.setMaxWidth(70);
        fw.setLabel30("Datum");

        return fw;
    }

}
