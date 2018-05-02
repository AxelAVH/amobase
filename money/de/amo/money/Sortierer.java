package de.amo.money;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: amo
 * Date: 23.10.14
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class Sortierer {

    public List<Buchungszeile> sortiere(SortedMap<String, Buchungszeile> buchungszeilen) {

        SortedMap<String, SortedMap<String, Buchungszeile>> dayMap = new TreeMap<String, SortedMap<String, Buchungszeile>>();

        // Grupieren in Liste der Buchungen eines Tages:
        int lfd = 0;
        for (Buchungszeile buchungszeile : buchungszeilen.values()) {
            lfd++;
            SortedMap<String, Buchungszeile> tagesbuchungen = dayMap.get(buchungszeile.datum);
            if (tagesbuchungen == null) {
                tagesbuchungen = new TreeMap<String, Buchungszeile>();
                dayMap.put(buchungszeile.datum, tagesbuchungen);
            }
            String key1 = "                    " + buchungszeile.hauptbuchungsNr;
            key1 = key1.substring( key1.length()-10 );
            String key2 = "                    " + buchungszeile.umbuchungNr;
            key2 = key2.substring( key2.length()-10 );
            String key3 = buchungszeile.umbuchungsPro == true ? "a" : "b";
            String tagesbuchungskey = key1 + "|" + key2 + "|" + key3 + "|" + lfd;
            tagesbuchungen.put(tagesbuchungskey, buchungszeile);
        }

        List<Buchungszeile> ret = new ArrayList<Buchungszeile>();


        for (SortedMap<String, Buchungszeile> tagesbuchungen : dayMap.values()) {
            while (tagesbuchungen.size() > 0) {
                Buchungszeile lastBuchung = null;
                if (ret.size() > 0) {
                    lastBuchung = ret.get(ret.size() - 1);
                    System.out.println(lastBuchung);
                }
                ret.add(extrahiereNachfolger(lastBuchung, tagesbuchungen));
            }
        }

        return ret;
    }


    private Buchungszeile extrahiereNachfolger(Buchungszeile lastBuchung, SortedMap<String, Buchungszeile> potentielleNachfolger) {
        int lastSaldo = 0;
        if (lastBuchung != null) {
            lastSaldo = lastBuchung.saldo;
        }

        // zuerst über die Buchungseinträge der Datenbank versuchen:
        for ( Map.Entry<String, Buchungszeile> entry : potentielleNachfolger.entrySet() ) {
            if (entry.getValue().hauptbuchungsNr != 0) {       // die sind der Datenbank bereits bekannt und durch die Sortiermerkmale gesichert
                    potentielleNachfolger.remove(entry.getKey());
                    return entry.getValue();
            }
//            if (buchungszeile.umbuchungNr == lastBuchung.umbuchungNr+1) {
//                if (buchungszeile.umbuchungNr == 0) {   // normale Bewegung
//                    potentielleNachfolger.remove(buchungszeile);
//                    return buchungszeile;
//                }
//            }
//            if (buchungszeile.umbuchungNr == lastBuchung.umbuchungNr) { // Umbuchung
//                if (lastBuchung.umbuchungNr == 0 && buchungszeile.umbuchungsPro) {
//                    potentielleNachfolger.remove(buchungszeile);
//                    return buchungszeile;
//                }
//                if (lastBuchung.umbuchungNr == 0 && buchungszeile.umbuchungsPro) {
//            }
        }



        // zuerst eine eventuell komplementäre Umbuchung rausfischen:
        if (lastBuchung != null) {
            for (Map.Entry<String, Buchungszeile> entry : potentielleNachfolger.entrySet()) {
                if (lastBuchung.hauptbuchungsNr > 0 &&      // Umbuchungen sollte es erst geben, wenn der Buchungsatz einmal gespeichert wurde.
//                    lastBuchung.umbuchungNr > 0 &&          // nur dann liegt eine Umbuchung vor
                        lastBuchung.hauptbuchungsNr == entry.getValue().hauptbuchungsNr && (lastBuchung.umbuchungNr) == entry.getValue().umbuchungNr) {
                    potentielleNachfolger.remove(entry.getKey());
                    return entry.getValue();
                }
            }
        }

        // dann eine evtl. Umbuchung rausfischen:
        if (lastBuchung != null) {
            for (Map.Entry<String, Buchungszeile> entry : potentielleNachfolger.entrySet()) {
                if (lastBuchung.hauptbuchungsNr > 0 &&      // Umbuchungen sollte es erst geben, wenn der Buchungsatz einmal gespeichert wurde.
//                    lastBuchung.umbuchungNr > 0 &&          // nur dann liegt eine Umbuchung vor
                        lastBuchung.hauptbuchungsNr == entry.getValue().hauptbuchungsNr && (lastBuchung.umbuchungNr+1) == entry.getValue().umbuchungNr) {
                    potentielleNachfolger.remove(entry.getKey());
                    return entry.getValue();
                }
            }
        }


        for (Map.Entry<String, Buchungszeile> entry : potentielleNachfolger.entrySet()) {
            if (entry.getValue().isAllerersterSatz) {
//                lastSaldo = buchungszeile.saldo - buchungszeile.betrag;
//                buchungszeile.isAllerersterSatz = false;
                potentielleNachfolger.remove(entry .getKey());
                return entry.getValue();
            }
        }

        for (Map.Entry<String, Buchungszeile> entry : potentielleNachfolger.entrySet()) {
            if (lastSaldo + entry.getValue().betrag == entry.getValue().saldo) {
                Buchungszeile ret = entry.getValue();           // !! beim remove verändert sich das Entry, liefert danach ein falsches value!!
                potentielleNachfolger.remove(entry .getKey());
                return ret;
            }
        }
        String msg = "Keinen Start-Satz gefunden.";
        if (lastBuchung != null) {
            msg = "Kein Nachfolger gefunden zu Zeile:\n" + lastBuchung.toShow();
        }
        msg += "\nPotentielle Nachfolger:";
        for (Buchungszeile buchungszeile : potentielleNachfolger.values()) {
            msg += "\n" + buchungszeile.toShow();
        }
        throw new RuntimeException(msg);
    }
}
