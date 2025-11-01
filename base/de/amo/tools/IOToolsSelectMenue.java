package de.amo.tools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
Klasse f�r Selekt-Programmierungen in MR-Tools<br>
Die einzelnen anzuzeigenden und auszuf�hrenden Selekt-Items werden in der Klasse
IOToolsSelectItem beschrieben. IOToolsSelectItem's werden vorteilhaft alse 
innere Klassen des MRTool-Programms implementiert.<br>
<br>
Beispielcode:<br>
<br>
public class RepairEzt extends de.logas.tools.MRTools {<br>
<br>
    IOToolsSelectMenue hauptMenue;<br>
<br>
    public void doIt() {<br>
    	hauptMenue = new IOToolsSelectMenue("Reparatur von EZT-Daten"); <br>
    	hauptMenue.addSelectItem(new SelectItemInputBesondereMasseinheit());<br>
    	hauptMenue.addSelectItem(new SelectItemShowZollCodeDaten());<br>
    	hauptMenue.show();<br>
    }<br>
<br>
    public static void main(String[] args) {<br>
        RepairEzt mr = new RepairEzt();<br>
        mr.doIt();<br>
    }<br>
<br>
    // ********************************************************************************<br>
    class SelectItemInputBesondereMasseinheit extends IOToolsSelectItem {<br>
<br>    	
    	SelectItemInputBesondereMasseinheit() {    		<br>
    		setAnzeigeText("Erfassen einer besonderen stat. Masseinheit");<br>
    	}<br>
<br>    	
    	public boolean doIt() {<br>
    		println("Aktion "+getAnzeigeText()+" startet:");<br>
    		try {<br>
	    		......<br>
    		}<br>
    		return true;<br>
    	}<br>
    }    <br>
<br>
    // ********************************************************************************<br>
    class SelectItemShowZollCodeDaten extends IOToolsSelectItem {<br>
<br>
    	SelectItemShowZollCodeDaten() {    		<br>
    		setAnzeigeText("Anzeige aller Daten eines HSD-Codes");<br>
    	}<br>
    	<br>
    	public boolean doIt() {<br>
    		println("Aktion "+getAnzeigeText()+" noch nicht implementiert.");<br>
    		return true;<br>
    	}<br>
    }<br>
    // ********************************************************************************<br>
}<br>
*/
public class IOToolsSelectMenue {
    
    String title;
    private List items;
    private IOToolsSelectItem itemFor_0_selection;
    private int displayStart = 0;
    private int displayEnd   = Integer.MAX_VALUE;
    private static char underLineChar = '-';
    private static char[] underLineChars = new char[80];
	public static String underLine;
	
	static {
	    Arrays.fill(underLineChars, underLineChar);
	    underLine = new String(underLineChars);
	}

    public IOToolsSelectMenue(String title) {
        this.title = title;
        items = new ArrayList();
    }

    public void setTitle(String s) {
    	title = s;
    }

    public String getTitle() {
        return title;
    }

    public void show() throws Exception {
        setDisplayRange(getDisplayStart(), getDisplayEnd());
        while (true) {
            String titleTmp = title;
            IOTools.println("\n" + titleTmp);
            String underline = underLine;
            if (titleTmp.length() < underline.length()) {
                underline = underLine.substring(0, titleTmp.length());
            }
            IOTools.println(underline);
            for (int i = 0; i < items.size(); i++) {
                if (i < displayStart || i > displayEnd) {
                    continue;
                }
                IOToolsSelectItem item = getSelectItem(i);
                if (!isAllowed(item)) {
                    continue;
                }
                String anzeigeText = item.getAnzeigeText();
                IOTools.println(""+(i + 1) + ") " + anzeigeText);
            }
            int auswahl = IOTools.inputInt(" Auswahl (0 fuer Ende des Menues) : ", 0, items.size());

            if (auswahl == 0) {
                if (itemFor_0_selection == null) {
                    return;
                } else {
                    if (!itemFor_0_selection.doIt()) {
                        return;
                    }
                }
            } else {
                IOToolsSelectItem item = getSelectItem(auswahl - 1);
                if (!item.doIt()) {
                    return;
                }
            }
        }
    }

    private boolean isAllowed(IOToolsSelectItem item) {
        return true;
    }

    private IOToolsSelectItem getSelectItem(int index) {
        return (IOToolsSelectItem)items.get(index);
    }

    public void addSelectItemFor0Selection(IOToolsSelectItem item) {
        itemFor_0_selection = item;
    }



    public void addSelectItem(IOToolsSelectItem item) {
    	items.add(item);
        item.setMenuNr(items.size());
    }

    public IOToolsSelectItem[] getSelectItems() {
        return (IOToolsSelectItem[]) items.toArray(new IOToolsSelectItem[0]);
    }

    public void setDisplayRange(int start, int end) {
        displayStart = start;
        displayEnd   = end;
        if (displayEnd >= items.size()) {
            displayEnd = items.size();
        }
    }

    public int getDisplayStart() {
        return displayStart;
    }

    public int getDisplayEnd() {
        return displayEnd;
    }
}