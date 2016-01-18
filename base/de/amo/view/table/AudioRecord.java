package de.amo.view.table;

/**
 * Created by private on 17.01.2016.
 */
public class AudioRecord {
    protected String title;
    protected String artist;
    protected String album;
    protected String datum;
    protected Double preis;
    protected Integer preisInCent;

    public AudioRecord() {
        title = "";
        artist = "";
        album = "";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public Double getPreis() {
        return preis;
    }

    public void setPreis(Double preis) {
        this.preis = preis;
    }

    public Integer getPreisInCent() {
        return preisInCent;
    }

    public void setPreisInCent(Integer preisInCent) {
        this.preisInCent = preisInCent;
    }
}