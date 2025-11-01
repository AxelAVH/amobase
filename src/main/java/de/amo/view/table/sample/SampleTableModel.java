package de.amo.view.table.sample;

import de.amo.view.fachwerte.Fachwert;
import de.amo.view.table.ATableModel;
import de.amo.view.table.AudioRecord;

import java.util.List;

/**
 * Created by private on 17.01.2016.
 */
public class SampleTableModel extends ATableModel {


    public SampleTableModel(List<Fachwert> fachwerte) {
        super(fachwerte);
    }

    @Override
    public Object getValueAt(Object record, String columName) {

        AudioRecord record1 = (AudioRecord) record;

        if ("Title".equals(columName)) return record1.getTitle();
        if ("Artist".equals(columName)) return record1.getArtist();
        if ("Album".equals(columName)) return record1.getAlbum();
        if ("Datum".equals(columName)) return record1.getDatum();
        if ("Preis".equals(columName)) return record1.getPreis();
        if ("PreisInCent".equals(columName)) return record1.getPreisInCent();
        return new Object();
    }

    @Override
    public void setValueAt(Object value, Object record, String columName) {
        AudioRecord record1 = (AudioRecord) record;
        if ("Title".equals(columName)) record1.setTitle((String) value);
        if ("Artist".equals(columName)) record1.setArtist((String) value);
        if ("Album".equals(columName)) record1.setAlbum((String) value);
        if ("Datum".equals(columName)) record1.setDatum((String) value);
        if ("Preis".equals(columName)) record1.setPreis((Double) value);
        if ("PreisInCent".equals(columName)) record1.setPreisInCent((Integer) value);
        System.out.println("invalid index");
    }

    @Override
    public boolean isRecordEmpty(Object record) {
        AudioRecord record1 = (AudioRecord) record;
        if (record1.getTitle() != null && record1.getTitle().trim().length() > 0) return false;
        if (record1.getArtist() != null && record1.getArtist().trim().length() > 0) return false;
        if (record1.getAlbum() != null && record1.getAlbum().trim().length() > 0) return false;
        if (record1.getDatum() != null && record1.getDatum().trim().length() > 0) return false;
        return true;
    }

    @Override
    public Object createEmptyRecord() {
        return new AudioRecord();
    }
}
