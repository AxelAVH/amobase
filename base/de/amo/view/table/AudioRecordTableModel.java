package de.amo.view.table;

import de.amo.view.cellrenderer.DatumsCellrenderer;
import de.amo.view.cellrenderer.Integer2FloatCellRenderer;

import javax.swing.table.TableCellRenderer;

/**
 * Created by private on 17.01.2016.
 */
public class AudioRecordTableModel extends ATableModel {

    private static String[] names = new String[]{"Titel","Artist", "Album","Datum", "Preis"};

    public AudioRecordTableModel() {
        super(names);
    }

    @Override
    public Object getValueAt(Object record, int column) {

        AudioRecord record1 = (AudioRecord) record;

        switch (column) {
            case 0:
                return record1.getTitle();
            case 1:
                return record1.getArtist();
            case 2:
                return record1.getAlbum();
            case 3:
                return record1.getDatum();
            case 4:
                return record1.getPreis();
            default:
                return new Object();
        }
    }

    @Override
    public TableCellRenderer getTableCellRenderer(int column) {
        if (column == 3) {
            return new DatumsCellrenderer();
//        } else if (column == 4) {
//            return new Integer2FloatCellRenderer();
        } else {
            super.getTableCellRenderer(column);
        }
        return null;
    }

    @Override
    public void setValueAt(Object value, Object record, int column) {
        AudioRecord record1 = (AudioRecord) record;
        switch (column) {
            case 0:
                record1.setTitle((String) value);
                break;
            case 1:
                record1.setArtist((String) value);
                break;
            case 2:
                record1.setAlbum((String) value);
                break;
            case 3:
                record1.setDatum((String) value);
                break;
            case 4:
                record1.setPreis((Integer) value);
                break;
            default:
                System.out.println("invalid index");
        }
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
