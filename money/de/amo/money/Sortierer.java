package de.amo.money;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: amo
 * Date: 23.10.14
 * Time: 14:49
 * To change this template use File | Settings | File Templates.
 */
public class Sortierer {

    public List<Buchungszeile> sortiere(SortedMap<String, Buchungszeile> buchungszeilen) {

        SortedMap<String, List<Buchungszeile>> dayMap = new TreeMap<String, List<Buchungszeile>>();

        // Grupieren in Liste der Buchungen eines Tages:
        for (Buchungszeile buchungszeile : buchungszeilen.values()) {
            List<Buchungszeile> tagesbuchungen = dayMap.get(buchungszeile.datum);
            if (tagesbuchungen == null) {
                tagesbuchungen = new ArrayList<Buchungszeile>();
                dayMap.put(buchungszeile.datum, tagesbuchungen);
            }
            tagesbuchungen.add(buchungszeile);
        }

        List<Buchungszeile> ret = new ArrayList<Buchungszeile>();


        for (List<Buchungszeile> buchungszeiles : dayMap.values()) {
            while (buchungszeiles.size() > 0) {
                Buchungszeile lastBuchung = null;
                if (ret.size() > 0) {
                    lastBuchung = ret.get(ret.size() - 1);
                }
                ret.add(extrahiereNachfolger(lastBuchung, buchungszeiles));
            }
        }

        return ret;
    }


    private Buchungszeile extrahiereNachfolger(Buchungszeile lastBuchung, List<Buchungszeile> potentielleNachfolger) {
        int lastSaldo = 0;
        if (lastBuchung != null) {
            lastSaldo = lastBuchung.saldo;
        }

        // zuerst eine eventuell komplementäre Umbuchung rausfischen:
        if (lastBuchung != null) {
            for (Buchungszeile buchungszeile : potentielleNachfolger) {
                if (lastBuchung.hauptbuchungsNr > 0 &&      // Umbuchungen sollte es erst geben, wenn der Buchungsatz einmal gespeichert wurde.
//                    lastBuchung.umbuchungNr > 0 &&          // nur dann liegt eine Umbuchung vor
                        lastBuchung.hauptbuchungsNr == buchungszeile.hauptbuchungsNr && (lastBuchung.umbuchungNr) == buchungszeile.umbuchungNr) {
                    potentielleNachfolger.remove(buchungszeile);
                    return buchungszeile;
                }
            }
        }

        // dann eine evtl. Umbuchung rausfischen:
        if (lastBuchung != null) {
            for (Buchungszeile buchungszeile : potentielleNachfolger) {
                if (lastBuchung.hauptbuchungsNr > 0 &&      // Umbuchungen sollte es erst geben, wenn der Buchungsatz einmal gespeichert wurde.
//                    lastBuchung.umbuchungNr > 0 &&          // nur dann liegt eine Umbuchung vor
                        lastBuchung.hauptbuchungsNr == buchungszeile.hauptbuchungsNr && (lastBuchung.umbuchungNr+1) == buchungszeile.umbuchungNr) {
                    potentielleNachfolger.remove(buchungszeile);
                    return buchungszeile;
                }
            }
        }


        for (Buchungszeile buchungszeile : potentielleNachfolger) {
            if (buchungszeile.isAllerersterSatz) {
                lastSaldo = buchungszeile.saldo - buchungszeile.betrag;
                buchungszeile.isAllerersterSatz = false;
            }
        }

        for (Buchungszeile buchungszeile : potentielleNachfolger) {
            if (lastSaldo + buchungszeile.betrag == buchungszeile.saldo) {
                potentielleNachfolger.remove(buchungszeile);
                return buchungszeile;
            }
        }
        String msg = "Keinen Start-Satz gefunden.";
        if (lastBuchung != null) {
            msg = "Kein Nachfolger gefunden zu Zeile:\n" + lastBuchung.toShow();
        }
        msg += "\nPotentielle Nachfolger:";
        for (Buchungszeile buchungszeile : potentielleNachfolger) {
            msg += "\n" + buchungszeile.toShow();
        }
        throw new RuntimeException(msg);
    }
}
