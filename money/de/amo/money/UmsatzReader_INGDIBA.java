package de.amo.money;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.SortedMap;

/**
 * Created by private on 09.02.2017.
 */
public class UmsatzReader_INGDIBA {


    MoneyTransient moneyTransient;

    public UmsatzReader_INGDIBA() {
    }

    public UmsatzReader_INGDIBA(MoneyTransient moneyTransient) {
        this.moneyTransient = moneyTransient;
    }

    public String ermittleKontonummer(File file) throws Exception {

        InputStreamReader reader            = new InputStreamReader(new FileInputStream(file),"windows-1252");
        BufferedReader br                   = new BufferedReader(reader);
        String          zeile               = null;
        String          kontoZeilenAnfang1  = "\"Konto\";\"";
        String          kontoZeilenAnfang2  = "Konto;";
        String          kontonummerTmp      = null;

        while ((zeile = br.readLine()) != null) {

            if (zeile.startsWith(kontoZeilenAnfang1)) {
                kontonummerTmp = zeile.substring(kontoZeilenAnfang1.length());
                kontonummerTmp = kontonummerTmp.replace("\"","");

                if ("Girokonto: 5407433753".equals(kontonummerTmp)) {   // IngDIBA hat nach Einführung der SEPA-Nummern die Darstellung verändert
                    kontonummerTmp = "DE77500105175407433753";
                }

                break;
            }
            if (zeile.startsWith(kontoZeilenAnfang2)) {
                kontonummerTmp = zeile.substring(kontoZeilenAnfang2.length());

                if ("Girokonto: 5407433753".equals(kontonummerTmp)) {   // IngDIBA hat nach Einführung der SEPA-Nummern die Darstellung verändert
                    kontonummerTmp = "DE77500105175407433753";
                }

                break;
            }
        }
        br.close();
        reader.close();
        return  kontonummerTmp;
    }


    public Buchungszeile readIngDibaFile(File file) throws Exception {

        SortedMap<String, Buchungszeile> map = moneyTransient.getBuchungszeilen();

        InputStreamReader reader            = new InputStreamReader(new FileInputStream(file),"windows-1252");
        BufferedReader br                   = new BufferedReader(reader);
        Buchungszeile  buchungszeileLast    = null;
        String          zeile               = null;
        boolean         startFound          = false;
        String          kontoZeilenAnfang1   = "\"Konto\";\"";
        String          kontoZeilenAnfang2   = "Konto;";

        while ((zeile = br.readLine()) != null) {

            if (zeile.startsWith(kontoZeilenAnfang1) || zeile.startsWith(kontoZeilenAnfang2)) {

                String kontonummerTmp;

                if (zeile.startsWith(kontoZeilenAnfang1)) {
                    kontonummerTmp = zeile.substring(kontoZeilenAnfang1.length());
                    kontonummerTmp = kontonummerTmp.replace("\"", "");
                } else {
                    kontonummerTmp = zeile.substring(kontoZeilenAnfang2.length());
                }

                if ("Girokonto: 5407433753".equals(kontonummerTmp)) {   // IngDIBA hat nach Einführung der SEPA-Nummern die Darstellung verändert
                    kontonummerTmp = "DE77500105175407433753";
                }

                if (moneyTransient.getKontonnr() == null || moneyTransient.getKontonnr().equals("")) {
                    moneyTransient.setKontonnr(kontonummerTmp);
                } else {
                    if (!moneyTransient.getKontonnr().equals(kontonummerTmp)) {
                        throw new RuntimeException("Einlesen von Konto " + kontonummerTmp + " auf " + moneyTransient.getKontonnr()+" darf nicht sein!!");
                    }
                }
            }
            if (zeile.startsWith("\"Buchung\"") || zeile.startsWith("Buchung")) {
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



}