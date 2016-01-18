package de.amo.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Diese Klasse beinhaltet statische Methoden zum Datei-Handling.<br>
 *
 * @author  Martin de Vries, Logas GmbH Hamburg
 * @author  Reinhard Rust, Logas GmbH Hamburg
 *
 * create  01.03.1999
 * change  07.05.2001  Umstellung von DOS-Befehlen auf Pure-Java-Code
 *
 * @version 2.0
 */
public class FileHandler {


    /** Gr��e des internen Kopier-Puffers */
    private final static int COPY_BUFFER_SIZE = 64 * 1024;  // 64 KByte

    private static ParameterString errorMsg = new ParameterString("%0Fehler beim %1 der Datei '%2' nach '%3': %4");

    /**
     *  Pr�ft den Zugriff auf die beiden �bergebenen Dateien: Die erste Datei muss
     *  existieren und lesbar sein, die zweite Datei braucht nicht zu existieren, muss
     *  aber schreibbar sein.<br>
     *  <br>
     *  Diese Methode wird von copyTo() und moveTo() verwendet.
     *
     *  @param sourceFile  Quelldatei
     *  @param targetFile  Zieldatei
     *  @param errorMsg    aktuelle Fehlermeldung
     */
    private static void checkReadWriteAccess(File sourceFile, File targetFile, ParameterString errorMsg) {

        if (!sourceFile.exists()) {
            errorMsg.setParameter(0,"Lese-");
            errorMsg.setParameter(4,"Quell-Datei existiert nicht.");
            Assert.assertRtExc(FileHandler.class,"checkReadWriteAccess() "+errorMsg.toString());
        }
        
        if (!sourceFile.canRead()) {
            errorMsg.setParameter(0,"Lese-");
            errorMsg.setParameter(4,"Quell-Datei kann nicht gelesen werden.");
            Assert.assertRtExc(FileHandler.class,"checkReadWriteAccess() "+errorMsg.toString());
        }
        
        if (targetFile.exists() && !targetFile.canWrite()) {
            errorMsg.setParameter(0,"Schreib-");
            errorMsg.setParameter(4,"Ziel-Datei kann nicht geschrieben werden.");
            Assert.assertRtExc(FileHandler.class,"checkReadWriteAccess() "+errorMsg.toString());
        }
    }
        
    /** 
     *  Kopiert die Datei vom Quellort zum angegebenen Zielort: Die erste Datei muss
     *  existieren und lesbar sein, die zweite Datei braucht nicht zu existieren, muss
     *  aber schreibbar sein.<br>
     *  Fehlermeldungen fliegen in Form von LogasRuntimeExceptions
     *  @param sourceFile  Quelldatei
     *  @param targetFile  Zieldatei
     */
	public static boolean copyTo(File sourceFile, File targetFile)  {
        return copyTo(sourceFile,targetFile,null);
    }

