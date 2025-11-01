package de.amo.tools;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 * Contains a number of static methods which facilitate the interaction
 * with the user from within Win32 console applications.
 * @author Axel Möller, Arnd Boehm
 */

public abstract class IOTools {

    private static boolean isRunningInsideServer = false;
    private static boolean infoMode = false;

    // Puffer für die Ein-/Ausgabe innerhalb eines laufenden Servers
    private static Map outputBuffers = new HashMap();    // Session-ID -> String-Buffer
    private static Map inputBuffers  = new HashMap();    // Session-ID -> String-Buffer
    private static Set interrupted   = new HashSet();    // Session-ID

    public void headLine() {
        String now = formatDate(new Date());
        String headline = "Tool started: " + this.getClass().getName() + "    " + now;
        println("\n" + headline);
        String rohling = "---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
        println(rohling.substring(0,headline.length()));
    }

    /**
     * Liest eine Zeile über die Eingabe von der Konsole (System.in)
     * @param ausgabe optionaler Führungstext
     */
    private static String readFromConsole(String ausgabe) throws IOException {
        if (ausgabe!=null) {
            print(ausgabe);
        }
        BufferedReader din = new BufferedReader(new InputStreamReader(System.in));
        String inp = din.readLine();
        return inp;
    }

    /**
     * Liest eine Zeile über die Eingabe des Austausch-Puffers (innerhalb des Servers)
     * @param ausgabe optionaler Führungstext
     */
    private static String readFromExchangeBuffer(String ausgabe) {
        if (ausgabe!=null) {
            print(ausgabe);
        }
        StringBuilder ret = new StringBuilder();
        while(true) {
            StringBuilder buffer = getCurrentInputBuffer();
            // Zeitgesteuert warten, bis Daten im Puffer sind
            if (buffer.length()==0) {
                try {Thread.sleep(200);} catch (InterruptedException e) {};
                continue;
            }
            // bis zum nächsten Trennzeichen (#0) lesen und konsumieren
            while (buffer.length()>0) {
                char c = buffer.charAt(0);
                buffer.deleteCharAt(0);
                if (c==(char)0) {
                    break;
                }
                else {
                    ret.append(c);
                }
            }
            return ret.toString();
        }
    }

    /**
     * Eingaberoutine
     * @param ausgabe Führungstext vor der erwarteten Eingabe
     */
    public static String input(String ausgabe) {
        String s = "" ;
        try	{
            // je nach Ausführungsmodus die passende Eingabe-Variante wählen
            if (isRunningInsideServer()) {
                s = readFromExchangeBuffer(ausgabe);
                println(s);
            }
            else {
                s = readFromConsole(ausgabe);
            }
        } catch (Exception e) {
            println("MRTool.Eingabe -> "+e);
        }
        return s;
    }

    /**
     * @param ausgabe Anzuzeigender Text
     * @param defaultBeforeTimeout Returnwert, wenn man nichts eingibt, außer Return
     * @param timeoutInSeconds Zeit, nach der die Methode spätestens zurückkehrt
     * @param defaultAfterTimeout Returnwert nach ablauf der Zeit
     * @param printDefaults Sollen dem Benutzer die Dafaults angezeigt werden? Ex: "prompt [defaultWert][10s|timeoutWert] : "
     */
    public static String inputWithTimeout(String ausgabe, String defaultBeforeTimeout, int timeoutInSeconds, String defaultAfterTimeout, boolean printDefaults) {
        String s = "" ;
        try	{
            BufferedReader din = new BufferedReader(new InputStreamReader(System.in));
            long stop = System.currentTimeMillis() + (timeoutInSeconds * 1000);
            print(ausgabe);
            if(printDefaults){
                // DefaultBeforeTimeout, ist nur interessant, wenn er sich von der Eingabe unterscheidet
                if(defaultBeforeTimeout==null || (defaultBeforeTimeout!=null && defaultBeforeTimeout.length()>0)){
                    print(" ["  + defaultBeforeTimeout + "]");
                }
                // Timeout ist immer interessant
                print(" [" + timeoutInSeconds + "s->" + defaultAfterTimeout + "]");
            }
            print(" : ");
            while(true){
                if(System.currentTimeMillis() > stop){
                    return defaultAfterTimeout;
                }
                if(din.ready()){
                    s = din.readLine();
                    if (s.length()==0){
                        return defaultBeforeTimeout;
                    }
                    return s;
                }
                Thread.sleep(200);
            }
        } catch (Exception e) {
            println("IOTool.Eingabe -> "+e);
        }
        return s;
    }

