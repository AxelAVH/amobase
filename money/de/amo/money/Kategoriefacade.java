package de.amo.money;

import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Created by private on 12.01.2016.
 */
public class Kategoriefacade {

    private static Kategoriefacade instance;

    private Kategoriefacade() {
    }

    public static Kategoriefacade get() {
        if (instance ==null) {
            instance= new Kategoriefacade();
            instance.init();
        }
        return instance;
    }

    private List<Kategorie> kategorien;

    public List <Kategorie> getKategorien() {
        return kategorien;
    }

    public void init() {
        kategorien = new ArrayList<>();

//        if (true) {

            try {
                loadKategorien();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
            return;
//        }

//        kategorien.add(new Kategorie("AK", "Auto - Kraftstoff"));
//        kategorien.add(new Kategorie("AR", "Auto - alles ausser Kraftstoff/Steuer/Versicherung"));
//        kategorien.add(new Kategorie("AV", "Auto - Versicherung"));
//        kategorien.add(new Kategorie("AS", "Auto - Steuern"));
//        kategorien.add(new Kategorie("BE", "Bar - Entnahme"));
//        kategorien.add(new Kategorie("ES", "Einkommen - Steuer" ));
//        kategorien.add(new Kategorie("EK", "Einkommen - alles rumd um dbh, auch Reisekosten und -Rückerstattungen" ));
//        kategorien.add(new Kategorie("FC", "Freizeit - Chor"));
//        kategorien.add(new Kategorie("GK", "Gesundheit - Kosten"));
//        kategorien.add(new Kategorie("GR", "Gesundheit - Rückerstattungen"));
//        kategorien.add(new Kategorie("FU", "Freizeit - Urlaub/Kultur"));
//        kategorien.add(new Kategorie("LT", "Leben - täglicher Bedarf" ));
//        kategorien.add(new Kategorie("LG", "Leben - Geschenke u. sonstige Anschaffungen" ));
//        kategorien.add(new Kategorie("LK", "Leben - Kleidung" ));
//        kategorien.add(new Kategorie("WM", "Wohnen - Miete"));
//        kategorien.add(new Kategorie("WW", "Wohnen - Warm"));
//        kategorien.add(new Kategorie("WT", "Wohnen - Telekommunikation"));
//        kategorien.add(new Kategorie("VB", "Versicherung - Berufsunfähigkeit"));
//        kategorien.add(new Kategorie("VR", "Versicherung - Rente"));
//        kategorien.add(new Kategorie("UA", "Unterhalt - Aktivitäten/Geschenke Kinder"));
//        kategorien.add(new Kategorie("UF", "Unterhalt - Frau"));
//        kategorien.add(new Kategorie("UK", "Unterhalt - Kinder"));
//        kategorien.add(new Kategorie("US", "Unterhalt - Sonstiges"));
//        kategorien.add(new Kategorie("ZK", "Zuschüsse - Kleinsorge"));
//        kategorien.add(new Kategorie("ZM", "Zuschüsse - Möller W."));
    }

    public String getKategoriebezeichnung(String code) {
        for (Kategorie kategorie : kategorien) {
            if (kategorie.getCode().equals(code)) {
                return kategorie.getBeschreibung();
            }
        }

        return null;
    }

//    public List<String> getKategorieCodes() {
//        List<String> ret =new ArrayList<>();
//        ret.addAll(kategorien.keySet());
//        return ret;
//    }

    public List<String> getComboboxList() {
        SortedSet<String> set = new TreeSet<String>();

        for (Kategorie kategorie : kategorien) {
            set.add(kategorie.getCode() + " - " + kategorie.getBeschreibung());
        }

        ArrayList ret = new ArrayList();
        ret.addAll(set);
        return ret;
    }

    public String getKategorieFromComboboxString(String selectedItem) {
        if (selectedItem == null || "".equals(selectedItem)) {
            return null;
        }
        return selectedItem.substring(0, selectedItem.indexOf(" - "));
    }

    public void loadKategorien() throws BackingStoreException {

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        try {
            prefs.childrenNames();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        kategorien = new ArrayList<>();

        Preferences kategorieNode = prefs.node("Kategorien");

        String[] keys = kategorieNode.keys();

        SortedMap<String, Kategorie> map = new TreeMap<>();

        for (int i = 0; i < keys.length; i++) {
            Kategorie kategorie = new Kategorie(keys[i], kategorieNode.get(keys[i], "unbekannt"));
            map.put(kategorie.getCode(),kategorie);
        }

        kategorien.addAll(map.values());
    }

    public void saveKategorien() throws BackingStoreException {

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        // ToDo: was muss man wirklich machen, um den Knoten zu töten?
        prefs.clear();
        prefs.remove("Kategorien");
        Preferences katPrefs = prefs.node("Kategorien");
        katPrefs.removeNode();
        prefs.flush();

        katPrefs = prefs.node("Kategorien");

        for (Kategorie kategorie : kategorien) {
            if (kategorie.getCode() == null || "".equals(kategorie.getCode().trim())) {
                continue;
            }
            katPrefs.put(kategorie.getCode(), kategorie.getBeschreibung());
        }
        prefs.flush();
    }

}
