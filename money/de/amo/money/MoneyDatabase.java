package de.amo.money;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by private on 11.10.2015.
 */
public class MoneyDatabase {

    String kontodir;

    public MoneyDatabase(String kontodir) {
        this.kontodir = kontodir;
    }

    public String getKontodir() {
        return kontodir;
    }

    public File getBackupDir() {
        File bdir = new File(new File(kontodir),"backup");
        if (!bdir.exists()) {
            bdir.mkdir();
        }
        return bdir;
    }

    public File getArchivDir() {
        File adir = new File(new File(kontodir),"archiv");
        if (!adir.exists()) {
            adir.mkdir();
        }
        return adir;
    }

    public File getDatabaseFile() {
        File f = new File(kontodir);
        f = new File(f, "database.csv");
        return f;
    }




    public String umsatzDateienEinlesen(MoneyTransient moneyTr) {

        int datensaetzeVorher = moneyTr.getBuchungszeilen().size();

        List<File> verarbeiteteFiles = new ArrayList<>();

        File dir = moneyTr.umsatzdateienDownloadDir;
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String name = file.getName().toLowerCase();
            if (name.startsWith("umsatzanzeige") && file.getName().endsWith(".csv")) {


                if (datensaetzeVorher == 0 && verarbeiteteFiles.size() > 0) {
                    throw new RuntimeException("Beim Einlesen der ersten Buchungssätze einer Datenbank darf nur eine Datei vorgelegt werden.");
                }

                System.out.println("  Lese Datei " + file.getName());
                try {
                    // die letzte in der Datei übermittelte Zeile ist in der Buchungsreihenfolge die Erste
                    Buchungszeile buchungszeileFirst = Buchungszeile.readIngDibaFile(file, moneyTr.getBuchungszeilen());

                    if (datensaetzeVorher == 0) {
                        buchungszeileFirst.isAllerersterSatz = true;
                    }
                    verarbeiteteFiles.add(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            }
        }
        moneyTr.recalculate();

        // Erst jetzt addieren, es können Exceptions geflogen sein wegen Lücken in den Dateien:
        moneyTr.getEingelesesUmsatzDateien().addAll(verarbeiteteFiles);

        int datensaetzeHinterher = moneyTr.getBuchungszeilen().size();

        if (datensaetzeVorher != datensaetzeHinterher) {
            moneyTr.setIsSaved(false);
        }

        return "Bank-Dateien eingelesen.";
    }


    public String loadDatabase(MoneyTransient moneyTr) {

        File f = getDatabaseFile();
        moneyTr.getBuchungszeilen().clear();

        if (f.exists()) {
            try {
                Buchungszeile.readDatabaseFile(f.getAbsolutePath(), moneyTr.getBuchungszeilen());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            moneyTr.getEingelesesUmsatzDateien().clear();
            moneyTr.recalculate();
            moneyTr.setIsSaved(true);
            moneyTr.setLastBackupDatabaseFile(null);
            return "Datenbank eingelesen ";
        } else {
            moneyTr.getEingelesesUmsatzDateien().clear();
            moneyTr.setIsSaved(true);
            moneyTr.setLastBackupDatabaseFile(null);
            return "Keine Datenbank eingelesen ";
        }
    }

    public String saveDatabase(MoneyTransient moneyTr) {

        File f = getDatabaseFile();

        if (f.exists()) {
            File backupDir = getBackupDir();
            File backupDatabase = new File(backupDir, f.getName() + "_" + new Date().getTime());
            boolean b = f.renameTo(backupDatabase);
            if (b) {
                moneyTr.setLastBackupDatabaseFile(backupDatabase);
            }
        }
        try {
            Buchungszeile.writeDatabaseFile(f.getAbsolutePath(), moneyTr.getAktuelleDaten());

            List<File> eingelesesUmsatzDateien = moneyTr.getEingelesesUmsatzDateien();
            for (File file : eingelesesUmsatzDateien) {
                File archivFile = new File(getArchivDir(),file.getName());
                if (archivFile.exists()) {
                    // ToDo: Gleichheit noch besser prüfen:
                    if (file.length() == archivFile.length()) {
                        archivFile.delete();    // Inkonsequent, wenn beide wirklich gleich wären ....
                    }
                }
                file.renameTo(archivFile);
            }
            eingelesesUmsatzDateien.clear();
        } catch (Exception e) {
            RuntimeException rte = new RuntimeException("Abbruch beim Sichern", e);
            throw rte;
        }
        moneyTr.setIsSaved(true);
        return "Gespeichert und Backup der Vorgängerversion erzeugt.";
    }



}