    /**
     *  Kopiert die Datei vom Quellort zum angegebenen Zielort: Die erste Datei muss
     *  existieren und lesbar sein, die zweite Datei braucht nicht zu existieren, muss
     *  aber schreibbar sein.<br>
     *  Fehlermeldungen fliegen in Form von LogasRuntimeExceptions
     *  @param sourceFile  Quelldatei
     *  @param targetFile  Zieldatei
     */
	public static boolean copyTo(File sourceFile, File targetFile, PropertyChangeListener changeListener)  {
        boolean inStreamIsOpen  = false;
        boolean outStreamIsOpen = false;
        InputStream  inStream   = null;
        OutputStream outStream  = null;

        try {
            errorMsg.setParameter(1,"Kopieren");
            errorMsg.setParameter(2,sourceFile.getAbsolutePath());
            errorMsg.setParameter(3,targetFile.getAbsolutePath());

            checkReadWriteAccess(sourceFile,targetFile,errorMsg);

            long fileSize = Math.max(sourceFile.length(),1); // wenn 0 dann 1
            byte[] buffer = new byte[COPY_BUFFER_SIZE];
            long totalBytedRead = 0;
            int  bytesRead = 0;

            try {
                inStream       = getFileInputStream(sourceFile);
                inStreamIsOpen = true;
            } catch (IOException ioe) {
                errorMsg.setParameter(4,"Oeffnen der Quelldatei fehlgeschlagen: "+ioe);
                Assert.assertRtExc(FileHandler.class,errorMsg.toString(), ioe);
            }

            try {
                File targetDir = targetFile.getParentFile();
                targetDir.mkdirs();
                if (!targetDir.exists()) {
                    throw new FileNotFoundException("Verzeichnis '"+targetDir.getAbsolutePath()+"' konnte nicht gefunden oder angelegt werden.");
                }
                outStream       = new FileOutputStream(targetFile);
                outStreamIsOpen = true;
            }
            catch (FileNotFoundException fnfe) {
                errorMsg.setParameter(4,"Oeffnen der Zieldatei fehlgeschlagen: "+fnfe);
                Assert.assertRtExc(FileHandler.class,errorMsg.toString(), fnfe);
            }

            do {
                // Datenblock lesen
                try {
                    bytesRead = inStream.read(buffer,0,COPY_BUFFER_SIZE);
                }
                catch (Exception exc) {
                    errorMsg.setParameter(4,"Lesen der Quelldatei fehlgeschlagen: "+exc);
                    Assert.assertRtExc(FileHandler.class,errorMsg.toString(), exc);
                }
                // EOF erreicht ?
                if (bytesRead < 0) {
                    break;
                }
                // Datenblock schreiben
                try {
                    outStream.write(buffer,0,bytesRead);
                }
                catch (Exception exc) {
                    errorMsg.setParameter(4,"Schreiben der Zieldatei fehlgeschlagen: "+exc);
                    Assert.assertRtExc(FileHandler.class,errorMsg.toString(), exc);
                }
                // ggf. Listener benachrichtigen
                if (changeListener!=null) {
                    Long oldProgress = new Long(totalBytedRead*100L/fileSize);
                    Long newProgress = new Long((totalBytedRead+bytesRead)*100L/fileSize);
                    changeListener.propertyChange(new PropertyChangeEvent(FileHandler.class,"copy.progress",oldProgress,newProgress));
                }
                totalBytedRead += bytesRead;
            }
            while (bytesRead > 0);

            targetFile.setLastModified(sourceFile.lastModified());

            try {
                inStream.close();
                inStreamIsOpen = false;
            }
            catch (IOException e) {
                errorMsg.setParameter(4," Schliessen der Quelldatei fehlgeschlagen: "+e);
                Assert.assertRtExc(FileHandler.class,errorMsg.toString(), e);
            }
            try {
                outStream.close();
                outStreamIsOpen = false;
            }
            catch (IOException e) {
                errorMsg.setParameter(4," Schliessen der Zielldatei fehlgeschlagen: "+e);
                Assert.assertRtExc(FileHandler.class,errorMsg.toString(), e);
            }
        }
        finally {
            // ggf. Dateien schlie�en, falls irgend etwas schief gegangen ist.
            // Dabei keine weiteren Exceptions werfen, da sonst die EIGENTLICHE Exception weg ist
            if (inStreamIsOpen) {
                try {
                    inStream.close();
                }
                catch (IOException e) {
                    // keine weitere Exception
                }
            }
            if (outStreamIsOpen) {
                try {
                    outStream.close();
                }
                catch (IOException e) {
                    // keine weitere Exception
                }
            }
        }
        return true;
    }

    public static InputStream getFileInputStream(File file) throws IOException {
        return ZipArchiver.getFileInputStream(file.getAbsolutePath());
    }


    /**
     *  siehe andere moveTo()-Methode
     */
	public static void moveTo(String sourceFileName, String targetFileName) {
        File sourceFile = new File(sourceFileName);
        File targetFile = new File(targetFileName);
        moveTo(sourceFile,targetFile);
    }
        
