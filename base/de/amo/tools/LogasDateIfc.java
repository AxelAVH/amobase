package de.amo.tools;



import java.util.*;



public interface LogasDateIfc {
    
    public final long FOREVER_MILLIS = Long.MAX_VALUE;
    public final long FORALONGTIME_MILLIS = Long.MIN_VALUE;
    public final long UNDEFINED_MILLIS = 0;
    
    public final String FOREVER = "für immer";
    public final String FORALONGTIME = "seit jeher";  // Es war einmal ???
    public final String SINCE_EVER = FORALONGTIME;
    public final String TODAY = "heute";
    public final String NOW = "now";
    public final String YESTERDAY = "gestern";
    public final String TOMORROW = "morgen";
    public final String UNDEFINED = "";
    
    /**
     * Liefert einen String im Format TT.MM.JJJJ.
     */
    public String toString();
    
    /**
     * @param datumsFormat der Datumsausgabe
     * <br> erlaubte Eingaben sind:
     * <br> tt.mm.jjjj bzw. TT.MM.JJJJ
     * <br> tt.mm.jj   bzw. TT.MM.JJ
     * <br> ttmmjjjj   bzw. TTMMJJJJ
     * <br> ttmmjj     bzw. TTMMJJ
     * <br> jjjjmmtt   bzw. JJJJMMTT
     * <br> jjjj-mm-tt   bzw. JJJJ-MM-TT
     * <br> jjmmtt     bzw. JJMMTT
     * <br> din        bzw. DIN (gem. DIN EN 28601 => jjjj-mm-tt)
     * @return String formatierter Datums-String bzw. "" bei Fehlern
     */
    public String toFormattedString(String datumsFormat);

    /**
     * Liefert true, wenn es sich um ein spezielles Datum handelt :<BR>
     * <BL>undefiniert (undefined)</BL>
     * <BL>Zukunft (forever)</BL>
     * <BL>Vergangenheit (for a long time)</BL>
     */
    public boolean isSpecialDate();
    
    /**
     * Liefert true, wenn das Datum nicht definiert ist (den Wert null hat).
     */
    public boolean isUndefined();
    /**
     * Liefert true, wenn das Datum definiert ist (einen Wert ungleich null hat).
     */
    public boolean isDefined();
    
    /**
     * Liefert true, wenn das Datum die Vergangenheit ausdrückt (seit jeher).
     */
    public boolean isForALongTime();
    
    /**
     * Liefert true, wenn das Datum die Zukunft ausdrückt (für immer).
     */
    public boolean isForever();
    
    /**
     * Liefert einen String im Format TT.MM.JJJJ.
     * ACHTUNG: eigentlich überflüssig, da die toString()-Methode das selbe Ergbenis liefert
     */
    public String toString8();

    /**
     * Erwartet ein Objekt vom Typ 'Date' und fügt es in LogasDate ein
     */
    public void setDateDate(Date datumDate);
    
    /**
     * Erwartet einen String im Format TTMMJJ oder einen Leerstring, der ein leeres Datum setzt,
     * oder das Keywort 'heute' für das Tagesdatum.
     */
    public void setDate(String datumsString);
        
    public Calendar getDate();
    
    public Date getDateDate();

    /**
     * Setzt das Datum auf den monatsersten Tage
     * Bsp.: gregDatum = 08.06.1998  wird auf 1.06.1998 gesetzt
     * @return gibt sich anschließend selbst zurück
     */
    public LogasDateIfc setMonatsAnfangDate();

    /**
     * Setzt das Datum auf den monatsletzten Tages
     * Bsp.: gregDatum = 08.06.1998  wird auf 30.06.1998 gesetzt
     * @return gibt sich anschließend selbst zurück
     */
    public LogasDateIfc setMonatsEndDate();
    
    public void setDate6EDV(String datumsString);
    
    public void setDate8EDV(String datumsString);
    
    /**
     * Gibt das Datum als String zurück in der Form JJJJMMTT
     * BWV 25.07.01
     */
    public String getDateAsString8EDV();
    
    /**
     * Gibt das Datum als String zurück in der Form JJMMTT
     * AS 10.12.01
     */
    public String getDateAsString6EDV();
    
    /**
     * MSt 01.07.98:
     * Berechnet die Tages-Differenz zwischen dem Object und dem vergleichsDatum;
     * liegt das Objekt-Datum vor diesem, ist der Wert positiv. Liegt das Datum nach dem
     * vergleichsDatum, wird die Differenz als negativer Wert übermittelt.
     */
    public int getTageBis(LogasDateIfc vergleichsDatum);
    
    /**
     * Ge 21.09.98:
     * Berechnet die Tages-Differenz zwischen dem Object und dem  heutigen Datum;
     * liegt das Objekt-Datum vor heute, ist der Wert positiv. Liegt das Datum in der
     * Zukunft, wird die Differenz als negativer Wert übermittelt.
     */
    public int getTageBisHeute();
    
    /**
     * MSt 18.08.98:
     * Verändert das Datum um die gegebene Anzahl von Tagen. Es sind positive und negative Werte zulässig
     * @return  gibt sich anschließend selbst zurück
     */
    public LogasDateIfc addTage(int deltaTage);
            
    public LogasDateIfc kopie();

    public boolean isGleicherTag(LogasDateIfc checkDate);
        
    public boolean isEqual(LogasDateIfc vergleichsDatum);
    
    public int compareTo(LogasDateIfc vergleichsDatum);
    
    public boolean isEarlierThan(LogasDateIfc vergleichsDatum);
    
    public void setDateInMillisGMT(long wert);
    
    public long getDateInMillisGMT() ;
    
    public String getDatum();
    
    public void setDateAndTime(String datumsString, String zeitString);
    
    public boolean isLaterThan(LogasDateIfc vergleichsDatum) ;
    
    /**
     * Liegt das übergebene Datum im gleichen Monat wie das aktuelle Datum?<br>
     * Liefert false, wenn eines oder beiden LogasDates undefined ist
     * oder das übergebene Datum gar null<br>
     * Der Rest wie zu erwarten ....
     */
    public boolean istImGleichenMonat(LogasDateIfc vergleichsDatum);
    
    public String getMonatAlsString2();
    public String getTagAlsString2();
    public String getJahrAlsString4();
    public int getJahrAlsInt();
    public int getTagAlsInt();

    public void setGenauigkeitAlsTag();



    /**
     * Methode liefert eine interne Repräsentation des LogasDate als String (momentan JJJJMMTT bzw. null für undefiniert). Diese Methode ist
     * bewußt nicht statisch, da ein nicht vorhandenes Datum (null) vom Aufrufer interpretiert werden soll.
     * @return Datum in interner (zu persistierender) String-Darstellung.
     */

    public String asString();
}