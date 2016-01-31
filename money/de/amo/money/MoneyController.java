package de.amo.money;

import de.amo.tools.IOToolsSelectItem;
import de.amo.tools.IOToolsSelectMenue;
import de.amo.tools.StringFormatter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/** Der MoneyController vermittelt das transiente Datenmodell (MoneyTransient) zwischen der View (MoneyView) und der Datenbank (MoneyDatabase)
 *
 * Created by amo on 24.08.2015.
 */
public class MoneyController {

    MoneyTransient moneyTr;
    MoneyView moneyView;
    MoneyDatabase moneyDatabase;

    public String lastMessage = "no message";

    public MoneyTransient getMoneyTr() {
        return moneyTr;
    }

    public MoneyController(MoneyTransient moneyTransient, MoneyDatabase moneyDatabase) {
        this.moneyTr = moneyTransient;
        this.moneyDatabase = moneyDatabase;
    }

    public MoneyDatabase getMoneyDatabase() {
        return moneyDatabase;
    }

    public String getMessage() {
        return lastMessage;
    }

    public void createForecast() {
        List<Buchungszeile> forecast = moneyTr.getForecast();
        forecast.clear();
        int forecastSumme = 0;

        Buchungszeile b;
        b = new Buchungszeile();
        b.datum = "00000000";
        b.pbetrag = 0;
        b.quelleZiel = "Axel ING-Diba";
        b.verwendungszweck = "Gehalt dbh";
        forecast.add(b);
        forecastSumme += b.pbetrag;

        b = new Buchungszeile();
        b.datum = "00000000";
        b.pbetrag = -5685;
        b.quelleZiel = "MLP";
        b.verwendungszweck = "Versicherung gross";
        forecast.add(b);
        addBuchungszeileInMessagePanel(b);
        forecastSumme += b.pbetrag;

        b = new Buchungszeile();
        b.datum = "00000000";
        b.pbetrag = -24187;
        b.quelleZiel = "MLP";
        b.verwendungszweck = "Versicherung klein";
        forecast.add(b);
        addBuchungszeileInMessagePanel(b);
        forecastSumme += b.pbetrag;

        b = new Buchungszeile();
        b.datum = "00000000";
        b.pbetrag = -50300;
        b.quelleZiel = "Harry Schulz";
        b.verwendungszweck = "Miete";
        forecast.add(b);
        addBuchungszeileInMessagePanel(b);
        forecastSumme += b.pbetrag;

        b = new Buchungszeile();
        b.datum = "00000000";
        b.pbetrag = -2500;
        b.quelleZiel = "EON";
        b.verwendungszweck = "Strom";
        forecast.add(b);
        addBuchungszeileInMessagePanel(b);
        forecastSumme += b.pbetrag;

        moneyTr.setForecastSumme(forecastSumme);

        moneyView.addMessage("Forecast (Einbehalt für Monatsanfangszahlungen) : " + StringFormatter.getFloatStringFromIntString("" + forecastSumme));
    }

    private void addBuchungszeileInMessagePanel(Buchungszeile b) {
        String floatStringFromIntString = StringFormatter.getFloatStringFromIntString("" + b.pbetrag);
        while (floatStringFromIntString.length() < 10) {
            floatStringFromIntString = " " + floatStringFromIntString;
        }

        moneyView.addMessage(b.quelleZiel + "\t" + b.verwendungszweck + "\t" + floatStringFromIntString);
    }

    public boolean isSaved() {
        return moneyTr.isSaved();
    }

    public void umsatzDateienEinlesen() {
        lastMessage = moneyDatabase.umsatzDateienEinlesen(moneyTr);
        moneyView.updateGui();
    }

//    public void loadDatabase() {
//        lastMessage =  moneyDatabase.loadDatabase(moneyTr);
//        moneyView.updateGui();
//    }

    public void saveDatabase() {
        lastMessage = moneyDatabase.saveDatabase(moneyTr);
        moneyView.updateGui();
    }

    public void refreshView() {
        moneyView.updateGui();
    }

    public class ErzeugePexport extends IOToolsSelectItem {
        public ErzeugePexport(IOToolsSelectMenue menue) {
            super(menue);
            setAnzeigeText("P-Datenexport");
        }

        @Override
        public boolean doIt() throws Exception {
            File f = new File(moneyDatabase.getKontodir());
            f = new File(f, "Datenexport.csv");
            Buchungszeile.writePExportFile(f.getAbsolutePath(), moneyTr.getAktuelleDaten());
            return true;
        }
    }


    public void createSplittbuchungen(Buchungszeile parent, int betrag, String kategorie, String kommentar) {
        List<Buchungszeile> splittbuchungen = parent.createSplittbuchungen(betrag, kategorie, kommentar);
        getMoneyTr().addUmbuchungszeilen(splittbuchungen.get(0), splittbuchungen.get(1));
    }
}