    /** 
     *  Verschiebt die Datei vom Quellort zum angegebenen Zielort: Die erste Datei muss
     *  existieren und lesbar sein, die zweite Datei darf nicht zu existieren.<br>
     *  <br>
     *
     *  @param sourceFile  Quelldatei
     *  @param targetFile  Zieldatei
     */
	public static void moveTo(File sourceFile, File targetFile) {
        errorMsg.setParameters("","Verschieben",sourceFile.getAbsolutePath(),targetFile.getAbsolutePath());

        // Gleicher Pfad -> nichts tun
        if (sourceFile.getAbsolutePath().equalsIgnoreCase(targetFile.getAbsolutePath())) {
            return;
        }
        checkReadWriteAccess(sourceFile,targetFile,errorMsg);
        
        boolean moveOk = moveOk = sourceFile.renameTo(targetFile);
        if (!moveOk) {
            moveToByCopyAndDelete(sourceFile,targetFile);
        }
    }

    /**
     *  Kopiert die Datei vom Quellort zum angegebenen Zielort und l�scht die Quelle.
     *  Fies ist eine Ausweich-Methode, falls das "einfache" moveTo() nicht ausreicht (da nur renamed wird),
     *  was z.B. bei verschiedenen Laufwerken der Fall ist.
     *  <br>
     *
     *  @param sourceFile  Quelldatei
     *  @param targetFile  Zieldatei
     */
    private static void moveToByCopyAndDelete(File sourceFile, File targetFile) {
        errorMsg.setParameters("","Verschieben",sourceFile.getAbsolutePath(),targetFile.getAbsolutePath());

        boolean moveOk = false;
        boolean copyOk = false;
        try {
            // erst kopieren ...
            copyTo(sourceFile,targetFile);
            copyOk = true;
            // ... dann l�schen
            delete(sourceFile);
            moveOk = true;
        }
        catch(Exception exc) {
            moveOk = false;
            errorMsg.setParameter(4,exc);
            if (copyOk) {
                // Fehler ist aufgetreten, aber das File ist bereits kopiert worden -> Kopie wieder l�schen
                try {
                    delete(targetFile);
                }
                catch(Exception delExc) {
                    errorMsg.setParameter(4,delExc);
                }
            }
        }
        if (!moveOk) {
            errorMsg.setParameter(0,"Unbekannter ");
            Assert.assertRtExc(FileHandler.class, errorMsg.toString());
        }
    }

    /**
     *  siehe andere delete-Methode
     */
    public static void delete(String fileName)
    throws Exception {
        FileHandler.delete(new File(fileName));
    }

    /**
     *  L�scht die Datei am Quellort. Die Datei muss vorhanden und schreibbar sein.
     *
     *  @param file  Name der Quelldatei
     */
	public static void delete(File file)
    throws Exception {
        errorMsg.setParameters("","L�schen",file.getAbsolutePath());

        if (!file.exists()) {
            errorMsg.setParameter(4,"Datei existiert nicht.");
            Assert.assertRtExc(FileHandler.class,"checkWriteAccess() "+errorMsg.toString());
        }
        if (!file.canWrite()) {
            errorMsg.setParameter(4,"Keine Schreib-Berechtigung auf die Datei.");
            Assert.assertRtExc(FileHandler.class,"checkWriteAccess() "+errorMsg.toString());
        }

        boolean deleteOk = false;
	    try {
            deleteOk = file.delete();
            deleteOk = deleteOk && !file.exists();
        }
        catch(Throwable exc) {
            errorMsg.setParameter(0,"Unbekannter ");
            errorMsg.setParameter(4,exc.getMessage());
            throw new Exception(errorMsg.toString());
        }

        if (!deleteOk) {
            errorMsg.setParameter(0,"Unbekannter ");
            errorMsg.setParameter(4,"Datei konnte nicht gel�scht werden.");
            throw new Exception(errorMsg.toString());
        }
    }

    //
    //
    private static boolean isfound = false; //ist true, wenn gesuchte Datei oder Verzeichnis gefunden wurde
    private static String found = null; //enthaelt die gefundene Datei oder das gefundene Verzeichnis

