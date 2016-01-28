package de.amo.money;

import de.amo.tools.Datum;
import de.amo.view.AToolTipHeader;
import de.amo.view.AmoStyle;
import de.amo.view.cellrenderer.Integer2FloatCellRenderer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

/**
 * Created by private on 14.01.2016.
 */
public class Reportgenerator {

    private SortedSet<String> reportKategorienFein;
    private SortedSet<String> reportKategorienGrob;
    private SortedSet<String> reportMonate;
    private SortedSet<String> reportJahre;
    private SortedMap<String, Integer> jahresSumme;

    public Reportgenerator(List<Buchungszeile> buchungszeilen, JTabbedPane tabbedPane) {

        initReportKategorien(buchungszeilen);
        initReportMonate();
        berechneJahressummen(buchungszeilen);

        JPanel tablePanel = createTablePanel();
        tabbedPane.add("Auswertung", tablePanel);
    }

    private void berechneJahressummen(List<Buchungszeile> buchungszeilen) {

        String heuteMonat = Datum.heute().substring(0,6);

        jahresSumme = new TreeMap<String, Integer>();

        for (Buchungszeile buchungszeile : buchungszeilen) {

            // der heutige (angebrochene) Monat wird nicht betrachtet
            if (buchungszeile.datum.startsWith(heuteMonat)) {
                continue;
            }

            String kat = buchungszeile.kategorie;
            if (kat == null) {
                kat = "";
            }
            kat = kat + "  ";

            String jahr = buchungszeile.datum.substring(0,4);
            String kat1 = jahr + kat.substring(0, 2);
            String kat2 = jahr + kat.substring(0, 1);

            Integer int1 = jahresSumme.get(kat1);
            Integer int2 = jahresSumme.get(kat2);

            if (int1 == null) {
                int1 = new Integer(0);
            }
            if (int2 == null) {
                int2 = new Integer(0);
            }

            int1 += buchungszeile.betrag;
            int2 += buchungszeile.betrag;
            jahresSumme.put(kat1, int1);
            jahresSumme.put(kat2, int2);
        }
    }


    private void initReportMonate() {
        reportMonate = new TreeSet();
        reportJahre  = new TreeSet();
        String heute = Datum.heute();
        String endjahr = heute.substring(0, 4);
        int endYear = Integer.parseInt(endjahr);

        for (int i = 2013; i <= endYear; i++) {
            for (int monat = 1; monat <= 12; monat++) {
                String reportMonat = "0" + monat;
                reportMonat = "" + i + reportMonat.substring(reportMonat.length() - 2);
                reportMonate.add(reportMonat);
            }
            reportJahre.add("" + i);
        }
    }

    private void initReportKategorien(List<Buchungszeile> buchungszeilen) {
        reportKategorienFein = new TreeSet();
        reportKategorienGrob = new TreeSet();
        for (Buchungszeile buchungszeile : buchungszeilen) {
            String kat = buchungszeile.kategorie;
            if (kat == null) {
                kat = "";
            }
            kat = kat + "  ";
            reportKategorienFein.add(kat.substring(0, 2));
            reportKategorienGrob.add(kat.substring(0, 1));
        }
    }



    public JPanel createTablePanel() {
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel,BoxLayout.Y_AXIS));

        createTabelle(tablePanel, reportKategorienGrob, "Monatliche Umsätze pro Hauptkategorie");

        createTabelle(tablePanel, reportKategorienFein, "Monatliche Umsätze pro Kategorie");

        return tablePanel;
    }

    private void createTabelle(JPanel tablePanel, SortedSet<String> reportKategorien, String title) {
        ReportsaetzeTableModel model = new ReportsaetzeTableModel();
        JScrollPane comp = new JScrollPane(createTable(model, reportKategorien));
        tablePanel.add(comp);
        comp.setBorder(new TitledBorder(title));

        BigDecimal monateImJahr = new BigDecimal(12);
        String heute = Datum.heute();
        String monatHeute = heute.substring(4,6);
        int monatHeuteI = Integer.parseInt(monatHeute);

        for (String  jahr : reportJahre) {

            if (heute.startsWith(jahr)) {
                // der heutige (angebrochene) Monat wird nicht betrachtet
                monateImJahr = new BigDecimal(monatHeuteI - 1);
            }

            int coloumn = 0;
            Object[] rowData = new Object[reportKategorien.size()+1];

            rowData[coloumn] = jahr;

            for (String kat : reportKategorien) {
                Integer value = jahresSumme.get(jahr + kat);
                coloumn++;
                if (value != null) {
                    if (monateImJahr.intValue() > 0) {
                        BigDecimal bd = new BigDecimal(value);
                        bd = bd.divide(monateImJahr, BigDecimal.ROUND_HALF_UP);
                        value = bd.intValue();
                    } else {
                        value = 0;
                    }
                }
                rowData[coloumn] = value;
            }

            model.addRow(rowData);
        }
    }

    private JTable createTable(DefaultTableModel model, SortedSet<String> reportKategorien) {

        model.addColumn("Jahr");

        for (String kat : reportKategorien) {
            model.addColumn(kat);
        }

        final Integer2FloatCellRenderer integerCellRenderer = new Integer2FloatCellRenderer();

        final JTable table = new JTable(model) {

            public boolean isCellEditable(int x, int y) {
                return false;
            }

            public TableCellRenderer getCellRenderer(int row, int column) {

                if (getModel().getColumnClass(column).equals(Integer.class)) {
                    return integerCellRenderer;
                }

                if (column == 0) {
                    return new DefaultTableCellRenderer() {
                        @Override
                        public int getHorizontalAlignment() {
                            return SwingConstants.CENTER;
                        }
                    };
                }
                // else...
                return super.getCellRenderer(row, column);
            }
        };

        // und jetzt noch Tooltips für die Spaltenköpfe:
        List <Kategorie> kategories = Kategoriefacade.get().getKategorien();
        Map<String, String> tooltipmap = new HashMap<>();
        for (Kategorie kategory : kategories) {
            tooltipmap.put(kategory.getCode(), kategory.getBeschreibung());
        }
        AToolTipHeader header = new AToolTipHeader(table.getColumnModel(), tooltipmap);
        //header.setToolTipText("Default ToolTip TEXT");
        table.setTableHeader(header);

        if (AmoStyle.isGuiTestMode()) {
            // der wirkt wirklich
            table.setBackground(Color.cyan);
        }
        table.setVisible(true);

        return table;

    }

    class ReportsaetzeTableModel extends DefaultTableModel {
        public Class getColumnClass(int c) {
            Object valueAt = getValueAt(0, c);
//            if (valueAt == null) {
//                return String.class;
//            }
            if (c == 0) {
                return String.class;
            } else {
                return Integer.class;
            }
//
//            Class aClass = valueAt.getClass();
//            return aClass;
        }
    }

}
