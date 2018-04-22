package de.amo.money;

import de.amo.tools.Datum;
import de.amo.tools.Environment;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by private on 06.09.2015.
 */
public class MoneyTransient {

    int     saldo   = 0;
    int     psaldo  = 0;
    String  message = "";
    boolean isSaved = true;
    String  kontonnr = "";

//    int pToNullBetrag = 0;  // Was m�sste �berwiesen werden, damit der pSaldo auf 0 geht?
//    int forecastSumme = 0;

    String monatsAbgrenzDatum        = null;
//    int    pSaldoMonatsanfang        = 0;
//    int    pToNullBetragMonatsanfang = 0;

    File lastBackupDatabaseFile;

    SortedMap<String, Buchungszeile>    buchungszeilen          = new TreeMap<String, Buchungszeile>();
    List<Buchungszeile>                 sortierteBuchungszeilen = new ArrayList<Buchungszeile>();

    File umsatzdateienDownloadDir;

    public MoneyTransient(String kontonummer, File umsatzdateienDownloadDir) {
        this.umsatzdateienDownloadDir =  umsatzdateienDownloadDir;
        this.kontonnr                   = kontonummer;
    }

    List<File> eingelesesUmsatzDateien = new ArrayList<File>();

    public int getSaldo() {
        return saldo;
    }

    public int getPsaldo() {
        return psaldo;
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

    public List<File> getEingelesesUmsatzDateien() {
        return eingelesesUmsatzDateien;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public void setLastBackupDatabaseFile(File lastBackupDatabaseFile) {
        this.lastBackupDatabaseFile = lastBackupDatabaseFile;
    }

    public String getKontonnr() {
        return kontonnr;
    }

    public void setKontonnr(String kontonnr) {
        this.kontonnr = kontonnr;
    }


    /** Berechnet die Salden anhand der aktuellen Buchungszeilen
     */
    public void recalculate() {

        saldo  = 0;
        psaldo = 0;
//        pSaldoMonatsanfang = 0;
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
        boolean isFirst = true;

        for (Buchungszeile buchungszeile : sortierteBuchungszeilen) {
            if (isFirst) {
                isFirst = false;
                saldo = buchungszeile.saldo;
            } else {
                saldo += buchungszeile.betrag;
            }

            if (buchungszeile.hauptbuchungsNr > 0) {
                lastHauptbuchungsnr = buchungszeile.hauptbuchungsNr;
            } else {
                lastHauptbuchungsnr++;
                buchungszeile.hauptbuchungsNr = lastHauptbuchungsnr;
            }

        }

        // Aufteilen der als Umlage markierten Beträge in Jahres-Zwölftel
        Map<String,Integer> jahresumlagevolumen = new HashMap<>(  );

        for (Buchungszeile buchungszeile : sortierteBuchungszeilen) {
            String kommentar = buchungszeile.kommentar;
            if (kommentar != null) {
                kommentar = kommentar.toLowerCase();
            } else {
                kommentar = "";
            }
            if (kommentar.startsWith( "$umlage$")) {

                String value  = kommentar.substring( "$umlage$".length() ).replace( ",", "." );
                double d      = Double.valueOf( value );
                BigDecimal bd = new BigDecimal(d);
                bd            = bd.movePointRight(2);
                int umlagevolumen = bd.intValue();

                String jahr       = buchungszeile.datum.substring( 0,4 );
                Integer jahresVol = jahresumlagevolumen.get(jahr);
                if (jahresVol == null) {
                    jahresVol = new Integer( 0 );
                }
                jahresumlagevolumen.put(jahr, jahresVol + umlagevolumen);
            }
        }

        String jahr         = "";
        String monat        = "";
        int    monatsUmlage = 0;
        int    jahresUmlage = 0;

        for ( Buchungszeile buchungszeile : sortierteBuchungszeilen ) {

            String aktJahr  = buchungszeile.datum.substring( 0, 4 );
            String aktMonat = buchungszeile.datum.substring( 0, 6 );

            if ( !aktJahr.equals( jahr ) ) {
                jahr = aktJahr;
                Integer b = jahresumlagevolumen.get( aktJahr );
                if ( b != null ) {
                    jahresUmlage = b;
                } else {
                    jahresUmlage = 0;
                }
                monatsUmlage = 0;
            }

            // ToDo: wenn ein Monat keine Werte hätte, würde für ihn nicht hochgezählt werden
            if ( !aktMonat.equals( monat ) ) {
                monat        = aktMonat;
                monatsUmlage = monatsUmlage + jahresUmlage / 12;
            }

            String kommentar = buchungszeile.kommentar;
            if (kommentar != null) {
                kommentar = kommentar.toLowerCase();
            } else {
                kommentar = "";
            }
            if (kommentar.startsWith( "$umlage$")) {

                String value = kommentar.substring( "$umlage$".length() );
                value = value.replace( ",", "." );
                double d = Double.valueOf( value );
                BigDecimal bd = new BigDecimal( d );
                bd = bd.movePointRight( 2 );
                int umlagevolumen = bd.intValue();
                monatsUmlage = monatsUmlage - umlagevolumen;
            }

            buchungszeile.saldoGeglaettet = buchungszeile.saldo + monatsUmlage;
        }
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

//    public int ermittleAnzahlUmsatzdateienImDownload() {
//        int ret = 0;
//        File dir = getUmsatzdateienDownloadDir();
//        File[] files = dir.listFiles();
//        for (int i = 0; i < files.length; i++) {
//            File file = files[i];
//            String name = file.getName().toLowerCase();
//            if (name.startsWith("umsatzanzeige") && file.getName().endsWith(".csv")) {
//                System.out.println("  Lese Datei " + file.getName());
//                ret++;
//            }
//        }
//        return ret;
//    }
//
}
