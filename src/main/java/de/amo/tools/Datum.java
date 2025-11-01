package de.amo.tools;



import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class Datum {

    /**
     * Methode ermittelt ein Datums-Objekt zum übergebenen Datum in interner Repräsentation.
     * @param datum interne Repräsentation des zu konvertierenden Datums
     * @return Datums-Objekt zum Datum in interner Repräsentation
     */

    public static Date asDate(String datum) {

        return (isUndefiniert(datum) ? DateToolkit.UNDEFINED_DATE : new SimpleDateFormat("yyyyMMdd").parse(datum,new ParsePosition(0)));
    }



    /**
     * Methode liefert zu den übergebenen Datums-Objekt eine interne Repräsentation. Die Parameter null sowie DateToolkit.UNDEFINED_DATE werden
     * dabei als identisch angesehen. Erhalten bleibt ausschliesslich die reine Datumsangabe, Zeitinformationen werden abgeschnitten.
     * @param datum zu konvertierendes Datums-Objekt
     * @return interne Repräsentation des Datums-Objektes
     */

    public static String asString(Date datum) {

        return (DateToolkit.isUndefined(datum) ? undefiniert() : new SimpleDateFormat("yyyyMMdd").format(datum));
    }



    /**
     * Methode nimmt das übergebene Datum und zieht die angegebene Anzahl von Tagen ab. Das Datum darf niemals undefiniert sein.
     * @param datum interne Repräsentation des Ausgangsdatums
     * @param tage Anzahl der Tage, die vom Ausgangsdatum abgezogen werden sollen
     * @return interne Repräsentation des um die gewünschten Tage verschobenen Ausgangsdatums
     */

    public static String backward(String datum,int tage) {

        return forward(datum,-tage);                                      // rückwärts in der Zeit entspricht vorwärts mit der entsprechend
    }                                                                     // negativen Anzahl von Tagen



    /**
     * Methode vergleicht die beiden übergebenen Daten. Resultat ist ein Wert < 0 sofern 'tag' zeitlich vor 'nacht' liegt und ein Wert > 0 sofern
     * 'tag' zeitlich hinter 'nacht' liegt. Repräsentieren beide Daten den gleichen Tag, so wird 0 zurückgegeben. Die Angabe eines undefinierten
     * Datums als Parameter ist nicht erlaubt.
     * @param tag erstes zu vergleichendes Datum in interner Repräsentation
     * @param nacht zweites zu vergleichendes Datum in interner Repräsentation
     * @return Zahl < 0 wenn tag zeitlich vor nacht, Zahl > 0 wenn tag zeitlich nach nacht und 0 wenn tag.equals(nacht) == true
     */

    public static int compare(String tag,String nacht) {

        return tag.compareTo(nacht);                                      // Vergleich der beiden Daten, der null-Fall braucht nicht berücksichtigt
    }                                                                     // zu werden (da nicht erlaubt)



    /**
     * Methode prüft die beiden Daten auf Gleichheit (gleicher Tag). Die beiden Parameter dürfen niemals undefiniert sein.
     * @param tag erstes zu vergleichendes Datum in interner Repräsentation
     * @param nacht zweites zu vergleichendes Datum in interner Repräsentation
     * @return true genau dann, wenn die beiden Parameter den gleichen Tag beschreiben, andernfalls false
     */

    public static boolean equals(String tag,String nacht) {

        return tag.equals(nacht);                                         // Vergleich der beiden Daten, der null-Fall braucht nicht berücksichtigt
    }                                                                     // zu werden (da nicht erlaubt)



    /**
     * Methode formatiert das übergebene Datum gemäß dem angegebenen Format. Es dürfen dabei lediglich die Parameter 'y', 'M' und 'd' als zu
     * ersetzende Zeichen in der Maske verwendet werden.
     * @param datum interne Repräsentation des zu formatierenden Datums
     * @param format Format für das zu formatierende Datum gemäß der Klasse SimpleDateFormat
     * @return Gemäß dem Format formatierte Form des übergebenen Datums bzw. "" für ein undefiniertes Datum
     */

    public static String format(String datum,String format) {

        return (isUndefiniert(datum) ? "" : new SimpleDateFormat(format).format(asDate(datum)));
                                                                          // interne Repräsentation gemäß dem übergebeben Format aufbereiten und
    }                                                                     // zurückliefern



    /**
     * Methode nimmt das übergebene Datum und zählt die angegebene Anzahl von Tagen hinzu. Das Datum darf niemals undefiniert sein.
     * @param datum interne Repräsentation des Ausgangsdatums
     * @param tage Anzahl der Tage, die auf das Ausgangsdatum addiert werden sollen
     * @return interne Repräsentation des um die gewünschten Tage verschobenen Ausgangsdatums
     */

    public static String forward(String datum,int tage) {

        Calendar calendar = GregorianCalendar.getInstance();

        calendar.setTime(asDate(datum));                                  // interne Repräsentation des Datums in das Kalendar-Objekt überführen
        calendar.add    (Calendar.DAY_OF_MONTH,tage);                     // gewünschte Tage mit Übertrag hinzuzählen

        return asString(calendar.getTime());                              // Zieldatum in interne Repräsentation zurücktransformieren und als
    }                                                                     // Resultat liefern



    /**
     * Methode liefert das Tagesdatum des vergangenen Tages in interner Repräsentation.
     * @return Tagesdatum des Vortags in interner Repräsentation.
     */

    public static String gestern() {

        Calendar calendar  = new GregorianCalendar();

        calendar.setTime(new Date());                                     // Kalender mit Tagesdatum initialisieren
        calendar.add    (Calendar.DAY_OF_YEAR, -1);                       // vorherigen Tag zum Tagesdatum bestimmen

        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    }                                                                     // gestriges Datum in interner Repräsentation liefern



    /**
     * Methode liefert das Tagesdatum des morgigen Tages in interner Repräsentation.
     * @return Tagesdatum des folgenden Tages in interner Repräsentation.
     */

    public static String morgen() {

        Calendar calendar  = new GregorianCalendar();

        calendar.setTime(new Date());                                     // Kalender mit Tagesdatum initialisieren
        calendar.add    (Calendar.DAY_OF_YEAR, +1);                       // morgigen Tag zum Tagesdatum bestimmen

        return new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    }                                                                     // morgiges Datum in interner Repräsentation liefern



    /**
     * Methode liefert das aktuelle Tagesdatum in interner Repräsentation.
     * @return Tagesdatum in interner Repräsentation
     */

    public static String heute() {

        return new SimpleDateFormat("yyyyMMdd").format(new Date());       // interne Repräsentation für das aktuelle Tagesdatum erzeugen und als
    }                                                                     // Resultat liefern



    /**
     * Methode liefert genau dann true, wenn es sich bei dem angegebenen Datum in interner Repräsentation um ein definiertes Datum handelt.
     * @param datum interne Repräsentation des zu prüfenden Datums
     * @return true genau dann wenn ein definiertes Datum übergeben wurde, sonst false
     */

    public static boolean isDefiniert(String datum) {

        return datum != undefiniert();
    }



    /**
     * Methode liefert genau dann true, wenn es sich bei dem angegebenen Datum in interner Repräsentation um ein undefiniertes Datum handelt.
     * @param datum interne Repräsentation des zu prüfenden Datums
     * @return true genau dann wenn ein undefiniertes Datum übergeben wurde, sonst false
     */

    public static boolean isUndefiniert(String datum) {

        return datum == undefiniert();
    }



    /**
     * Methode liefert das Datum als Resultat das zeitlich am weitesten fortgeschritten ist, aber gleichzeitig einem der beiden Parameter
     * entspricht. Beide Parameter dürfen niemals undefiniert sein.
     * @param tag interne Repräsentation des ersten zu vergleichenden Parameters
     * @param nacht interne Repräsentation des zweiten zu vergleichenden Paramters
     * @return interne Repräsentation des zeitlich fortgeschritteneren Datums der beiden Parameter
     */

    public static String maximum(String tag,String nacht) {

        return (tag.compareTo(nacht) > 0 ? tag : nacht);
    }



    /**
     * Methode liefert das Datum als Resultat das zeitlich am weitesten zurückliegt, aber gleichzeitig einem der beiden Parameter entspricht. Beide
     * Parameter dürfen niemals undefiniert sein.
     * @param tag interne Repräsentation des ersten zu vergleichenden Parameters
     * @param nacht interne Repräsentation des zweiten zu vergleichenden Paramters
     * @return interne Repräsentation des zeitlich am weitesten zurückliegenden Datums der beiden Parameter
     */

    public static String minimum(String tag,String nacht) {

        return (tag.compareTo(nacht) < 0 ? tag : nacht);
    }



    /**
     * Methode liefert die interne Repräsentation für ein nichtdefiniertes Datum.
     * @return interne Repräsentation eines undefinierten Datums
     */

    public static String undefiniert() {

        return null;                                                      // ein undefiniertes Datum entspricht null in interner Repräsentation
    }


    public static void main(String args[]) {


        String start = IOTools.input(     "Startdatum: (JJJJMMTT) : ");

        int anzahlTage = IOTools.inputInt("Anzahl Tage            : ",-10000,10000);

        IOTools.println(                  "Neues Datum            : " + Datum.forward(start, anzahlTage));

    }
}