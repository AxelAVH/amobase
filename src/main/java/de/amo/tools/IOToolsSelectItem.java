package de.amo.tools;
/**
 * Hilfsklasse für Selekt-Programmierungen in MR-Tools, siehe weiter in IOToolsSelectMenue<br>
 * Beschreibt einen einzelnen zu selektierenden Eintrag durch Anzeigetext und bei Selektion
 * auszuführende Methode ("doIt()")<br>
 * Wird durch dieses Item ein dokumentierter Change am Produkt (z.B. Vera) durchgeführt,
 * ist die Change-ID (z.b. gem. der ACCESS-Datenbank VeraChanges.mdb) in der Methode
 * setAnzeigeText mit zu übergeben.
 * @author Axel Möller
 */
public abstract class IOToolsSelectItem {

    /*
     * Diverse Ausführungs-Status-Werte
     */
    public final static int STATUS_UNKNOWN = 0;
    public final static int STATUS_INITIAL = 1;
    public final static int STATUS_ERROR   = 2;
    public final static int STATUS_WARNING = 3;
    public final static int STATUS_OK      = 4;


    private String anzeigeText;
    private String changeId;
    private int menuNr;

    IOToolsSelectMenue menue;

    protected IOToolsSelectItem() {
    }

    protected IOToolsSelectItem(IOToolsSelectMenue menue) {
        this();
        this.menue = menue;
    }


    /**
     * Führt die mit dem Item verknüpfte Aktion aus. Liefert false, wenn danach das
     * aktuelle Menue verlassen werden soll, true, wenn das Menue wieder angezeigt werden
     * soll.<br>
     * Diese Methode kann überschrieben werden, um FÜR EINEN Mandanten eine Aktion auszuführen.
     * Falls etwas FÜR ALLE Mandanten ausgeführt werden soll, kann diese Methode unverändert
     * belassen werden und stattdessen die gleichnamige Methode mit MandantIfc als Parameter
     * verwendet werden.
     */
    public abstract boolean doIt() throws Exception;

    public void setAnzeigeText(String s) {
        anzeigeText = s;
    }
    
    public String getSimpleAnzeigeText() {
        String ret = anzeigeText;
        if (changeId != null) {
            ret = changeId + " : "+ret;
        }
        return ret;
    }
    
    public String getAnzeigeText() {
        String ret = getSimpleAnzeigeText();
        return ret;
    }

    public void setTitel(String titel) {
        menue.setTitle(titel);
    }
    public int getMenuNr() {
        return menuNr;
    }

    public void setMenuNr(int menuNr) {
        this.menuNr = menuNr;
    }

    private int getColumnCount(String str) {
        if (str == null) {
            return 0;
        }
        int cnt = 1;
        while (str.indexOf(";") >= 0) {
            cnt++;
            str=str.substring(str.indexOf(";")+1);
        }
        return cnt;
    }
    
    private String getColumn(int col, String str) {
        int colCnt = getColumnCount(str);
        if (str == null || col >= colCnt) {
            return null;
        }
        String ret = null;
        if (str.indexOf(";") < 0) {
            return str;
        }
        if (col == colCnt-1) {
            return str.substring(str.lastIndexOf(";")+1);
        }
        for (int i = 0; i <= col;i++) {
            ret = str.substring(0, str.indexOf(";"));
            if (str.indexOf(";") > 0) {
                str = str.substring(str.indexOf(";")+1);
            }
        }
        return ret;
    }
    
}