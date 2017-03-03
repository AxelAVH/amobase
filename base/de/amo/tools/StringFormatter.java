package de.amo.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class StringFormatter {
    
    private final static String BLANKS = "                    ";


    public static String getFloatStringFromIntString(String stringValue) {
        String dezDelim = ",";
        String thousDelim = ".";
        if (stringValue == null) {
            return null;
        }
        boolean isNegativ = stringValue.startsWith("-");
        if (isNegativ) {
            stringValue = stringValue.substring(1);
        }


        if (stringValue.length() == 0) {
            stringValue = "0" + dezDelim + " 00";
        } else if (stringValue.length() == 1) {
            stringValue = "0" + dezDelim + "0" + stringValue;
        } else if (stringValue.length() == 2) {
            stringValue = "0" + dezDelim + stringValue;
        } else {
            stringValue = stringValue.substring(0, stringValue.length() - 2) + dezDelim + stringValue.substring(stringValue.length() - 2);
        }

        if (stringValue.length() > 6) {
            stringValue = stringValue.substring(0, stringValue.length() - 6) + thousDelim + stringValue.substring(stringValue.length() - 6);
        }

        if (isNegativ) {
            stringValue = "-" + stringValue;
        }

        return stringValue;
    }

    /**
     * Auff�llen einer Zeichenkette mit einem Pr�- bzw. Postfix von Leerzeichen bis zur angegebenen L�nge. Ist die �bergebene Zeichenkette l�nger
     * als das gew�nschte Resultat, wird die Zeichenkette hinten abgeschnitten.
     * <PRE>
     * fillUp("Hallo",10) = "Hallo     ";
     * fillUp("Hallo", 3) = "Hal";
     * fillUp("Hallo",-3) = "";
     * fillUp(   null,10) = "          "
     * </PRE>
     */
    
    public static String fillUp(String zeichenkette,int length) {
        StringBuilder buf = new StringBuilder();
        if (zeichenkette == null) {
            buf.append(BLANKS);
		}
		else {
            buf.append(zeichenkette);
        }
        
        while (buf.length() < length) {
            buf.append(BLANKS);
        }
        
        return buf.substring(0, length);
    }
    
    

    /**
     * Es wird ein boolean in das Kennzeichen "J" bzw "N" umgewandelt
     * @param boolean
     * @return String mit "J" bzw "N"
     */
    
    public static String fillUp(boolean kennzeichen) {
        
        return (kennzeichen ? "J" : "N");
    }
    
    
    

    
    

    
    /**
     * H�ngt beliebige Zeichen hinten an eine Zeichenkette an, bis die geforderte L�nge erreicht ist. Falls die �bergebene Zeichenkette bereits die
     * gew�nschte L�nge hat, wird diese nicht abgeschnitten, sondern unver�ndert zur�ckgegeben.
     */
    
    public static String appendCharAufLaenge(String zeichenkette,char c,int length) {
        
        if (zeichenkette == null) {
            zeichenkette = "";
        }
        
        if (zeichenkette.length() >= length) {
            return zeichenkette;
        }
        
        StringBuilder buffer = new StringBuilder();
        buffer.append(zeichenkette);
        while (buffer.length() < length) {
            buffer.append(c);
        }
        return buffer.substring(0,length);
    }
    
    /**
     * Es wird ein int mit Nullen aufgef�llt. Die Nullen werden vorne eingef�gt.
     * <br> Ist die L�ngenangabe kleiner als die Stellenzahl des int-Wertes,
     * wird der Returnstring auf die ben�tigte L�nge f�r den int-Wert vergr�ssert.<br>
     * <PRE>
     * Beispiel:
     * String sneu = de.amo.tools.StringFormatter.fillUpExact(125,5);
     * --> sneu = "00125";
     * String sneu2 = de.amo.tools.StringFormatter.fillUpExact(125000,5,0);
     * --> �bergebener int:Exception
     * </PRE>
     * @return String mit Nullen linksb�ndig aufgef�llte ganze Zahl
     */
    public static String fillUpExact(int izahl, int len) {
        String ret = "";
        String hilf;
        for (int i=0; i<len; i++) {
            ret = ret + "0";
        }
        hilf = ret.valueOf(izahl);
        int endIndex = len - hilf.length();
        if (endIndex < 0) {
            System.out.println("�bergebener int: " + izahl + " ist gr��er als �bergebene L�nge " + len);
        }
        else {
            ret = ret.substring(0,endIndex) + hilf;
        }
        return ret;
    }
    
    /**
     * Es wird ein long mit Nullen linksb�ndig aufgef�llt.
     * <br> Ist die L�ngenangabe kleiner als die Stellenzahl des int-Wertes,
     * wird der Returnstring auf die ben�tigte L�nge f�r den int-Wert vergr�ssert.<br>
     * Achtung: der long darf nicht groesser als die �bergebene L�nge sein. In diesem Fall wird eine
     * Assertion geworfen und der long als String, unabhaengig von der Laenge, zurueckgegeben.
     * <PRE>
     * Beispiel:
     * String sneu = de.amo.tools.StringFormatter.fillUp(125,5);
     * --> sneu = "00125";
     * String sneu2 = de.amo.tools.StringFormatter.fillUp(125000,5,0);
     * --> sneu2 = "125000";
     * </PRE>
     * @return String mit Nullen linksb�ndig aufgef�llte ganze Zahl
     */
    public static String fillUp(long lzahl, int len) {
        String ret = "";
        String hilf;
        for (int i = 0; i < len; i++) {
            ret = ret + "0";
        }
        hilf = ret.valueOf(lzahl);
        int endIndex = len - hilf.length();
        if (endIndex < 0) {
            System.out.println("�bergebener long: " + lzahl + " ist gr��er als �bergebene L�nge " + len);
            ret = hilf;
		}
		else {
            try {
                ret = ret.substring(0, endIndex) + hilf;
			}
			catch (StringIndexOutOfBoundsException e) {
                System.out.println(e.toString());
            }
        }
        return ret;
    }

    public static String fillUpLeft(String value, int maxLength) {
        return fillUpLeft(value, maxLength, " ");
    }

    public static String fillUpLeft(String value, int maxLength, String fillChar) {

        if (maxLength <= 0) return value;
        if (value.length() >= maxLength) return value;

        if (value == null) value = "";

        if (fillChar == null) fillChar = " ";

        int offSet = maxLength - value.length();
        for (int i = 0; i < offSet; i++) {
            value = fillChar + value;
        }
        return value;
    }

    /**
     * Ermittelt die Anzahl der Zeichen hinter dem ersten Vorkommen des Trenners im QuellenString.
     */
    private static int getAnzahlNachStellen(String src, String trenner) {
        int ret = 0;
        int indexTrenner = src.indexOf(trenner);
        String hinten = src.substring(indexTrenner+1);
        ret = hinten.length();
        return ret;
    }

    public static String convertZadatUmrechnungskurs(double d, int len, int nk) {
        String ret = "";
        double dd = d;
        long faktor = 1;
        //
        while (d>1) {
            nk--;
            d = d/10;
        }
        for (int i=0;i<nk;++i) {
            faktor = faktor * 10;
        }
        if ((dd*faktor)>Long.MAX_VALUE) {
            return "Overflow";
        }
        dd = ((double) java.lang.Math.round(dd * faktor)) / faktor;
        ret = Double.toString(dd);
        
        //Umwandlung Dezimalpunkt in Dezimalkomma
        ret = ret.replace('.',',');
        int ilen = ret.length();
        for (int i=ilen; i<len; i++) {
            ret = ret + "0";
        }
        return ret;
    }
    /**
     * Es wird ein double mit Nullen linksb�ndig aufgef�llt und der Dezimalpunkt nicht dargestellt.
     * <br> Ist die L�ngenangabe kleiner als die Stellenzahl des int-Wertes,
     * wird der Returnstring auf die ben�tigte L�nge f�r den int-Wert vergr�ssert.<br>
     * Achtung: Wird als Nachkommaanzahl 0 angegeben und liegt ein double mit Nachkommastellen
     * vor, werden diese nicht gerundet und auch nicht abgeschnitten! Hier ist die Methode fillUpDezFix
     * zu verwenden!
     * <PRE>
     * Beispiel:
     * String sneu = de.amo.tools.StringFormatter.fillUp(125.82,10,2);
     * --> sneu = "0000012582";
     * String sneu2 = de.amo.tools.StringFormatter.fillUp(1250.00,10,0);
     * --> sneu2 = "0000125000";
     * </PRE>
     * @param double zu formatiernde Dezimalzahl
     * @param L�nge des Returnstrings
     * @param Anzahl der Nachkommastellen
     * @return String mit Nullen linksb�ndig aufgef�llter String
     */
    public static String fillUp(double dbl, int len, int nk) {
        String ret = "";
        String hilf, ganz, dez;
        int ipt;
        for (int i=0; i<len; i++) {
            ret = ret + "0";
        }
        hilf = ret.valueOf(dbl);
        hilf = hilf.trim();
        ipt  = hilf.indexOf(".");
        if (ipt == -1) {
            ganz = hilf;
            dez ="";
        }
        else {
            ganz = hilf.substring(0,ipt);
            dez = hilf.substring(ipt+1,hilf.length());
        }
        int subind = len-ganz.length()-dez.length();
        if (subind<0) {
            ret = ganz + dez;
        }
        else {
            try {
                ret = ret.substring(0,len-ganz.length()-dez.length()) + ganz + dez;
            }
            catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
    /**
     * Umwandlung eines doubles in einen String.
     * <br> Der double-Wert wird auf die �bergebene Anzahl von Nachkommastellen gerundet.<br>
     * <PRE>d = 19.355
     * String s = de.amo.tools.StringFormatter.convertToString(d,2,5);
     * --> s = "19.36"
     * </PRE>
     * @param double zu formatierende Dezimalzahl
     * @param Anzahl der Nachkommastellen
     * @param L�nge des Returnstrings
     * @return String mit Nullen linksb�ndig aufgef�llter String
     * ggf. wird der String groesser als die angegebene L�nge
     * bzw. "Overflow" wenn Dezimalzahl zum Runden zu gro�
     * @see de.logas.basics.QuantitaetImpl Methode getFormatedMenge(int dez)
     */
    public static String convertToString(double dbl, int nk, int len) {
        String ret = "";
        String hilf;
        double dd = dbl;
        long faktor = 1;
        //
        for (int i=0;i<nk;++i) {
            faktor = faktor * 10;
        }
        if ((dd*faktor)>Long.MAX_VALUE) {
            System.out.println("der zu rundende Wert " + (dd*faktor) + " �bersteigt die Maximalgrenze von " + Long.MAX_VALUE);
            return "Overflow";
        }
        dd = ((double) java.lang.Math.round(dd * faktor)) / faktor;
        // fuehrende Blanks
        for (int i=0; i<len; i++) {
            ret = ret + " ";
        }
        hilf = ret.valueOf(dd);
        hilf = hilf.trim();
        int ipt = hilf.lastIndexOf(".");
        int il;
        if (ipt>-1) {
            il = hilf.substring(ipt+1).length();
        }
        else {
            il = 0;
            hilf = hilf + ".";
        }
        for (int i=0;i<(nk-il);++i) {
            hilf = hilf + "0";
        }
        if (nk<1) {
            hilf = hilf.substring(0,hilf.length() - il -1);
        }
        if (hilf.length()>len) { //String zu gro�
            ret = hilf;
        }
        else {
            try {
                ret = ret.substring(0,len-hilf.length()) + hilf;
            }
            catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }
    
    
    /**
     * Umwandlung eines doubles in einen String.
     * <br> Der double-Wert wird auf die �bergebene Anzahl von Nachkommastellen gerundet.<br>
     * <PRE>d = 19.355
     * String s = de.amo.tools.StringFormatter.convert(d,2);
     * --> s = "19.36"
     * </PRE>
     * @param double zu formatiernde Dezimalzahl
     * @param Anzahl der Nachkommastellen
     * @return String der konvertierte String bzw. "Overflow" wenn Dezimalzahl zum Runden zu gro�
     */
    public static String convertToString(double dbl, int nk) {
        String ret = "";
        String hilf;
        // Rundung des doubles auf die �bergebene Anzahl von Dezimalstellen
        double dd = dbl;
        long faktor = 1;
        for (int i=0;i<nk;++i) {
            faktor = faktor * 10;
        }
        if ((dd*faktor)>Long.MAX_VALUE) {
            return "Overflow";
        }
        dd = ((double) java.lang.Math.round(dd * faktor)) / faktor;
        // Umwandlung des double-Wertes in einen String
        hilf = ret.valueOf(dd);
        int eZeichen = hilf.indexOf("E");
        if (eZeichen==-1) {
            // keine scientific notation
            int ipt = hilf.lastIndexOf(".");
            int il; // L�nge der Zeichen hinter dem Dezimalpunkt
            if (ipt>-1) {
                il = hilf.substring(ipt+1).length();
            }
            else {
                il = 0;
                hilf = hilf + ".";
            }
            // Auff�llen der nicht signifikanten Stellen hinter dem Dezimalpunkt
            for (int i=0;i<(nk-il);++i) {
                hilf = hilf + "0";
            }
            if (nk<1) {
                hilf = hilf.substring(0,hilf.length() - il -1);
            }
            ret = hilf.trim();
        }
        else {
            String dezis;
            // es liegt eine Zahl >= 10 hoch 7 vor oder eine Zahl < 10 hoch -3
            // Ermittlung des Exponenten
            String exponent = hilf.substring(eZeichen+1);
            // der �briggebliebene Quotient von hilf und 10**exponent
            String quot = hilf.substring(0,eZeichen);
            quot = StringFormatter.removeString(quot,".");
            int iExp = 0;
            try {
                iExp = Integer.parseInt(exponent);
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
            int lenQuot = iExp + 1 - quot.length();
            //Auffuellen mit Nullen bis zum Dezimalpunkt
            for (int i=0;i<lenQuot;++i) {
                quot = quot + "0";
            }
            if (lenQuot<0) {
                dezis = quot.substring(iExp+1);
                quot = quot.substring(0,iExp+1);
                for (int i=dezis.length();i<nk;++i) {
                    dezis = dezis + "0";
                }
            }
            else {
                dezis = "";
                for (int i=0;i<nk;++i) {
                    dezis = dezis + "0";
                }
            }
            quot = quot + "." + dezis;
            ret = quot;
        }
        return ret;
    }
    





    private static String VIELENULLEN = "000000000000000000000000000000000000000000000";
    /**
     * Universelle Formatierung einer Zahl rechtsb�ndig in einen String<br>
     * Das Trennzeichen sowie die F�llzeichen vor der Ziffer sind frei w�hlbar.<br>
     * Rechstseitig vom Trennzeichen evtl. vorh. Leerraum wird mit "0" aufgef�llt<br>
     * Linksseitig wird mit dem �bergebenen F�llzeichen aufgef�llt<br>
     * Passt die Zahl nicht in das Format, kommt eine HinweisException<br>
     * Nachkommastellen werden gem�� der Nachkommastellenzahl gerundet<br>
     *
     * @param double die zu formatierende Zahl
     * @param int L�nge des return-String
     * @param int Anzahl Stellen nach dem Komma
     * @param String Trennzeichen (String beliebiger L�nge)
     * @param String Fuellzeichen zum Linksseitigen Auff�llen (String mit L�nge 1)
     * @author Axel M�ller
     */
    public static String convertToString(double wert, int laenge, int nachKommaLaenge, String trennZeichen, String fuellZeichen) {

        if (nachKommaLaenge == 0) {
            trennZeichen = "";
        }
        boolean isNegativ = false;
        int vorzeichenLaenge = 0;
        if (wert < 0) {
            isNegativ = true;
            vorzeichenLaenge = 1;
        }

        if (fuellZeichen.length() == 0) {
            System.out.println("convertToString() : Laenge des Fuellzeichens ist 0");
        }
        double factor = java.lang.Math.pow(10, nachKommaLaenge);
        if (isNegativ) {
            factor = factor * (-1);
        }
        wert = wert * factor;
        wert = java.lang.Math.round(wert);
        
        Double	test	= new Double(wert);
        long	ltest	= test.longValue();
        String	str 	= Long.toString(ltest);
        int 	l		= str.length();
        
        int fehlendeLaenge = nachKommaLaenge - l;
        if (fehlendeLaenge > 0) {
            str = VIELENULLEN.substring(0, fehlendeLaenge) + str;
            l = l + fehlendeLaenge;
        }
        String wertVorKomma  = null;
        String wertNachKomma = null;
        wertVorKomma = str.substring(0, (l - nachKommaLaenge));
        wertNachKomma = str.substring(l - nachKommaLaenge, l);
        
        for (int i = 0; wertNachKomma.length() < nachKommaLaenge; i++) {
            wertNachKomma = wertNachKomma + "0";
        }
        
        int vorKommaRest = laenge - nachKommaLaenge - trennZeichen.length() - wertVorKomma.length();
        if ((vorKommaRest-vorzeichenLaenge) < 0) {
            System.out.println("convertToString() Wert " + wert / factor + " zu gro� f�r das Format " + laenge + "." + nachKommaLaenge);
        }
        int startWert = 0;
        if (wertVorKomma.equals("")) {
            wertVorKomma = "0";
            startWert = 1;
        }
        if (isNegativ) {
            wertVorKomma = "-"+wertVorKomma;
            startWert++;
        }
        for (int i = startWert; i < vorKommaRest; i++) {
            wertVorKomma = fuellZeichen + wertVorKomma;
        }
        
        return wertVorKomma + trennZeichen + wertNachKomma;
    }
    
	/**
	 * * Universelle Formatierung einer Zahl rechtsb�ndig in einen String<br>
	 * Das Trennzeichen sowie die F�llzeichen vor der Ziffer sind frei w�hlbar.<br>
	 * Rechstseitig vom Trennzeichen evtl. vorh. Leerraum wird mit "0" aufgef�llt<br>
	 * Linksseitig wird mit dem �bergebenen F�llzeichen aufgef�llt<br>
	 * Passt die Zahl nicht in das Format, kommt eine HinweisException<br>
	 * Nachkommastellen werden gem�� der Nachkommastellenzahl gerundet<br>
	 * Angabe der Minimalen Anzahl der Stellen nach dem Komma
	 *
	 * @param wert					Double Wert der in String konvertiert wird
	 * @param gesamtLaenge			L�nge des return-String
	 * @param stellenNachKomma		Anzahl Stellen nach dem Komma
	 * @param minimumNachkommaStellen Minimale Anzahl der Nachkommastellen
	 * @param trennzeichen			Trennzeichen (String beliebiger L�nge)
	 * @param fuellZeichen			Fuellzeichen zum Linksseitigen Auff�llen (String mit L�nge 1)
	 * @return in String konvertierter Wert mit o.g. Parametern
	 */
	public static String convertToString(double wert, int gesamtLaenge, int stellenNachKomma, int minimumNachkommaStellen, String trennzeichen, String fuellZeichen) {
		String stringWert = convertToString(wert, gesamtLaenge, stellenNachKomma, trennzeichen, fuellZeichen);
        if ( stringWert.contains(".") && !stringWert.contains(",") ) {
            stringWert = stringWert.replace(".", ",");
        }
		String[] splittedValues = stringWert.split(",");
		String vorKommaStellen = splittedValues[0];
		String nachKommaStellen = splittedValues[1];

		while (nachKommaStellen.endsWith("0")) {
			if (nachKommaStellen.length() <= minimumNachkommaStellen) {
				break;
			}
			nachKommaStellen = nachKommaStellen.substring(0, nachKommaStellen.length() - 1);
		}

		return vorKommaStellen + trennzeichen + nachKommaStellen;
	}



    /**
     * Es werden in einem String alle vorkommenden TeilStrings herausgel�scht.
     * <br>
     * Beispiel: String input = "Dies ist ein String mit Blanks   "
     * String output = de.amo.tools.StringFormatter.removeString(input," ");
     * --> output = "DiesisteinStringmitBlanks"
     * <br>
     * @param String str der zu bearbeitende String
     * @param String sToDel der aus str herauszunehmende String
     * @return String der um sToDel verk�rzte String
     */
    public static String removeString(String str, String sToDel) {
        if (str == null) {
            str = "";
        }
        if (sToDel == null) {
            sToDel = "";
        }
        String ret = str;
        String sVorne,sHinten;
        int iToDel;
        //
        iToDel = ret.indexOf(sToDel);
        while (iToDel > (-1)) {
            sVorne = ret.substring(0,iToDel);
            sHinten = ret.substring(iToDel+1);
            ret = sVorne + sHinten;
            iToDel = ret.indexOf(sToDel);
        }
        return ret;
    }
    /**
     * Es werden in einem String alle f�hrenden TeilStrings herausgel�scht.
     * <br>
     * Aus dem �bergebenen String werden alle Teilstrings von links nach rechts herausgel�scht,
     * solange bis ein differierendes Zeichen auftritt.
     * <br>
     * Beispiel-Aufruf:
     * <PRE>
     * String input = "0013"
     * String output = de.amo.tools.StringFormatter.removeFuehrendeStrings(output,"0");
     * --> output = "13"
     * </PRE>
     * @param String str der zu bearbeitende String
     * @param String sToDel der aus str herauszunehmende String
     * @return String der um sToDel gek�rzte String
     */
    public static String removeFuehrendeStrings(String str, String sToDel) {
        if (str == null) {
            str = "";
        }
        if (sToDel == null) {
            sToDel = "";
        }
        String ret = str;
        int laengeToDel;
        //
        laengeToDel = sToDel.length();
        if (laengeToDel!=0) {
            while (ret.startsWith(sToDel)) {
                ret = ret.substring(laengeToDel);
            }
        }
        return ret;
    }

    /**
     * Entfernen von gleichen f�hrenden Zeichen aus einem String<br>
     * Schneller als "removeFuehrendeStrings()", da nur genau einmal ein neuer
     * String per substring-Methode instantiiert wird.
     */
    public static String removeLeadingChars(String str, char ctoDel) {
        int l = str.length();
        int startPos = 0;
        for (int i = 0; i < l; i++) {
            if (str.charAt(i) != ctoDel) {
                startPos = i;
                break;
            }
        }
        return str.substring(startPos);
    }
    
    /**
     * Es wird ein String, der nur Ziffern enthalten darf, in ein Double umgewandelt.
     * <PRE>
     * Beispiel:
     * double d = de.amo.tools.StringFormatter.toDouble("0012511000,5);
     * --> d = 125.11
     * double d = de.amo.tools.StringFormatter.toDouble("125000",1);
     * --> d = 12500.0
     * </PRE>
     * @param String umzuwandelnder String
     * @param int Anzhal der Nachkommastellen des Strings
     * @return double Wert des �bergebenen Strings bzw 0, wenn �bergabewert ung�ltig
     */
    public static double toDouble(String str, int nk) {
        double d;
        long lzahl;
        int faktor = 1;
        for (int i=0; i<nk; i++) {
            faktor = faktor * 10;
        }
            lzahl = Long.parseLong(str);
        d = lzahl;
        d = d/faktor;
        return d;
    }
    
    /**
     *	Sucht in dem String in nach einem Token, und ersetzt alle Vorkommen
     *	durch newToken
     *
     *@param in	Der Eingabestring
     *@param token Die Zeichenkette, die ersetzt werden soll
     *@param newToken Die Zeichenkette, die jedes Vorkommen von token ersetzen soll
     *@return Der bereinigte String. Wenn token nicht im String vorkommnt, wird nichts getan
     **/
    public static String searchAndReplace(String in, String token, String newToken) {
        //
        //	Token suchen
        //
        int pos	= in.indexOf(token);
        
        //
        //	Wenn der Token nicht da ist kann man abbrechen
        //
        if (pos==-1) {
            return in;
		}
		else {
            //
            //	Ergebnisbuffer anlegen, der bereits alle Zeichen bis zum ersten Vorkommen
            //	besitzt
            //
            StringBuilder buffer	= new StringBuilder(in.substring(0,pos) + newToken);
            
            //
            //	Die L�nge des Tokens ist interessant, da diese immer auf den Startoffset
            //	addiert werden mu�
            //
            int len	= token.length();
            
            //
            //	Gibt den neuen Startoffset an.
            //
            int start	= pos + len;
            
            //
            //	N�chsten token suchen
            //
            pos	= in.indexOf(token,start);
            
            //
            //	Solange der Token gefunden wird...
            //
            while(pos!=-1) {
                //
                // ... Zeichen aus in kopieren und den neuen Token anh�ngen
                //
                buffer.append(in.substring(start,pos) + newToken);
                
                //
                //	Den neuen Suchpunkt berechnen
                //
                start	= pos + len;
                
                //
                //	und suchen
                //
                pos	= in.indexOf(token,start);
            }
            
            //
            //	Der Rest von in mu� auch noch angeh�ngt werden
            //
            buffer.append(in.substring(start));
            
            //
            //	Ich habe fertig
            //
            return buffer.toString();
        }
    }
    
    
    /**
     * Es werden in einem String EBCDIC-Zahlen in lesbare Zahlen umgeandelt (ASCII?).
     * Enth�lt den in VZADATu3 enthaltenen Algorithmus EBC2ASC.
     * @param String str die zu bearbeitende EBCDIC-Zahl
     * @return String die konvertierte Zahl als String
     * @see Umschl�sselungstabellen von OFD D�sseldorf (als Fax erh�ltlich)
     */
    public static String toAscii(String ebc) {
        String asc = ebc;
        int faktor=1;
        char last = ' ';
        String vorn = "";
        try {
            last = ebc.charAt(ebc.length()-1);
            vorn = ebc.substring(0,ebc.length()-1);
        }
        catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        int i=0;
        //Auswertung des letzten Buchstabens
        switch (last) {
            case '{': { i=0; break;}
            case 'A': { i=1; break;}
            case 'B': { i=2; break;}
            case 'C': { i=3; break;}
            case 'D': { i=4; break;}
            case 'E': { i=5; break;}
            case 'F': { i=6; break;}
            case 'G': { i=7; break;}
            case 'H': { i=8; break;}
            case 'I': { i=9; break;}
            case '}': { i=0; faktor=-1; break;}
            case 'J': { i=1; faktor=-1; break;}
            case 'K': { i=2; faktor=-1; break;}
            case 'L': { i=3; faktor=-1; break;}
            case 'M': { i=4; faktor=-1; break;}
            case 'N': { i=5; faktor=-1; break;}
            case 'O': { i=6; faktor=-1; break;}
            case 'P': { i=7; faktor=-1; break;}
            case 'Q': { i=8; faktor=-1; break;}
            case 'R': { i=9; faktor=-1; break;}
        }
        long l = Long.parseLong(vorn);
        l = l*10 + i;
        l = l*faktor;
        asc = asc.valueOf(l);
        return asc;
    }
    public static String eingabe(String ausgabe) {
        String s = "" ;
        try {
            BufferedReader din = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(ausgabe);
            s = din.readLine();
        }
        catch (Exception e) {
            System.err.println("BasisTester.eingabe -> "+e);
        }
        return s;
    }
    /**
     * Bildet die aktuelle Zeit als formattieren String
     * @return String Datum und aktuelle Zeit in der Form "jjjjmmttHHMMSSxxxx" mit xxxx = MilliSekunden
     */
    public static String getAktuelleZeitString() {
        String ret = "";
        // Bilden der aktuellen Zeit
        GregorianCalendar cal = new GregorianCalendar();
        String date = StringFormatter.erzeugeDatumsString(cal);
        String time = (new java.util.Date()).toString().substring(11,19);
        String hhmmss = StringFormatter.removeString(time,":");
        String msek = "";
        long secs = (new java.util.Date()).getTime();
        long ganz = secs/1000;
        long ms = secs - (ganz * 1000);
        msek = msek.valueOf(ms);
        int iToFill = 3 - msek.length();
        for (int i=0; i<iToFill; i++) {
            msek += "0";
        }
        ret = date + hhmmss + msek;
        return ret;
    }
    /**
     * Liefert f�r den �bergebenen GregorianCalendar den String, der in der Variablen "datum" abzulegen w�re.
     */
    private static String erzeugeDatumsString(Calendar cal) {
        // Tag 2-stellig formatieren
        int iTag = cal.get(Calendar.DAY_OF_MONTH);
        String tag = StringFormatter.fillUp(iTag,2);

        // Monat 2-stellig formatieren
        int iMonat = cal.get(Calendar.MONTH) + 1;
        String monat = StringFormatter.fillUp(iMonat,2);

        // Jahr 4-stellig
        String jahr = "0000";
        int iJahr = cal.get(Calendar.YEAR);
        if ((iJahr >= 1990) && (iJahr < 2010)) {
            jahr = Integer.toString(iJahr);
		}
		else {
            jahr = "0000" + Integer.toString(iJahr);
            int iL = jahr.length();
            jahr = jahr.substring(iL-4,iL);
        }

        return jahr + monat + tag;
    }

    /**
     * Formatiert einen double-Wert mit Angabe einer Zahl von Nachkommastellen
     *
     * @param value             Die zu formatierende Zahl
     * @param maxFractionDigits Die maximale Zahl der Nachkommastellen
     * @param minFractionDigits Die minimale Zahl der Nachkommastellen
     */
    public static String formatDouble(double value, int maxFractionDigits, int minFractionDigits) {
        //String pattern = "###,##0.00";
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.GERMAN);
        formatter.setGroupingUsed(false);
        formatter.setMaximumFractionDigits(maxFractionDigits);
        formatter.setMinimumFractionDigits(minFractionDigits);
        formatter.setGroupingUsed(true);
        return formatter.format(value);
    }

}