    /* loescht das angegebene Verzeichnis
     * path entspricht dem zu loeschenden Verzeichnis und ist ohne abschlie�endes "\\" anzugeben.
     * withfiles bedeutet das alle Dateien in dem Verzeichnis geloescht werden
     * withsubdirs bedeutet das alle Unterverzeichnisse im Angegebenen Verzeichnis geloescht werden.
     */
    public static void deleteDirectory(String path, boolean withfiles, boolean withsubdirs)
    throws Exception {
        if (path==null) {
            return;
        }
        if(withfiles) {
            //loescht alle Dateien in path
            FileHandler.deleteFiles(path);
        }
        
        if(withsubdirs) {
            //loescht alle Unterverzeichnisse in path
            FileHandler.deleteSubDirs(path);
        }
        
        if(!withsubdirs) {
            File soondead = new File(path);
            //loescht das aktuelle Verzeichnis mit dem die Methode aufgerufen wird.
            //Dies ist genau das Verzeichnis mit dem gestartet wurde, da
            //withsubdirs == false => keine Rekursion.
            if (!soondead.exists()) {
                Assert.assertRtExc(soondead,"Path doesn't exist!");
            }
            soondead.delete();
            if(soondead.isDirectory()) {
                throw new Exception("Directory wurde nicht gel�scht");
            }
        }
        
        File soondead = new File(path);
        //loescht das aktuelle Verzeichnis mit dem die Methode aufgerufen wird.
        //Da die Methode rekursiv angewendet wird, ist dies nicht unbedingt das
        //Verzeichnis, mit dem gestartet wurde.
        if (!soondead.exists()) {
            Assert.assertRtExc(FileHandler.class, soondead + " Path doesn't exist!");
        }
        soondead.delete();
    }
    
    /* loescht alle Unterverzeichnisse eines gegebenen Verzeichnisses 
     * path ist das Verzeichnis, ab dem alles geloescht wird.
     */
    public static void deleteSubDirs(String path)
    throws Exception {
        if (path==null) {
            return;
        }
        //holt sich eine Liste der Unterverzeichnisse
        String[] dirlist = FileHandler.getSubDirectoryList(path);
        
        if(dirlist != null) {
            for(int i = 0; i < dirlist.length; i++) {
                //wenn das Verzeichnis weitere Unterverzeichnisse hat, werden diese
                //inklusive darin befindlicher Dateien und weiterer Unterverzeichnisse geloescht.
                FileHandler.deleteDirectory(dirlist[i], true, true);
            }
        }
        if(dirlist == null) {
            File soondead = new File(path);
            //Wenn keine weiteren Unterverzeichnisse vorhanden sind wird
            //das aktuelle Verzeichnis geloescht
            if (!soondead.exists()) {
                Assert.assertRtExc(FileHandler.class, soondead + " Path doesn't exist!");
            }
            soondead.delete();
            if(soondead.isDirectory()) {
                throw new Exception("Directory wurde nicht gel�scht");
            }
        }
    }
    
    /** loescht alle Dateien in einem Gegebenen Verzeichnis jedoch nicht eventuell
     * weitere vorhandene Verzeichnisse.
     * path ist das Mutter/Vater Verzeichnis der Dateien die zu loeschen sind
     */
    public static void deleteFiles(String path) throws Exception {
        //holt sich ein Array aller Dateien im aktuellen Pfad.
        String[] filelist = FileHandler.getFileList(path);
        if (filelist==null) {
            return;
        }
        //Ausfuehren des Loesch-Vorgangs.
        for(int i = 0; i < filelist.length; i++) {
            if(filelist[i] == null) {
                Assert.assertRtExc(FileHandler.class, filelist[i] + " File must not be Null");
            }
            if(path == null) {
                Assert.assertRtExc(FileHandler.class, path + " Path must not be Null!");
            }
            File soondead = new File(path, filelist[i]);
            soondead.delete();
            if(soondead.isFile()) {
                throw new Exception("File wurde nicht gel�scht");
            }
        }
    }
    