    /**
     * Eingaberoutine
     * @param ausgabe Führungstext vor der erwarteten Eingabe
     * @param def Defaultwert bei leerer Eingabe
     */
    public static String input(String ausgabe, String def) {
        String s = input(ausgabe + " ["  + def + "] : ");
        return s.length()==0 ? def : s;
    }

    public static double inputDouble(String ausgabe) {
        String erg;
        boolean firstTime = true;
        Double D;
        double i;
        while (true) {
            erg = input(ausgabe);
            try {
                D = new Double(erg);
                i = D.doubleValue();
                return i;
            } catch (NumberFormatException n) {
                if (firstTime) {
                    ausgabe = "Wiederholung : "+ausgabe;
                    firstTime = false;
                }
            }
        }
    }
    /**
     * Eingaberoutine für Integer-Werte<br>
     * Wird solange wiederholt, bis zulässige Eingabe erfolgt ist.<br>
     * @param ausgabe Ausgabe-Titel, wird um den Wertebereich und ":" erweitert.
     * @param min - Minmal einzugebender Wert.
     * @param max - Maximal einzugebender Wert
     * @param def - Default Wert
     */
    public static int inputInt(String ausgabe, int min, int max, int def) {
        String erg;
        boolean firstTime = true;
        Integer I;
        int i;
        ausgabe = ausgabe + " ("+min+" ... "+max+") ["+def+"] : ";
        while (true) {
            erg = input(ausgabe);
            if(erg == null || erg.trim().equals("")) {
                return def;
            }
            try {
                I = new Integer(erg);
                i = I.intValue();
                if ( (i >= min) && (i <= max) ) {
                    return i;
                } else {
                    println("Eingabe nicht im zulaessigen Bereich "+min+" ..."+max);
                    if (firstTime) {
                        ausgabe = "Wiederholung : "+ausgabe;
                        firstTime = false;
                    }
                }
            } catch (NumberFormatException n) {
                if (firstTime) {
                    ausgabe = "Wiederholung : "+ausgabe;
                    firstTime = false;
                }
            }
        }
    }

    /**
     * Eingaberoutine für Integer-Werte<br>
     * Wird solange wiederholt, bis zulässige Eingabe erfolgt ist.<br>
     * @param ausgabe Ausgabe-Titel, wird um den Wertebereich und ":" erweitert.
     * @param min Minimum-Wert
     * @param max Maximum-Wert
     */
    public static int inputInt(String ausgabe, int min, int max) {
        return inputInt(ausgabe, min, max, min);
    }

