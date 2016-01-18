package de.amo.tools;

import java.util.*;
import java.util.Vector;
import java.util.StringTokenizer;

/**
 *
 *  Die Klasse amo.ParameterString dient dazu, einen String mit Platzhaltern zu definieren,
 *  die erst zu einem sp�teren Zeitpunkt definiert werden m�ssen. Die Initialisierung
 *  erfolgt mit einem String, der Platzhalter der Form %0 bis %9 enth�lt. Diese k�nnen
 *  entweder einzeln �ber <code>setParameter(int,String)</code> oder insgesamt �ber
 *  <code>setParameters(String[])</code> definiert werden. Derselbe Parameter kann auch
 *  mehrfach in einem String vorkommen und nacheinander mehrfach ersetzt werden. Um ein
 *  %-Zeichen im Zielstring zu erhalten, m�ssen zwei %-Zeichen nacheinander vorkommen.
 *  <p>
 *  Wie in Java �blich, beginnt die Z�hlung der Parameter bei 0 (hier also bei %0) !
 *  <p>
 *  Beispiele:<br>
 *  "Die Datei %0 konnte nicht gefunden werden. Grund: %1."
 *  "Die Summe aller %0 ist gr��er als 100%%."
 *  <p>
 *
 *  @author Reinhard Rust, LOGAS GmbH
 *
 *  @see #setParameter
 *  @see #setParameters
 */

 public class ParameterString
{
    /**
      *  Sonderzeichen f�r einen Parameter, dessen Nummer folget (z.B. %3)
      */
    private final String PARAM_CHAR = "%";

    /**
      *  "Rohstring", in dem Platzhalter enthalten sind und noch kein Parameter ersetzt wurde.
      */
    private String rawString;

    /**
      *  String, in dem alle vorkommenden Parameter ersetzt wurden
      */
    private StringBuilder replacedString = null;

    /**
      *  Feld mit den Platzhaltern f�r die Ersetzungen.
      */
    private List replacements = new ArrayList();


    /**
      *  Legt einen amo.ParameterString mit leerem Rohstring an
      */
    public ParameterString() {
        setRawString("");
    }

    /**
      *  Legt einen amo.ParameterString mit einem Rohstring an
      */
    public ParameterString(String s) {
        setRawString(s);
    }

    /**
      *  Setzt den Rohstring f�r diesen amo.ParameterString
      */
    public void setRawString(String s) {
        rawString = s;
        replacedString = null;
    }

    /**
      *  Liefert den Rohstring f�r diesen amo.ParameterString
      */
    public String getRawString() {
        return rawString;
    }

    /**
      *  Definiert einen Parameter f�r die Ersetzung.
      *
      *  @param idx           Index (Nummer) des Parameters. Wertebereich: 0..9
      *  @param replacement   String, der anstelle des Parameters eingesetzt werden soll
      *
      *  @see #getParameter
      *  @see #setParameters
      */
    public void setParameter(int idx, Object replacement) {
        if (idx >= replacements.size()) {
            for (int i = replacements.size()-1; i < idx; i++) {
                replacements.add(null);
            }
        }
        replacements.set(idx, replacement);
        replacedString = null;
    }

    /**
      *  Liefert einen aktuellen Parameter
      *
      *  @param idx  Index (Nummer) des Parameters. Wertebereich: 0..9
      *
      *  @see #setParameter
      *  @see #setParameters
      */
    public Object getParameter(int idx) {
        return replacements.get(idx);
    }

