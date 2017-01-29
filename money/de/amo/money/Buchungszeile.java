package de.amo.money;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

/**
 * Created with IntelliJ IDEA.
 * User: amo
 * Date: 17.04.14
 * Time: 21:52
 * To change this template use File | Settings | File Templates.
 */
public class Buchungszeile implements Cloneable {

    /** Null oder "", sonst:  "V1" oder "V2" usw.
     */
    public String   formatversion;
    public int      hauptbuchungsNr;
    public int      umbuchungNr;
    public boolean  umbuchungsPro;

    public String datum             = "";
    public int    betrag            = 0;
    public String waehrung          = "";
    public int    saldo             = 0;
    public String buchungstext      = "";    // Gutschrift / Lastschrifteinzug / Dauerauftrag / ...
    public String quelleZiel        = "";
    public String verwendungszweck  = "";
    public int    pbetrag           = 0;
    public int    pSaldo            = 0;
    public String kommentar         = "";
    public String kategorie         = "";

    public boolean isAllerersterSatz  = false;

    public String getUniquenessKey() {
        String s = "                    " + betrag;
        s = s.substring(s.length() - 20);
        String t = "                    " + saldo;
        t = t.substring(t.length() - 20);
        String key = datum + "~" + s + "~" + t + "~" + quelleZiel.trim() + "~" + verwendungszweck.trim();

        if (isUmbuchung()) {    // Umbuchungssätze tauchen nur in der database-Datei auf, müssen nicht zwischen Buchungssätzen und Databasezeilen gemerged werden
            key = hauptbuchungsNr + "~" + umbuchungNr + "~" + key;
        }

        return key;
    }

    public boolean isUmbuchung() {
        return umbuchungNr > 0;
    }


    /** Achtung: die Umbuchungsnummer wird erst beim Einhängen in die Liste erzeugt
     */
    public List<Buchungszeile> createSplittbuchungen(int betrag, String kategorie, String kommentar) {
        Buchungszeile pro, contra;
        try {
            pro    = (Buchungszeile) this.clone();
            contra = (Buchungszeile) this.clone();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pro.pbetrag         = 0;
        contra.pbetrag      = 0;
        pro.betrag          = -betrag;
        contra.betrag       = betrag;
        pro.kategorie       = this.kategorie;
        contra.kategorie    = kategorie;
        pro.kommentar       = kommentar;
        contra.kommentar    = kommentar;
        pro.umbuchungsPro   = true;
        contra.umbuchungsPro=false;
        List<Buchungszeile> ret = new ArrayList<Buchungszeile>();
        ret.add(pro);
        ret.add(contra);
        return ret;
    }

    public static Buchungszeile fromDatabaseZeile(String zeile) {
        Buchungszeile b = new Buchungszeile();

        String[] columns    = getColumns(zeile);
        b.formatversion     = columns[0];
        if ("V1".equals(b.formatversion)) {
            b.formatversion     = columns[0];
            b.hauptbuchungsNr   = Integer.parseInt(columns[1]);
            if (!columns[2].startsWith("0")) {
                b.umbuchungNr   = Integer.parseInt(columns[2].substring(0, columns[2].length() - 1));
                b.umbuchungsPro = "a".equals(columns[2].substring(columns[2].length() - 1));
            }
            b.kategorie         = columns[3];
            b.datum             = columns[4];
            b.quelleZiel        = columns[5];
            b.buchungstext      = columns[6];
            b.verwendungszweck  = columns[7];
            b.betrag            = Integer.parseInt(columns[8]);
            b.waehrung          = columns[9];
            b.saldo             = Integer.parseInt(columns[10]);

            b.pbetrag           = Integer.parseInt(columns[11]);
            b.pSaldo            = Integer.parseInt(columns[12]);
            b.kommentar         = columns[13];
        } else {
            b.datum             = columns[0];
            b.quelleZiel        = columns[1];
            b.buchungstext      = columns[2];
            b.verwendungszweck  = columns[3];
            b.betrag            = Integer.parseInt(columns[4]);
            b.waehrung          = columns[5];
            b.saldo             = Integer.parseInt(columns[6]);

            b.pbetrag           = Integer.parseInt(columns[7]);
            b.pSaldo            = Integer.parseInt(columns[8]);
            b.kommentar         = columns[9];
        }

        return b;
    }

    private int fromDouble(double d) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.movePointRight(2);
        return bd.intValue();
    }

    private double fromInt(int i) {
        BigDecimal bd = new BigDecimal(i);
        bd = bd.movePointLeft(2);
        return bd.doubleValue();
    }

    public double getBetragAsDouble() {
        return fromInt(betrag);
    }

