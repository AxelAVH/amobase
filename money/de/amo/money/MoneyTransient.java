package de.amo.money;

import de.amo.tools.Datum;
import de.amo.tools.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by private on 06.09.2015.
 */
public class MoneyTransient {

    int     saldo   = 0;
    int     psaldo  = 0;
    String  message = "";
    boolean isSaved = true;

    int pToNullBetrag = 0;  // Was m�sste �berwiesen werden, damit der pSaldo auf 0 geht?
    int forecastSumme = 0;

    String monatsAbgrenzDatum        = null;
    int    pSaldoMonatsanfang        = 0;
    int    pToNullBetragMonatsanfang = 0;

    File lastBackupDatabaseFile;

    SortedMap<String, Buchungszeile>    buchungszeilen          = new TreeMap<String, Buchungszeile>();
    List<Buchungszeile>                 sortierteBuchungszeilen = new ArrayList<Buchungszeile>();

    File umsatzdateienDownloadDir       = Environment.getOS_DownloadDir();


    List<Buchungszeile> forecast = new ArrayList<Buchungszeile>();

    List<File> eingelesesUmsatzDateien = new ArrayList<File>();

    public int getSaldo() {
        return saldo;
    }

    public int getPsaldo() {
        return psaldo;
    }

    public int getpToNullBetrag() {
        return pToNullBetrag;
    }

    public int getForecastSumme() {
        return forecastSumme;
    }

    public void setForecastSumme(int forecastSumme) {
        this.forecastSumme = forecastSumme;
    }

    public String getMonatsAbgrenzDatum() {
        return monatsAbgrenzDatum;
    }

    public int getpSaldoMonatsanfang() {
        return pSaldoMonatsanfang;
    }

    public int getpToNullBetragMonatsanfang() {
        return pToNullBetragMonatsanfang;
    }

    public SortedMap<String, Buchungszeile> getBuchungszeilen() {
        return buchungszeilen;
    }

    /**
     * Liefert die sortierten Buchungss�tze
     * @return
     */
    public List<Buchungszeile> getAktuelleDaten() {
        return sortierteBuchungszeilen;
    }

    public List<Buchungszeile> getForecast() {
        return forecast;
    }

    public List<File> getEingelesesUmsatzDateien() {
        return eingelesesUmsatzDateien;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public File getLastBackupDatabaseFile() {
        return lastBackupDatabaseFile;
    }

    public void setLastBackupDatabaseFile(File lastBackupDatabaseFile) {
        this.lastBackupDatabaseFile = lastBackupDatabaseFile;
    }


    public File getUmsatzdateienDownloadDir() {
        return umsatzdateienDownloadDir;
    }

    /** Berechnet die Salden anhand der aktuellen Buchungszeilen
     */
    public void recalculate() {

        saldo  = 0;
        psaldo = 0;
        pSaldoMonatsanfang = 0;
        message = "";

        monatsAbgrenzDatum = null;
        String heute = Datum.heute();
        String tag   = heute.substring(6,8);
        if (tag.startsWith("0")) {
            monatsAbgrenzDatum = heute.substring(0,6) + "00";
        } else {
            monatsAbgrenzDatum = heute;
        }

        sortierteBuchungszeilen = new Sortierer().sortiere(buchungszeilen);

        int lastHauptbuchungsnr = 0;

        for (Buchungszeile buchungszeile : sortierteBuchungszeilen) {
            saldo  += buchungszeile.betrag;
            psaldo += buchungszeile.pbetrag;
            buchungszeile.pSaldo = psaldo;

            if (buchungszeile.datum.compareTo(monatsAbgrenzDatum) <= 0) {
                pSaldoMonatsanfang += buchungszeile.pbetrag;
            }

            if (buchungszeile.hauptbuchungsNr > 0) {
                lastHauptbuchungsnr = buchungszeile.hauptbuchungsNr;
            } else {
                lastHauptbuchungsnr++;
                buchungszeile.hauptbuchungsNr = lastHauptbuchungsnr;
            }

        }

        pToNullBetrag             = -(psaldo + forecastSumme);

        pToNullBetragMonatsanfang = -(pSaldoMonatsanfang + forecastSumme);
    }

    public void addUmbuchungszeilen(Buchungszeile pro, Buchungszeile contra) {

        int nextBuchungsnr = 0;
        int lastIndex      = 0;

        for (int i = 0; i < sortierteBuchungszeilen.size(); i++) {

            Buchungszeile bz = sortierteBuchungszeilen.get(i);

            if (bz.hauptbuchungsNr == 0) {
                throw new RuntimeException("Datenbank ist noch nicht migriiert.");
            }

            if (bz.hauptbuchungsNr != pro.hauptbuchungsNr) {
                continue;
            }

            if (bz.umbuchungNr >= nextBuchungsnr) {
                nextBuchungsnr = bz.umbuchungNr + 1;
            }

            lastIndex = i;
        }

        pro   .umbuchungNr = nextBuchungsnr;
        contra.umbuchungNr = nextBuchungsnr;

        sortierteBuchungszeilen.add(lastIndex + 1, pro);
        sortierteBuchungszeilen.add(lastIndex + 2, contra);
    }

    public int ermittleAnzahlUmsatzdateienImDownload() {
        int ret = 0;
        File dir = getUmsatzdateienDownloadDir();
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String name = file.getName().toLowerCase();
            if (name.startsWith("umsatzanzeige") && file.getName().endsWith(".csv")) {
                System.out.println("  Lese Datei " + file.getName());
                ret++;
            }
        }
        return ret;
    }

}
