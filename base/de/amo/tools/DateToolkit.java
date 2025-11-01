package de.amo.tools;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateToolkit {

// *********************************   Constants   *********************************

    public final static long FOREVER_MILLIS      = LogasDateIfc.FOREVER_MILLIS;
    public final static long FORALONGTIME_MILLIS = LogasDateIfc.FORALONGTIME_MILLIS;
    public final static long UNDEFINED_MILLIS    = LogasDateIfc.UNDEFINED_MILLIS;

    public final static java.util.Date FOREVER_DATE      = new java.util.Date(FOREVER_MILLIS);
    public final static java.util.Date FORALONGTIME_DATE = new java.util.Date(FORALONGTIME_MILLIS);
    public final static java.util.Date UNDEFINED_DATE    = new java.util.Date(UNDEFINED_MILLIS);

    public final static int CENTURY_THRESHOLD = 50;


    private final static int[] MONTH_LENGTH = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public final static String TECHNICAL_FORMAT = "yyyy.MM.dd HH:mm:ss.SSS";
    public final static String EDV_8_FORMAT     = "yyyyMMdd";
    public final static String EDV_12_FORMAT    = "yyyyMMddHHmm";
    public final static String EDV_14_FORMAT    = "yyyyMMddHHmmss";
    public final static String EDV_17_FORMAT    = "yyyyMMddHHmmssSSS";

    public final static String DETAILED_READABLE_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public final static String SIMPLE_READABLE_FORMAT   = "dd.MM.yyyy";

// *********************************   Methods   *********************************

    /**
     *  Wandelt ein Date verlustfrei in einen String um
     */
    public static String toTechnicalRepresentation(java.util.Date aDate) {
        return new SimpleDateFormat(TECHNICAL_FORMAT).format(aDate);
    }

    /**
     *  Wandelt die technische Repräsentation eines Dates in ein Date um
     */
    public static java.util.Date fromTechnicalRepresentation(String aString){
        try{
            return new SimpleDateFormat(TECHNICAL_FORMAT).parse(aString);
        }
        catch(ParseException pe){
            throw new IllegalArgumentException("Exception parsing <" + aString + "> " + pe.getMessage());
        }
    }

    
    /**
     *  Wandelt ein LogasDate verlustfrei in einen String um
     */
    public static String toTechnicalLogasDateRepresentation(LogasDateIfc aDate) {
        if (aDate==null) {
            return null;
        }
        return aDate.getDatum() + "-" + aDate.getDateInMillisGMT();
    }

    /**
     *  Wandelt ein LogasDate in eine String-Repräsentation eines Datums um
     */
    public static String fromLogasDate(LogasDateIfc logasDate, String format) {
        if (logasDate==null) {
            return new SimpleDateFormat(format).format(UNDEFINED_DATE);
        }
        return new SimpleDateFormat(format).format(logasDate.getDateDate());
    }

   /**
    *  Generiert einen formatierten String aus einem Datum
    */
    public static String getFormattedDate(int day, int month, int year) {
        String newDate = "";
        newDate +=       ((day < 10)   ? "0" + String.valueOf(day)   : String.valueOf(day));
        newDate += "." + ((month < 10) ? "0" + String.valueOf(month) : String.valueOf(month));
        newDate += "." + String.valueOf(getLongYear(year));
        return newDate;
    }

   /**
    *  Generiert einen formatierten String aus einem Datum als TT.MM.JJJJ
    * für den Fall, dass das Date null ist, wird "" zugrückgegeben
    */
    public static String getFormattedDate(java.util.Date date) {
        if(date==null){
            return "";
        }
        return getFormattedDate(date.getTime());
    }

    /**
     *  Generiert einen formatierten String aus einem Datum als long (timeMillis)
     */
     public static String getFormattedDate(long millis) {
         if (shouldRenderEmptyString(millis)) {
             return " ";
         } else {
             GregorianCalendar tempCal = new GregorianCalendar(0, 0, 0, 0, 0, 0);
             tempCal.setTime(new Date(millis));
             int day = tempCal.get(Calendar.DAY_OF_MONTH);
             int month = tempCal.get(Calendar.MONTH) + 1;
             int year = tempCal.get(Calendar.YEAR);
             return getFormattedDate(day, month, year);
         }
     }

    /**
     * Gibt zurück, ob die übergebenen Millisekunden einem Spezialwert entsprechen und daher ein leerer String gerendert werden soll.
     * @param millis Die überprüfte Zeit in Millisekunden
     * @return <code>true</code> => Zeigt dem Aufrufer an, dass das Datum nicht sinnvoll gerendert werden kann.<br/><code>false</code> => Aufrufer entscheidet selber, wie das Datum gerendert werden soll
     */
    public static boolean shouldRenderEmptyString(long millis){
        return millis == FOREVER_MILLIS || millis == FORALONGTIME_MILLIS || millis == UNDEFINED_MILLIS;
    }

    /**
    * Generiert einen umgekehrt formatierten String aus einem Datum (in EDV-8-Darstellung), d.h. TT.MM.JJJJ wird in die Form JJJJMMTT
    * konvertiert (z.B. 21.04.2002 -> 20020421). Für die Aufrufparameter UNDEFINED_DATE, FORALONGTIME_MILLIS, FOREVER_MILLIS und null
    * wird "" zurückgegeben.
    */
    
    public static String getReverseFormattedDate(java.util.Date date) {
        String char10 = getFormattedDate(date);
        if (char10.length() != 10) {
            return "";
        }
        return char10.substring(6, 10) + char10.substring(3, 5) + char10.substring(0, 2);
    }


    /**
     * Generiert einen komprimiert formatierten String aus einem Datum, d.h. TT.MM.JJJJ wird in die Form TTMMJJJJ
     * konvertiert (z.B. 21.04.2002 -> 21042002). Für die Aufrufparameter UNDEFINED_DATE, FORALONGTIME_MILLIS, FOREVER_MILLIS und null
     * wird "" zurückgegeben.
     */

     public static String getCompressedFormattedDate(java.util.Date date) {
         String char10 = getFormattedDate(date);
         if (char10.length() != 10) {
             return "";
         }
         return char10.substring(0, 2) + char10.substring(3, 5) + char10.substring(6, 10);
     }


   /**
    *  Generiert einen nach ISO/DIN formatierten String aus einem Datum,
    *  also für 21.02.2002 -> 2002-02-21
    */
    public static String getIsoFormattedDate(java.util.Date date) {
        String char10 = getFormattedDate(date);
        if (char10.length() != 10) {
            return "";
        }
        return char10.substring(6,10) + '-' + char10.substring(3,5) + '-' + char10.substring(0,2);
    }    
    
   /**
    *  Generiert ein Date aus einem formatierten String
    */
    public static java.util.Date getDateFromString(String char10) {
        try {
            int day   = Integer.parseInt(char10.substring(0, 2));
            int month = Integer.parseInt(char10.substring(3, 5));
            int year  = Integer.parseInt(char10.substring(6, 10));
            return new GregorianCalendar(year, month-1, day, 0, 0, 0).getTime();
        }
        catch (Exception e) {
            return UNDEFINED_DATE;
        }
    }

    
    
    /** 
     * Generiert ein Date aus einem formatierten String der Form JJJJMMTT. Für alle anderen Parameter
     * wird UNDEFINED_DATE zurückgegeben.
     */
    
    public static java.util.Date getDateFromReverseFormattedString(String char8) {
        if (char8 == null || char8.length() != 8) {
            return DateToolkit.UNDEFINED_DATE;
        }
        
        return getDateFromString(char8.substring(6,8) + "." + char8.substring(4,6) + "." + char8.substring(0,4));
    }

    
    
   /**
    *  Liefert eine 4-stellige Jahreszahl zu einer angegebene Jahreszahl. Ist die
    *  übergebene Jahreszahl bereits 4-stellig, wird sie unverändert zurück gegeben.
    *  Andernfalls wird über den Jahrhundert-Schwellwert ermittelt, um welches
    *  Jahrhundert das Datum expandiert werden muss.
    */
    public static int getLongYear(int year) {
        if (year < 100) {
            year += 2000;
        }
        return year;
    }
    
   /**
    *  Liefert die Anzahl der Tage in einem bestimmten Monat eines Jahres. Dabei werden
    *  Schaltjahre entsprechend berücksichtigt.
    *  @param month Monatsangabe (1 <= month <= 12)
    *  @param year Jahresangabe (4-stellig)
    */
    public static int getDaysInMonth(int month, int year) {
        if (month == 2) {
            GregorianCalendar cal = new GregorianCalendar();
            return (cal.isLeapYear(year)) ? MONTH_LENGTH[month-1]+1 : MONTH_LENGTH[month-1];
        }
        else {
            return MONTH_LENGTH[month-1];
        }
    }

    /**
     * Liefert das heutige Datum OHNE Stunden und Minuten
     */
    public static Date today() {
        GregorianCalendar nowAsCal  = new GregorianCalendar();
        int year  = nowAsCal.get(Calendar.YEAR);
        int month = nowAsCal.get(Calendar.MONTH);
        int day   = nowAsCal.get(Calendar.DAY_OF_MONTH);
        GregorianCalendar resultCal = new GregorianCalendar(year, month, day,  0, 0, 0);
        return resultCal.getTime();

    }

    // ###################################################################################
    
   /**
    *  Erzeugt einen Formatierer, der in der Lage ist, Datumsangaben zu formatieren. Es ist
    *  eine spezielle Ausprägung eines 'java.text.SimpleDateFormat' mit folgenden Eigenschaften:
    *  <ul>
    *  <li> Die Kurzform des Parameters "E" (day in week) wird als Nummer des Wochentages ausgewertet.
    *       Montag bedeutet dabei bei 1.</li>
    *  </ul>
    *  @param  formatString Format-String mit den speziellen Formatierungssymbolen
    *  @return Der Formatierer mit dem angegebenen Format-String
    *  @see java.text.SimpleDateFormat
    */
    public static DateFormat createDateFormat(String formatString) {
        return new SimpleDateFormat(formatString, new LDateFormatSymbols());
    }
    
   /**
    *  Formatiert ein Datum mit den "üblichen" Formatierungszeichen
    *  @see java.text.SimpleDateFormat
    */
    public static String format(Date date, String formatString) {
        return format(date,formatString,true);
    }

   /**
    *  Formatiert ein Datum mit den "üblichen" Formatierungszeichen
    *  @param handleUndefinedDate  gibt an, ob ein undefiniertes Datum als Leerstring formatiert werden soll
    *                              wenn "false" wird das Datum in jedem Fall durch das Format formatiert.
    *  @see java.text.SimpleDateFormat
    */
    public static String format(Date date, String formatString, boolean handleUndefinedDate) {
        if (date==null) {
            return "";
        }
        if (handleUndefinedDate && isUndefined(date)) {
            return "";
        }
        return createDateFormat(formatString).format(date);
    }

    /**
     *  Hilfsklasse für die Methode "createDateFormat", welche die speziellen Wochentagsstrings (Zahlen)
     *  enthält.
     */
    private static class LDateFormatSymbols extends DateFormatSymbols {
       private final String[] weekdayNumbers = new String[] {"", "7", "1", "2", "3", "4", "5", "6"};
       public LDateFormatSymbols() {
            super();
            setShortWeekdays(weekdayNumbers);
        }
    }
    
    // ###################################################################################
    
    

    /**
     *  Lokale Klasse, die das Parsing einer komfortablen Eingabe in ein Kalender-Objekt leistet.
     *  Der zu untersuchende Text darf unvollständige Teile sowie Sonderzeichen (*, +, -) enthalten,
     *  die entsprechend interpretiert werden.
     */
    private static class SpecialDateParser {
        
        private static String invalidYearMessage  = "Jahresangaben vor 1900 sind nicht zulässig.";
        private static String invalidMonthMessage = "Die Monatsangabe ist ungültig.";
        private static String invalidDayMessage   = "Die Tagesangabe ist ungültig.";


        /**
         *  Wertet die Eingabe bei Vorliegen einer Sonderfunktion aus. Dies sind
         *  <ul>
         *  <li> *  für heute
         *  <li> +[Tage]  für morgen bzw. + X Tage
         *  <li> -[Tage]  für gestern bzw. - X Tage
         *  </ul>
         *  Die Eingabe wird in ein gültiges Datum übersetzt oder eine Ausnahme mit 
         *  dem Grund des Fehlers geworfen.
         */
        private Calendar parseSpecialInput(String text) throws IllegalArgumentException {
            // Kalender-Objekt mit heutigem Datum annlegen (ohne Minuten und Stunden)
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // Betrag der Tage nach dem Vorzeichen ermitteln
            int days = 1;
            try {
                // String aus den reinen Zahlzeichen (ohne '.' zusammensetzen)
                String daysStr = text.substring(1, 2) + text.substring(3, 5);
                days = Integer.parseInt(daysStr.trim());
            }
            catch(Throwable thr) {
                days = 1;
            }

            // Sonderzeichen und -fälle behandeln
            char c = text.charAt(0);
            switch(c) {
                // '*' = heute
                case '*' :
                    cal = cal;
                    break;

                // '+' = morgen bzw. + X Tage
                case '+' :
                    cal.add(Calendar.DATE, days);
                    break;

                // '-' = gestern bzw. - X Tage
                case '-' :
                    cal.add(Calendar.DATE, -days);
                    break;

                default:
                    throw new IllegalArgumentException("Ungültiges Anfangszeichen:" + c);
            }

            return cal;
        }

        /**
         *  Zerlegt einen String in die 3 Komponenten Tag, Monat, Jahr (als int-Array [0..2])
         *  Dabei werden fehlende Angaben mit dem heutigen Datum aufgefüllt (z.B. Monat + Jahr)
         */
        private int[] getCalendarComponentsFromString(String text) throws IllegalArgumentException {
            // Kalender-Objekt mit heutigem Datum annlegen (ohne Minuten und Stunden)
            GregorianCalendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            // automatisches Auffüllen fehlender Angaben (Monat, Jahr)
            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(text, ".", false);
            int day=-1, month=-1, year=-1;
            try {
                // aktuelle Werte aus dem String ermitteln
                day = Integer.parseInt(tokenizer.nextToken().trim());
                month = Integer.parseInt(tokenizer.nextToken().trim());
                year = Integer.parseInt(tokenizer.nextToken().trim());
            }
            catch(Throwable thr) {
                // fehlende Werte automatisch ergänzen
                if (day   == -1) day   = cal.get(Calendar.DAY_OF_MONTH);
                if (month == -1) month = cal.get(Calendar.MONTH)+1;
                if (year  == -1) year  = cal.get(Calendar.YEAR);
            };
            return new int[] { day, month, year };
        }

        /**
         *  Prüft, ob das Datum im Kalender überhaupt existiert (z.B. 30.02.2000 existiert nicht!)
         *  Entweder hat die Methode keine Auswirkungen oder es wird eine Ausnahme mit 
         *  dem Grund des Fehlers geworfen.
         */
        private void checkDateExists(int day, int month, int year) {
            // Prüfung, ob es ein gültiges Datum ist (Tage passen zu Monar/Jahr)
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException(invalidMonthMessage);
            }
            else {
                int monthLength = DateToolkit.getDaysInMonth(month,year);
                if (day < 1 || day > monthLength) {
                    throw new IllegalArgumentException(invalidDayMessage);
                }
            }
            // Prüfung, ob die Jahresangabe "sinnvoll" ist ( mindestens 1900 )
            if (year < 1900) {
                throw new IllegalArgumentException(invalidYearMessage);
            }
        }
    }
    
    public static boolean isUndefined( Date date){
        if( date == null ){
            return true;
        }
        String undefDatum = DateToolkit.getFormattedDate( DateToolkit.UNDEFINED_DATE );
        String datum = DateToolkit.getFormattedDate(date);
        return (datum.equals(undefDatum));
    }
 
    public static String getFormattedTimeFor(long time) {

        double _seconds = time / 1000d;
        int days = (int)_seconds / 86400;
        _seconds -= days * 86400;
        int hours = (int) _seconds / 3600;
        _seconds -= hours * 3600;
        int minutes = (int) _seconds / 60;
        _seconds -= minutes * 60;
        int seconds = (int) _seconds;
        _seconds -= seconds;
        double millis = (double) _seconds * 1000;

        String msg = "";
        DecimalFormat df = new DecimalFormat("00");
        DecimalFormat df000 = new DecimalFormat("000");
        if(days == 1) msg = df.format(days) + " Tag, ";
        if(days > 1) msg = df.format(days) + " Tage, ";
        msg += df.format(hours) + ":" + df.format(minutes) + ":" + df.format(seconds) + "." + df000.format(millis);

        return msg;
    }

    /**
     * Liefert einen Zeitstempel für JETZT
     * @return String tt.mm.yyyy hh:mm:ss
     */
    public static String getNow() {
        Date genaujetzt = new Date();
        SimpleDateFormat formaterDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        formaterDate.setTimeZone( TimeZone.getDefault() );
        return formaterDate.format(genaujetzt);
    }



    /**
     * Gibt die Zeitdifferenz als Anzahl Millisekunden zurück, das mit der Anzahl an Millisekunden
     * seit dem 1.1.1970 initialisiert worden ist
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getDateTimeDifferenceInMs(Date startDate, Date endDate) {
        long ms = endDate.getTime()-startDate.getTime();
        return ms;
    }


    /**
     * Gibt die Zeitdifferenz zwischen beiden Daten in formatierter Form aus
     * @param startDate
     * @param endDate
     * @return gibt die Zeitdifferenz als formatierten String zurück
     */
    public static String getFormattedTimeDifference(LogasDateIfc startDate, LogasDateIfc endDate) {
        return getFormattedTimeDifference(startDate.getDateDate(), endDate.getDateDate());
    }


    /**
     * Gibt die Zeitdifferenz zwischen beiden Daten in formatierter Form aus
     * @param startDate
     * @param endDate
     * @return gibt die Zeitdifferenz als formatierten String zurück
     */
    public static String getFormattedTimeDifference(Date startDate, Date endDate) {
        return getFormattedTimeDifference(getDateTimeDifferenceInMs(startDate, endDate));
    }


    /**
     * Gibt die im Zeitdifferenz als formatierten String zurück
     * @param timeDifferenceInMs        Zeitdifferenz in Millisekunden
     * @return gibt die Zeit als formatierten Text zurück
     */
    public static String getFormattedTimeDifference(long timeDifferenceInMs) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        numberFormat.setMaximumIntegerDigits(2);
        numberFormat.setMinimumIntegerDigits(2);

        StringBuffer sb= new StringBuffer();

        timeDifferenceInMs       = timeDifferenceInMs % (24*60*60*1000);
        long hours   = timeDifferenceInMs /    (60*60*1000);
        timeDifferenceInMs       = timeDifferenceInMs %    (60*60*1000);
        long minutes = timeDifferenceInMs /       (60*1000);
        timeDifferenceInMs       = timeDifferenceInMs %       (60*1000);
        long seconds = timeDifferenceInMs /          (1000);

        String suffix = "min";
        if (hours>0) {
            sb.append(numberFormat.format(hours));
            sb.append(':');
            suffix = "h";
        }
        sb.append(numberFormat.format(minutes));
        sb.append(':');
        sb.append(numberFormat.format(seconds));
        sb.append(' ');
        sb.append(suffix);
        return sb.toString();

    }



}