    /**
     * Eingaberoutine für Integer-Werte<br>
     * Wird solange wiederholt, bis zulässige Eingabe erfolgt ist.<br>
     * @param ausgabe String Ausgabe-Titel, wird um den Wertebereich und ":" erweitert.
     * @param min - Minmal einzugebender Wert.
     * @param max - Maximal einzugebender Wert
     */
    public static Integer inputInteger(String ausgabe, int min, int max) {
        while (true) {
            String erg = input(ausgabe);
            if(erg == null || erg.trim().equals("")) {
                return null;
            }
            try {
                Integer I = new Integer(erg);
                if ( (I.intValue() >= min) && (I.intValue() <= max) ) {
                    return I;
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException n) {
                println("INVALID INDEX - Please enter a number in ["+min+".."+max+"]");
            }
        }
    }


    /**
     * Eingabemethode, läßt nur J,j,y,Y,N,n zu, antwort true für Ja und false für Nein<br>
     * Bei ungültiger Eingabe wird gefragt, ob die Programmbearbeitung gestoppt werden soll.
     * wird dies verneint, wiederholt sich die Abfrage.<br>
     * @param ausgabe Führungstext vor der erwarteten Eingabe, wird automatisch ergänzt um " (J,j,y,Y,N,n) ? : "
     * @return boolean siehe oben.
     */
    public static boolean inputJaNein(String ausgabe) {
        String inp;
        String ja = ";J;j;Y;y;";
        String nein = ";N;n;";
        ausgabe = ausgabe + " (J,j,y,Y,N,n) ? : ";
        while (true) {
            try	{
                inp = input(ausgabe);
                if (ja.indexOf(inp) > 0) {
                    return true;
                } else {
                    if (nein.indexOf(inp) > 0) {
                        return false;
                    }
                }
                println("Eingabefehler : "+inp);
            }
            catch (Exception e) {
                println("Eingabefehler : "+e);
            }
        }
    }

    /**
     * Eingabemethode, läßt nur J,j,y,Y,N,n zu, antwort true für Ja und false für Nein. Keine Eingabe -> Default-Wert<br>
     * @param ausgabe Führungstext vor der erwarteten Eingabe, wird automatisch ergänzt um " (J,j,y,Y,N,n) ? : "
     * @return boolean siehe oben.
     */
    public static boolean inputJaNein(String ausgabe, boolean defaultValue) {
        String inp;
        String ja = ";J;j;Y;y;";
        String nein = ";N;n;";
        String defaultVal = defaultValue ? "y" : "n";
        ausgabe = ausgabe + " (J,j,y,Y,N,n) ["+ defaultVal+"] : ";
        while (true) {
            try	{
                inp = input(ausgabe);
                if (ja.indexOf(inp) > 0) {
                    return true;
                } else {
                    if (nein.indexOf(inp) > 0) {
                        return false;
                    }
                }
                return defaultValue;
            } catch (Exception e) {
                println("Eingabefehler : "+e);
            }
        }
    }

    /**
     * Eingaberoutine für ein unsichtbares Passwort
     * @param prompt Führungstext oberhalb der erwarteten Eingabe
     */
    public static String inputPassword(String prompt) {
        String s = "" ;
        try	{
            println(prompt);

            MaskingThread maskingthread = new MaskingThread();
            Thread thread = new Thread(maskingthread);
            thread.start();

            s = input(null);
            maskingthread.stopMasking();
        }
        catch (Exception e) {
            println("MRTool.Eingabe -> "+e);
        }
        return s;
    }


    /**
     *   Innere Klasse, die während der Eingabe die eingegebenen Zeichen in der Anzeige wieder löscht,
     *   um so eine Passwort-Eingabe zu ermöglichen
     */
    static class MaskingThread extends Thread {
       private boolean stop = false;

       public void run() {
          while(!stop) {
             try {
                sleep(15);
             }
             catch (InterruptedException iex) {
             }
             if (!stop) {
                 System.out.print("\r                                        \r");
             }
             System.out.flush();
          }
       }

       public void stopMasking() {
          this.stop = true;
       }
    }
    /**
     * Ausgabe auf den Bildschirm, ohne anschliessenden Zeilenumbruch
     */
    public static void print(String s) 	{
        if (isRunningInsideServer()) {
            getCurrentOutputBuffer().append(s);
        }
        else {
            System.out.print(s);
            System.out.flush();
        }
    }

    /**
     * Ausgabe auf den Bildschirm, mit anschliessendem Zeilenumbruch
     */
    public static void println(String s) 	{
        if (isRunningInsideServer()) {
            getCurrentOutputBuffer().append(s).append('\n');
        }
        else {
            System.out.println(s);
            System.out.flush();
        }
    }

    /**
     * Ausgabe (auf den Bildschirm) mit anschliessendem Zeilenumbruch
     */
    public static void println() 	{
        if (isRunningInsideServer()) {
            getCurrentOutputBuffer().append('\n');
        }
        else {
            System.out.println();
            System.out.flush();
        }
    }

    /**
     * Ausgabe eines Fehlertextes der Exception sowie des StackTraces
     */
    public static void printException(Exception e) {
        if (e == null) {
            return;
        }
        println(e.toString());
        e.printStackTrace();
    }

    // -------- File Ein- und Ausgabeoperationen ------------------------
    /**
     * Instanzieren eines DataInputStream
     */
    public static BufferedReader createOpenInputFile(String fileName) throws Exception {
        String str;
        BufferedReader finp = null;
        try {
            finp = new BufferedReader(new FileReader(fileName));
        } catch (Exception e) {
            str = "Fehler beim Datei-Oeffnen : "+e.toString();
            println(str);
            throw new Exception(str);
        }
        return finp;
    }

    /**
     * Lesen von einem DataInputStream
     */
    public static String readFile(BufferedReader finp) throws Exception {
        String strInput;
        try {
            strInput = finp.readLine();
        } catch (Exception e) {
            String str = "Fehler beim Datei-Lesen : "+e.toString();
            println(str);
            throw new Exception(str);
        }
        return strInput;
    }

    /**
     * Schließen des DataInputStream
     */
    public static void closeInputFile(BufferedReader finp) throws Exception {
        try {
            finp.close();
        } catch (Exception e) {
            String str = "Fehler beim Datei-Schliessen : "+e.toString();
            println(str);
            throw new Exception(str);
        }
    }

    /**
     * Instantiieren eines PrintWriters
     */
    public static PrintWriter openOutputFile(String fileName) throws Exception {
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileWriter(fileName));
        } catch (IOException e) {
            throw new Exception("Ausgabedatei "+fileName+" kann nicht geöffnet werden : "+e.toString());
        }
        return printWriter;
    }
    /**
     * Instantiieren eines PrintWriters über File
     */
    public static PrintWriter openOutputFile(File aFile) throws Exception {
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileWriter(aFile));
        } catch (IOException e) {
            throw new Exception("Ausgabedatei "+aFile.getPath()+" kann nicht geöffnet werden : "+e.toString());
        }
        return printWriter;
    }

    /**
     * Instantiieren eines PrintWriters über File
     */
    public static PrintWriter openOutputFile(String aFileName, boolean append) throws Exception {
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(new FileWriter(aFileName, append));
        } catch (IOException e) {
            throw new Exception("Ausgabedatei "+aFileName+" kann nicht geöffnet werden : "+e.toString());
        }
        return printWriter;
    }

    public static boolean isInfoMode() {
        return infoMode;
    }

    public static void setInfoMode(boolean infoMode) {
        IOTools.infoMode = infoMode;
    }

    public static boolean isRunningInsideServer() {
        return isRunningInsideServer;
    }

    public static void setRunningInsideServer(boolean runningInsideServer) {
        isRunningInsideServer = runningInsideServer;
    }

    public static void closeOutputFile(PrintWriter printWriter) {
        if (printWriter!=null) {
            printWriter.close();
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return formatter.format(date);
    }

    /**
     * Liefert Datum und Zeit als String im Format jjjjmmtt_hh_mm
     */
    public static String getTimeStamp() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH_mm");
        return formatter.format(new Date());
    }


    /**
     * Trägt die ID in die "Interrupted-Tabelle" ein (nur für server-interne Ausführung).
     * Nachfolgende Zugriffe auf den Buffer führen zu einer "IllegalStateException".
     * @param sessionId  ID des Tools, das unterbrochen werden soll.
     */
    public static void interrupt(String sessionId) {
        interrupted.add(sessionId);
    }

    /**
     * Löscht alle Ein- und Ausgabe-Puffer für die übergebene ID
     * @param sessionId       ID des Tools, dessen Puffer gelöscht werden sollen
     * @param clearInterrupt  gibt an, ob auch das Interrupted-Flag gelöscht werden soll
     */
    public static void clearBuffers(String sessionId, boolean clearInterrupt) {
        inputBuffers.remove(sessionId);
        outputBuffers.remove(sessionId);
        if (clearInterrupt) {
            interrupted.remove(sessionId);
        }
    }

    /**
     * Löscht alle Ein- und Ausgabe-Puffer für die übergebene ID
     * @param sessionId  ID des Tools, dessen Daten ausgetauscht werden sollen
     * @param userInput  (neue) Benutzereingaben
     * @return (neue) Ausgaben seit der letzten Abfrage
     */
    public static String exchangeData(String sessionId, String userInput) {
        if (userInput!=null) {
            getBuffer(inputBuffers,sessionId).append(userInput).append((char)0);
        }
        StringBuilder buffer = getBuffer(outputBuffers,sessionId);
        String content = buffer.toString();
        buffer.delete(0, buffer.length());
        return content;
    }

    /**
     * Liefert den Eingabe-Puffer, der vom aktuell laufenden Tool verwendet wird
     */
    private static StringBuilder getCurrentInputBuffer() {
        return getCurrentBuffer(inputBuffers);
    }

    /**
     * Liefert den Ausgabe-Puffer, der vom aktuell laufenden Tool verwendet wird
     */
    private static StringBuilder getCurrentOutputBuffer() {
        return getCurrentBuffer(outputBuffers);
    }

    /**
     * Liefert den Ein- oder Ausgabe-Puffer, der vom aktuell laufenden Tool verwendet wird,
     * je nach dem, welche Map gerade übergeben wird
     */
    private static StringBuilder getCurrentBuffer(Map bufferMap) {
        String sessionId = "sessionId";
        return getBuffer(bufferMap,sessionId);
    }

    /**
     * Liefert den Ein- oder Ausgabe-Puffer, der vom angegebenen Tool verwendet wird,
     * je nach dem, welche Map gerade übergeben wird. 
     *
     * @param sessionId  ID des Tools, dessen Daten ausgetauscht werden sollen
     */
    private static StringBuilder getBuffer(Map bufferMap, String sessionId) {
        if (interrupted.contains(sessionId)) {
            throw new IllegalStateException("MR-Tool-Session '"+sessionId+"' has been interrupted.");
        }
        StringBuilder buffer = (StringBuilder) bufferMap.get(sessionId);
        if (buffer == null) {
            buffer = new StringBuilder();
            bufferMap.put(sessionId,buffer);
        }
        // in jedem Fall den alten Puffer zurückgeben !
        return buffer;
    }

}