    /* Liefert alle im Verzeichnis vorhandenen Unterverzeichnisse zurueck.
     * Versagt leider beim root Verzeichnis.
     * path entspricht dem Verzeichnis aus dem alle unterverzeichnisse geholt werden.
     * Liefert null wenn keine Unterverzeichnisse vorhanden sind.
     */
    public static String[] getSubDirectoryList(String path) throws Exception {
        String filelist[] = null; //eine Liste aller Zeilen des "dir" Befehls
        String subdirlist[] = null; //eine Liste aller Unterverzeichnisse
        File testdir = null; //testdir dient der Pruefung auf File oder Directory
        Vector dirlist = new Vector(); //variabler Container 
                                       //fuer eine unbekannte Anzahl von Verzeichnissen 
        filelist = getDirList(path);
        if (filelist==null) {
            return null;
        }
        for(int i = 0; i < filelist.length; i++) {
            testdir = new File(path, filelist[i]);
            if(testdir != null && testdir.isDirectory()) {
                dirlist.addElement(path + File.separator + filelist[i]);
            }
        }
        if(dirlist != null) {
            subdirlist = new String[dirlist.size()];
            dirlist.copyInto(subdirlist);
        }
	    return subdirlist;
    }
    
    /* Liefert alle im Verzeichnis vorhandenen Dateien zurueck.
     * Versagt leider beim root Verzeichnis.
     * Path entspricht dem Verzeichnis aus dem alle Dateien geholt werden
     * Liefert null wenn keine Dateien vorhanden sind.
     */
    public static String[] getFileList(String path) {
        String filelist[] = null; //eine Liste aller Zeilen des "dir" Befehls
        String returnfilelist[] = null; //Die Liste der zurueckzugebenden 
        File testfile = null; //testfile dient der Pruefung auf File oder Directory
        Vector list = new Vector(); //variabler Container 
                                    //fuer eine unbekannte Anzahl von Dateien.
        filelist = getDirList(path);
        if(path==null || filelist == null) {
            return null;
        }
        for(int i = 0; i < filelist.length; i++) {
            if(filelist[i] != null) {
                testfile = new File(path, filelist[i]);
                if(testfile.isFile()) {
                    list.addElement(filelist[i]);
                }
            }
        }
        returnfilelist = new String[list.size()];
        list.copyInto(returnfilelist);

	    return returnfilelist;
    }
        
    /* Sucht ein Verzeichnis in einem Unterverzeichnis path mit dem Namen dirname
     * und liefert den voll qualifizierten Verzeichnisnamen an den Aufrufer zurueck
     * wenn kein uebereinstimmendes Verzeichnis gefunden wurde, wird null zurueckgegeben.
     * path ist das Verzeichnis, in dem gesucht werden soll. Leider darf path kein root sein!
     * dirname ist der Name des zu suchenden Verzeichnisses. 
     */
    public static String findDirectoryInSubdir(String path, String dirname) throws Exception {
        if(!isfound) {
            //wenn das gesuchte Verzeichnis noch nicht gefunden wurde, wird
            //getDirectory gerufen die Methode speichert das eventuell gefundene
            //Verzeichnis in found
            FileHandler.found = FileHandler.getDirectory(path, dirname);
        }
        return found;
    }
    
    /* Sucht eine Datei in einem Unterverzeichnis path mit dem Namen filename
     * und liefert den voll qualifizierten Dateinamen an den Aufrufer zurueck
     * wenn keine uebereinstimmende Datei gefunden wurde, wird null zurueckgegeben.
     * path ist das Verzeichnis, in dem gesucht werden soll. Leider darf path kein root sein!
     * filename ist der Name der zu suchenden Datei. 
     */
    public static String findFileInSubdir(String path, String filename) throws Exception {
        if(!isfound) {
            //wenn die gesuchte Datei noch nicht gefunden wurde, wird
            //getFile gerufen. Die Methode speichert die eventuell gefundene
            //Datei in found
            FileHandler.found = FileHandler.getFile(path, filename);
        }
        return found;
    }
    