    public void setBetrag(double betrag) {
        this.betrag = fromDouble(betrag);
    }

    public double getSaldoAsDouble() {
        return fromInt(saldo);
    }

    public void setSaldo(double saldo) {
        this.saldo = fromDouble(saldo);
    }

    public double getPBetrag() {
        return fromInt(pbetrag);
    }

    public double getPBetragAsDouble() {
        return fromInt(pbetrag);
    }

    public void setPBetrag(double pbetrag) {
        this.pbetrag = fromDouble(pbetrag);
    }

    public double getPSaldoAsDouble() {
        return fromInt(pSaldo);
    }

    public void setPSaldo(double pSaldo) {
        this.pSaldo = fromDouble(pSaldo);
    }

    public static Buchungszeile fromIngDibaZeile(String zeile) {
        Buchungszeile b = new Buchungszeile();
        String[] columns = getColumns(zeile);
        String s            = columns[0];
        b.datum             = s.substring(6,10) + s.substring(3,5) + s.substring(0,2);
        b.quelleZiel        = columns[2];
        b.buchungstext      = columns[3];
        b.verwendungszweck  = columns[4];
        b.betrag            = readLong(columns[5]);
        b.waehrung          = columns[6];
        b.saldo             = readLong(columns[7]);
        b.pbetrag           = b.betrag;
        return b;
    }

    private static int readLong(String ds) {
        String orig = ds;
        if (ds==null) {
            return 0;
        }
        ds = ds.trim();
        if ("".equals(ds)) {
            return 0;
        }

        ds = ds.replace(".","");

        int pos = ds.indexOf(",");
        if (pos <0) {
            ds += ",00";
        } else if (pos == ds.length()-1) {
            ds += "00";
        } else if (pos == ds.length()-2) {
            ds += "0";
        }
        ds = ds.replace(",", "");
        int ret = 0;
        try {
            ret = Integer.parseInt(ds);
        } catch (Exception e ) {
            System.out.println("Fehler bei <"+orig+">");
        }
        return ret;
    }

    public String toDatabaseZeile() {
        String[] out = new String[14];
        out[0] = "V1";
        out[1] = "" + hauptbuchungsNr;
        out[2] = "" + umbuchungNr;
        if (umbuchungNr > 0) {
            if (umbuchungsPro) {
                out[2] += "a";
            } else {
                out[2] += "b";
            }
        }
        out[3] = kategorie;
        out[4] = datum;
        out[5] = quelleZiel;
        out[6] = buchungstext;
        out[7] = verwendungszweck;
        out[8] = "" + betrag;
        out[9] = waehrung;
        out[10] = "" + saldo;
        out[11] = "" + pbetrag;
        out[12] = "" + pSaldo;
        out[13] = kommentar;

        String zeile = "";
        for (int i = 0; i < out.length; i++) {
            if (out[i] == null) {
                out[i] = "";
            }
            out[i] = "\"" + out[i] + "\"";
            if (i != 0) {
                out[i] = ";" + out[i];
            }
            zeile += out[i];
        }
        return zeile;
    }

    public String toPexportZeile() {
        if (pbetrag == 0) {
            return null;
        }
        String[] out = new String[7];
        out[0] = datum;
        out[1] = quelleZiel;
        out[2] = buchungstext;
        out[3] = verwendungszweck;
//        out[4] = "" + betrag;
        out[4] = waehrung;
//        out[6] = "" + saldo;
        out[5] = "" + pbetrag;
        out[6] = "" + pSaldo;
//        out[9] = kommentar;

        String zeile = "";
        for (int i = 0; i < out.length; i++) {
            if (out[i] == null) {
                out[i] = "";
            }
            out[i] = "\"" + out[i] + "\"";
            if (i != 0) {
                out[i] = ";" + out[i];
            }
            zeile += out[i];
        }
        return zeile;
    }

    public String toShow() {
        return datum.substring(6, 8) + "." + datum.substring(4, 6) + "." + datum.substring(0, 4) + " | " +
                (hauptbuchungsNr + " | " + umbuchungNr + " | ") +
                (quelleZiel + "                                                 ").substring(0, 30) + " | " +
                (buchungstext + "                                               ").substring(0, 20) + " | " +
                (verwendungszweck + "                                           ").substring(0, 40) + " | " +
                formatLongForEuroOutput(betrag) + "|" +
                formatLongForEuroOutput(saldo) + "|" +
                formatLongForEuroOutput(pbetrag) + "|" +
                formatLongForEuroOutput(pSaldo) + "|" +
                kommentar;
    }


