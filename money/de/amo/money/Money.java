package de.amo.money;

import de.amo.tools.FileHandler;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by amo on 22.08.2015.
 */
public class Money {


    public static void main(String args[]) {
        if (args.length == 0) {
            File testDir = new File("C:\\Users\\private\\IdeaProjects\\Money\\test\\run");

            File testRessourceDir = new File("C:\\Users\\private\\IdeaProjects\\Money\\test\\");

            String kontoDir = testDir.getAbsolutePath();

            // das Run-Verzeichnis leeren
            File[] files = testDir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                file.deleteOnExit();
            }


            FileHandler.copyDir(new File(testRessourceDir, "initial03"), testDir);

            File database = new File("C:\\Users\\private\\IdeaProjects\\Money\\test\\database.csv");
            FileHandler.copyTo(database, new File(testDir, "database.csv"));

            Money.main2(new String[]{"kontodir=" + kontoDir});

        } else {

            main2(args);

        }

    }

    public static void main2(String args[]) {

        String kontodir = null;

        for (int i = 0; i < args.length; i++) {
            int pos = args[i].indexOf("=");
            if (pos < 1) {
                continue;
            }
            String key = args[i].substring(0, pos);

            if (key.toLowerCase().equals("kontodir") && args[i].length() > (pos + 1)) {
                kontodir = args[i].substring(pos + 1);
                File f = new File(kontodir);
                if (!f.exists()) {
                    System.out.println("<" + kontodir + "> ist kein gueltiges Verzeichnis.");
                    System.exit(0);
                }
                if (!f.isDirectory()) {
                    System.out.println("<" + kontodir + "> ist kein Verzeichnis.");
                    System.exit(0);
                }
            }
        }

        if (kontodir == null) {
            System.out.println("Parameter 'kontodir' ist anzugeben!");
            System.exit(0);
        }

        try {
            Kategoriefacade.init(kontodir);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        MoneyTransient  moneyTr             = new MoneyTransient();
        MoneyDatabase   moneyDatabase       = new MoneyDatabase (kontodir);
        MoneyController moneyController     = new MoneyController(moneyTr, moneyDatabase);
        MoneyView       moneyView           = new MoneyView(moneyController);

        moneyController.moneyView = moneyView;

        moneyDatabase.loadDatabase(moneyTr);

        moneyController.createForecast();

        moneyTr.recalculate();
        moneyView.updateGui();

    }


}