    /* Hilfsmethode fuer findDirectoryInSubdir.
     * Sucht ein Verzeichnis in einem Unterverzeichnis path mit dem Namen dirnamen
     * und liefert den voll qualifizierten Verzeichnisnamen an den Aufrufer zurueck
     */
    private static String getDirectory(String path, String dirname) throws Exception {
        //Holt eine Liste aller uUnterverzeichnisse des Verzeichnisses path 
        String[] subdirlist = FileHandler.getSubDirectoryList(path);
        if(subdirlist != null) {
            for(int i = 0; i < subdirlist.length && !isfound; i++) {
                if(subdirlist[i].equalsIgnoreCase(path + "\\" + dirname)) {
                    //Wenn subdirlist[i] dem dirnamen entspricht (beides als absolute Pfad-Namen)
                    //wird der voll qualifizierte Verzeichnisname in found gespeichert und isfound
                    //auf true gesetzt.
                    FileHandler.found = subdirlist[i];
                    FileHandler.isfound = true;
                    break;
                } else {
                    //Wenn subdirlist[i] nicht das gesuchte Verzeichnis ist,
                    //wird ein push zur n�chsten Rekursions-Ebene ausgef�hrt
                    FileHandler.findDirectoryInSubdir(subdirlist[i], dirname);
                }
            }
        }
        return FileHandler.found;
    }
    
    
    /* Hilfsmethode fuer findDirectoryInSubdir.
     * Sucht ein Verzeichnis in einem Unterverzeichnis path mit dem Namen dirnamen
     * und liefert den voll qualifizierten Verzeichnisnamen an den Aufrufer zurueck
     */
    private static String getFile(String path, String filename) throws Exception {
        //Liste aller Dateien in path.
        String[] filelist = FileHandler.getFileList(path);
        if(filelist != null && filename != null) {
            for(int i = 0; i < filelist.length; i++) {
                if(filelist[i] != null) {
                    if(filelist[i].equalsIgnoreCase(filename)) {
                        //Wenn die Datei vorhanden ist wird sie mit voll qualifiziertem
                        //Namen abgespeichert.
                        FileHandler.found = path + "\\" + filelist[i];
                        FileHandler.isfound = true;
                        break;
                    }
                }
            }
        }
        if(!isfound) {
            //Wenn die gesuchte Datei immer noch nicht gefunden wurde, wird
            //findFileInSubdir gerufen.
            //Dies entspricht einem push zur n�chsten Rekursions-Ebene.
            String[] subdirlist = FileHandler.getSubDirectoryList(path);
            for (int j = 0; j < subdirlist.length && !isfound; j++) {
                FileHandler.findFileInSubdir(subdirlist[j], filename);
            }
        }
        return FileHandler.found;
    }