    public static int readDatabaseFile(String filename, SortedMap<String, Buchungszeile> map) throws Exception {

        InputStreamReader reader = new InputStreamReader(new FileInputStream(filename),"windows-1252");
        BufferedReader    br     = new BufferedReader(reader);

        String zeile;
        int satzNrErwartet = 0;

        while ((zeile = br.readLine()) != null) {
            Buchungszeile b = Buchungszeile.fromDatabaseZeile(zeile);
            map.put(b.getUniquenessKey(), b); // durch den Database-Datensatz wird eine bereits bestehende Zeile überschieben
        }

        reader.close();

        return satzNrErwartet;
    }

    /**
     * @return liefert die Buchungszeile für die letzte eingelesene Datei-Zeile zurück (was chronologisch die erste Buchung ist (bei ING-DIBA)
     */
    public static Buchungszeile readIngDibaFile(File file, SortedMap<String, Buchungszeile> map) throws Exception {

        InputStreamReader reader            = new InputStreamReader(new FileInputStream(file),"windows-1252");
        BufferedReader br                   = new BufferedReader(reader);
        Buchungszeile  buchungszeileLast    = null;
        String          zeile               = null;
        boolean         startFound          = false;

        while ((zeile = br.readLine()) != null) {
            if (zeile.startsWith("\"Buchung\"")) {
                startFound = true;
                // todo: weitere Spalten absichern bzgl. der Erwartung
                continue;
            }

            if (!startFound) {
                continue;
            }

            Buchungszeile b = Buchungszeile.fromIngDibaZeile(zeile);
            buchungszeileLast =b;
            System.out.println(zeile);
            if (!map.containsKey(b.getUniquenessKey())) {   // durch den Umsatz-Datensatz wird eine bereits bestehende Zeile NICHT überschieben
                //System.out.println("KEY: " + b.getUniquenessKey());
                map.put(b.getUniquenessKey(), b);
            }
        }
        br.close();
        reader.close();

        return buchungszeileLast;
    }


    public static void writeDatabaseFile(String filename, List<Buchungszeile> zeilen) throws Exception {

        Writer w = new OutputStreamWriter(new FileOutputStream(filename), "windows-1252");
        BufferedWriter out = new BufferedWriter(w);

//        PrintWriter printWriter = IOTools.openOutputFile(filename);

        for (Buchungszeile buchungszeile : zeilen) {
            out.write(buchungszeile.toDatabaseZeile());
            out.write("\n");
        }
        out.close();
        //IOTools.closeOutputFile(printWriter);
    }

    public static void writePExportFile(String filename, List<Buchungszeile> zeilen) throws Exception {

        String lineFeed = new String("\n"); // Unix LineFeed

        Writer w = new OutputStreamWriter(new FileOutputStream(filename), "windows-1252");
        BufferedWriter out = new BufferedWriter(w);

        for (Buchungszeile buchungszeile : zeilen) {
            if (buchungszeile.pbetrag != 0) {
                out.write(buchungszeile.toPexportZeile());
                out.write(lineFeed);
            }
        }
        out.close();

/*        PrintWriter printWriter = IOTools.openOutputFile(filename);

        for (Buchungszeile buchungszeile : zeilen) {
            if (buchungszeile.pbetrag != 0) {
                printWriter.println(buchungszeile.toPexportZeile());
            }
        }

        IOTools.closeOutputFile(printWriter);
*/
    }

    public static String formatLongForEuroOutput(int l) {
        int maxL = 10;
        String s = "                    " + l;
        s = s.substring(s.length()-(maxL-2));
        String ganze       = s.substring(0, s.length() - 2);
        String fraktionale = s.substring(s.length() - 2);
        fraktionale = fraktionale.replace(" ", "0");
        if (ganze.endsWith(" ")) {
            ganze = ganze.substring(0,ganze.length()-1) + "0";
        }
        return ganze + "," + fraktionale;
    }

    public static String[] getColumns(String zeile) {
        if (!zeile.startsWith("\"") && !zeile.endsWith("\"")) {
            return new String[0];
        }
        zeile = zeile.substring(1);                     // wegen dem ersten "
        zeile = zeile.substring(0, zeile.length() - 1); // wegen dem letzten "
        if (zeile.endsWith("\";\"")) {
            zeile += "$null$";
        }
        String[] split = zeile.split("\";\"");
        if ("$null$".equals(split[split.length-1])) {
            split[split.length-1] = "";
        }
        // 11.10.2015: Trimmern IMMER
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();

        }
        return split;
    }

    public static void main(String[] args) {
        System.out.println(formatLongForEuroOutput(1));
        System.out.println(formatLongForEuroOutput(11));
        System.out.println(formatLongForEuroOutput(111));
    }
}
