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

    private Map<String,String> kategorien;

    public Map<String, String> getKategorien() {
        return kategorien;
    }

    public void init() {
        kategorien = new HashMap<String,String>();

        if (true) {

            try {
                loadKategorien();
            } catch (BackingStoreException e) {
                e.printStackTrace();
            }
            return;
        }

        kategorien.put("AK", "Auto - Kraftstoff");
        kategorien.put("AR", "Auto - alles ausser Kraftstoff/Steuer/Versicherung");
        kategorien.put("AV", "Auto - Versicherung");
        kategorien.put("AS", "Auto - Steuern");
        kategorien.put("BE", "Bar - Entnahme");
        kategorien.put("ES", "Einkommen - Steuer" );
        kategorien.put("EK", "Einkommen - alles rumd um dbh, auch Reisekosten und -Rückerstattungen" );
        kategorien.put("FC", "Freizeit - Chor");
        kategorien.put("GK", "Gesundheit - Kosten");
        kategorien.put("GR", "Gesundheit - Rückerstattungen");
        kategorien.put("FU", "Freizeit - Urlaub/Kultur");
        kategorien.put("LT", "Leben - täglicher Bedarf" );
        kategorien.put("LG", "Leben - Geschenke u. sonstige Anschaffungen" );
        kategorien.put("LK", "Leben - Kleidung" );
        kategorien.put("WM", "Wohnen - Miete");
        kategorien.put("WW", "Wohnen - Warm");
        kategorien.put("WT", "Wohnen - Telekommunikation");
        kategorien.put("VB", "Versicherung - Berufsunfähigkeit");
        kategorien.put("VR", "Versicherung - Rente");
        kategorien.put("UA", "Unterhalt - Aktivitäten/Geschenke Kinder");
        kategorien.put("UF", "Unterhalt - Frau");
        kategorien.put("UK", "Unterhalt - Kinder");
        kategorien.put("US", "Unterhalt - Sonstiges");
        kategorien.put("ZK", "Zuschüsse - Kleinsorge");
        kategorien.put("ZM", "Zuschüsse - Möller W.");
    }

    public String getKategoriebezeichnung(String kategorie) {
        return kategorien.get(kategorie);
    }

    public List<String> getKategorieCodes() {
        List<String> ret =new ArrayList<>();
        ret.addAll(kategorien.keySet());
        return ret;
    }

    public List<String> getComboboxList() {
        SortedSet<String> set = new TreeSet<String>();

        for (Map.Entry<String, String> entry : kategorien.entrySet()) {
            set.add(entry.getKey() + " - " + entry.getValue());
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

        kategorien = new HashMap<>();

        Preferences kategorieNode = prefs.node("Kategorien");

        String[] keys = kategorieNode.keys();

        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            String value = kategorieNode.get(key, "unbekannt");
            kategorien.put(key, value);
        }


    }

    public void saveKategorien() throws BackingStoreException {

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        prefs.remove("Kategorien");
        Preferences katPrefs = prefs.node("Kategorien");

        for (Map.Entry<String, String> entrySet: kategorien.entrySet()) {
            katPrefs.put(entrySet.getKey(), entrySet.getValue());
        }

        prefs.flush();

    }

}