    /**
     *  Liefert den Inhalt der Datei als Text. Falls diese nicht existiert, wird null geliefert.
     *  Es werden keine Exceptions geworfen.
     */
    public static String getFileContentAsString(File file, boolean addNewLine) {
        StringBuilder buffer = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while (reader.ready()) {
                String line = reader.readLine();
                buffer.append(line);
                if(addNewLine){
                   buffer.append('\n');
                }
            }
            reader.close();
            return buffer.toString();
        }
        catch (Throwable thr) {
            return null;
        }
    }

    public static String getFileContentAsString(File file) {
        return getFileContentAsString(file, true);
    }

    /**
     *  Schreibt den String in eine Datei. Falls es nicht funktioniert, wird false geliefert.
     *  Es werden keine Exceptions geworfen.
     */
    public static boolean writeFileContentAsString(File file, String s) {
        try {
            if (s==null) {
                s = "";
            }
            PrintWriter writer = new PrintWriter(new FileWriter(file));
            java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(s,"\n");
            while(tokenizer.hasMoreElements()) {
                writer.println(tokenizer.nextElement());
            }
            writer.close();
            return true;
        }
        catch (Throwable thr) {
            return false;
        }
    }

    public static String[] getDirList(String path) {
        return new File(path).list();
    }

    /**
     *  F�gt an den Namen des uebergebenen Files einen Timestamp an.<BR>
     *  Und zwar im Format "dateiname" + "-" + "dd.MM.yyyy + "-" + hh:mm:ss" + Datentyp 
     */
    public static File createFileWithTimeStamp(File _file) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy-HH.mm.ss");
        String dateFormatString = formatter.format(new Date());
        String oldFileName = _file.getName();
        StringBuilder newFileNameBuffer = new StringBuilder(oldFileName);
        int idxOfPoint = oldFileName.lastIndexOf(".");
        if (idxOfPoint < 0) {
            newFileNameBuffer.append("-").append(dateFormatString);
        } else {
            newFileNameBuffer = new StringBuilder();
            newFileNameBuffer.append(oldFileName.substring(0, idxOfPoint)).append("-").append(dateFormatString).append(oldFileName.substring(idxOfPoint));            
        }
        File fileWithTimeStamp = new File(_file.getParentFile(), newFileNameBuffer.toString());
        return fileWithTimeStamp;
    }

    /**
     * Entpackt eine Zip - Datei in ein bestimmtes Verzeichnis
     * @param prefix String der an den Anfang des Dateinamens der entpackten Datei gehaengt werden soll.
     * @param suffix String der an das Ende des Dateinamens der entpackten Datei gehaengt werden soll.
     * Wird als suffix "" uebergeben, entsteht eine Datei ohne Dateinamenerweiterung.
     * Wird als suffix null uebergeben wird die Original Dateinamenerweiterung der entpackten Datei verwendet.
     * @param zipFile Das zu entpackende ZIP - File.
     * @param destDir Zielverzeichnis fuer die entpackten Dateien.
     * @return Liste mit den den entpackten Dateien als File Objekte.
     */
    public static List unzip(String prefix, String suffix, ZipFile zipFile, File destDir){
        File unzippedFile;
        List unzipped = new Vector();

        Enumeration entries = zipFile.entries();
        try {
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String destFile = destDir.getAbsolutePath() + "/" + (prefix == null ? "" : prefix +  "_") + entry.getName() + (suffix == null ? "" : suffix);
                unzippedFile = new File(destFile);
                OutputStream out = null;
                if (!entry.isDirectory()) {
                    if (!unzippedFile.exists()) {
                        byte[] buffer = new byte[(int)entry.getSize()];
                        int len;

                        InputStream in = zipFile.getInputStream(entry);
                        out = new BufferedOutputStream(new FileOutputStream(unzippedFile));

                        while ((len = in.read(buffer)) >= 0){
                            out.write(buffer, 0, len);
                        }

                        in.close();
                        out.close();
                        unzipped.add(new File(destFile));
                    }
                    else{
                        Assert.assertRtExc(null, "Datei " + unzippedFile.getName() + " konnte nicht aus dem Archiv " + zipFile.getName() + " extrahiert werden, da in dem Verzeichnis " + destDir.getAbsolutePath() + " bereits eine Datei unter diesem Namen existiert.");
                    }
                }
            }
            zipFile.close();
        } catch (IOException e) {
            Assert.assertRtExc(null, "Fehler beim Lesen der Zip Datei " + zipFile.getName() , e);
        }
        return unzipped;
    }

    /**
     * Findet anhand des Dateidatums die aktuellste Datei
     * @param files die zu untersuchenden Dateien.
     * @return
     */
    public static File getNewestFile(File[] files){
        File file = null;
        int lastModified = 0;
        for (int i = 0; i < files.length; i++) {
            File fileTmp = files[i];
            if (fileTmp.exists()) {
                if(fileTmp.lastModified() > lastModified)
                    file = fileTmp;
            }
        }

        return file;
                }


    public static void writeInFile(String fileName, String toWriteInFile) {
        File file = new File(fileName);
        boolean fileNonExistant = true;
        if (file.exists())
            fileNonExistant = file.delete();

        try {
            file.createNewFile();
        } catch (IOException e) {
            Assert.assertRtExc(null, "Datei " + fileName + " konnte nicht angelegt werden.", e);
        }

        try {
            if (fileNonExistant) {
                PrintWriter fw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                fw.println(toWriteInFile);
                fw.close();
            } else {
                Assert.assertRtExc(null, "Datei besteht bereits kann aber nicht geloescht werden. ==> ABBRUCH");
            }

        } catch (IOException e) {
            Assert.assertRtExc(null, "Fehler beim Bearbeiten der Datei " + fileName, e);
        }
    }


    /*
     * Liest die ganze Datei in einen String oder
     * wenn die Gr��e der Datei readSize �bersteigt wird readSize vom Ende gerechnet gelesen.
     */
    public static String readTail(File file, long readSize){
        StringBuilder text= new StringBuilder();
        String tmp=null;
        RandomAccessFile raFile = null;
        try{
            raFile= new RandomAccessFile(file,"r");
            long size=raFile.length();
            if (size>readSize && readSize > 0) {
                raFile.seek(size-readSize);
                raFile.readLine();   // bis zuim Ende einer Zeile weiterr�cken.
                text.append("Total File Size(byte) = "+size+"\r\n");
                text.append("Read       Size(byte) = "+readSize+" ( size can be changed in ini-File) \r\n\n");
            }
            while((tmp=raFile.readLine())!=null){
              text.append(tmp);
              text.append("\n\r");
            }
        }
        catch(EOFException  e) {
        }
        catch(FileNotFoundException e) {
            text.append(e.getMessage());
        }
        catch(IOException e){
            text.append(e.getMessage());
        }
        finally {
            if (raFile!=null) {
                try {
                    raFile.close();
                }
                catch(Exception e) {
                    Assert.assertRtExc(FileHandler.class,"Fehler beim Schliessen der Daten <"+file.getAbsolutePath()+">", e);
                }
            }
        }
        return text.toString();
    }

    /**
     * L�scht alle Dateien ggf. rekursiv im angegebenen Verzeichnis, die �lter als X tage sind
     * @param directory  Verzeichnis, in dem gel�scht werden soll
     * @param recursive  Flag, ob auch alle Unterverzeichnisse gel�scht werden sollen
     * @param daysToKeep Maximal zul�ssiges Alter der Dateien in Tagen
     */
    public static void cleanupDirectory(File directory, boolean recursive, int daysToKeep) {
        long ONE_DAY = 1000 * 60 * 60 * 24;
        long now = System.currentTimeMillis();
        long timestampToKeep = now - (daysToKeep*ONE_DAY);
        File[] files = directory.listFiles();
        for(int i=0; files != null && i<files.length; i++) {
            // Verzeichnis gefunden:
            if (files[i].isDirectory() && recursive) {
                // wenn "rekursiv" angegeben wurde, dann auch absteigen
                cleanupDirectory(files[i],recursive,daysToKeep);
            }
            long fileTimestamp = files[i].lastModified();
            if (fileTimestamp < timestampToKeep) {
                try {
                    files[i].delete();    // ein Verzeichnis w�rde nur gel�scht, wenn es leer ist
                } catch(Throwable thr) {
                    // ignorieren !
                }
            }
        }
    }


    /**
     *  Liest den Inhalt des Eingangsstreams und schreibt ihn auf den Ausgangs-Stream
     */
    private final static int MAX_BUFFER = 8096;
    public static void transferStream(InputStream inStream, OutputStream outStream) throws IOException {
        byte[] buffer = new byte[MAX_BUFFER];
        int bytesRead = 0;
        do {
            bytesRead = inStream.read(buffer,0,MAX_BUFFER);
            if (bytesRead>0) {
                outStream.write(buffer,0,bytesRead);
            }
        }
        while (bytesRead>0);
    }


    static class Assert {
        public static void assertRtExc(Object o, String s) {
            Exception e = new Exception(o.getClass().getName() + " : " + s);
            e.printStackTrace();
            System.exit(0);
        }

        public static void assertRtExc(Object o, String s, Exception e) {
            System.out.println(o.getClass().getName() + " : " + s);
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static void deleteRecursive(File f) {

        if (f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    deleteRecursive(file);
                } else {
                    file.delete();
                }
            }
        }
        f.delete();
    }

    // ToDO: Unterverzeichnisse einbauen
    public static void copyDir(File sourceDir, File targetDir) {

        if (!sourceDir.exists()) {
            return;
        }

        if (!sourceDir.isDirectory()) {
            return;
        }
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        File[] files = sourceDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            copyTo(file, new File(targetDir, file.getName()));
        }
    }
}