    /**
      *  Definiert den einzigen Parameter f�r die Ersetzung.
      */
    public void setParameter(Object p0) {
        setParameter(0, p0);
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1) {
        setParameters(Arrays.asList(new Object[] {p0, p1}));
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1, Object p2) {
        setParameters(Arrays.asList(new Object[] {p0, p1, p2}));
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1, Object p2, Object p3) {
        setParameters(Arrays.asList(new Object[] {p0, p1, p2, p3}));
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1, Object p2, Object p3, Object p4) {
        setParameters(Arrays.asList(new Object[] {p0, p1, p2, p3, p4}));
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
        setParameters(Arrays.asList(new Object[] {p0, p1, p2, p3, p4, p5}));
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
        setParameters(Arrays.asList(new Object[] {p0, p1, p2, p3, p4, p5, p6}));
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
        setParameters(Arrays.asList(new Object[] {p0, p1, p2, p3, p4, p5, p6, p7}));
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
        setParameters(Arrays.asList(new Object[] {p0, p1, p2, p3, p4, p5, p6, p7, p8}));
    }

    /**
      *  Definiert mehrere Parameter f�r die Ersetzung.
      */
    public void setParameters(Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
        setParameters(Arrays.asList(new Object[] {p0, p1, p2, p3, p4, p5, p6, p7, p8, p9}));
    }

    /**
      *  Definiert mehrere (i.d.R. alle) Parameter f�r die Ersetzung. Dabei werden alle
      *  �bergebenden Parameter f�r die Ersetzung gespeichert. Enth�lt <code>params</code>
      *  weniger Elemente als bisher definiert wurden, so werden nur diese �berschrieben und
      *  die h�heren Elemente bleiben bestehen.
      *
      *  @param params  Array mit den Ersetzungsparametern
      *
      *  @see #setParameter
      *  @see #getParameter
      */
    public void setParameters(Object[] params) {
        setParameters(Arrays.asList(params));
    }

    public void setParameters(List l) {
        Iterator iter = l.iterator();
        for (int i=0; iter.hasNext(); i++) {
            setParameter(i, iter.next());
        }
    }

    /**
      *  L�scht alle Parameter f�r die Ersetzung.
      *
      *  @see #setParameter
      *  @see #setParameters
      */
    public void clearParameters() {
        replacements.clear();
        replacedString = null;
    }

    /**
      *  Liefert den Ergebnisstring, in dem alle formalen Parameter (%0..%9) im Rohstring
      *  durch die aktuellen Parameter ersetzt wurden.
      *
      *  @see #setParameter
      *  @see #setParameters
      */
    public String toString() {
        if (replacedString == null) {
            computeReplacedString();
        }
        return replacedString.toString();
    }

    /**
      *  Diese Methode f�hrt die eigentliche Parameterersetzung durch. Der String wird
      *  auf das %-Zeichen hin durchsucht. Das nachfolgende Zeichen bestimmt den Index f�r
      *  f�r die Ersetzung.
      */
    private void computeReplacedString() {
        int idx = 0;                          // Param-Index
        String currentToken = null;           // aktuell gelesenens Token (% als Trenner)
        String currentReplacement = null;     // Ersetzung des aktuellen Tokens
        replacedString = new StringBuilder();  // Zielstring initialisieren

        // StringTokenizer als Parser f�r die %-Zeichen
        StringTokenizer tokenizer = new StringTokenizer(rawString,PARAM_CHAR,true);
        while (tokenizer.hasMoreTokens()) {
            currentToken = tokenizer.nextToken();
            // Fall: aktuelles Token besteht nur aus genau dem %-Zeichen
            //       Dann Folgetoken lesen und ggf. erstes Zeichen (z.B. '3' f�r "%3") ersetzen
            if (currentToken.equals(PARAM_CHAR)) {
                currentToken = tokenizer.nextToken();
                idx = currentToken.charAt(0)-(int)'0'; // Param-Index ermitteln
                if (idx>=0 && idx<replacements.size() && replacements.get(idx)!=null) {
                    // bei g�ltigem Parameter Ersetzung festlegen
                    currentReplacement = replacements.get(idx).toString();
                }
                else {
                    // sonst leere Ersetzung
                    currentReplacement = "";
                }
                // Ersetzung im aktuellen Token durchf�hren
                currentToken = currentReplacement + currentToken.substring(1);
            }
            // aktuelles Token, das ggf. vorher erweitert wurde, anh�ngen
            replacedString.append(currentToken);
        }
    }

}