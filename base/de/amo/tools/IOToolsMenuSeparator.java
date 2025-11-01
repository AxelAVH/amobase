package de.amo.tools;

/**
 *  MR-Tool-Item als optischen Trenner im Men�.
 *  Die Ausf�hrung hat keine Auswirkung.
 *
 *  @author Reinhard Rust, dbh AG
 *  @since  08-03-2007
 */
public class IOToolsMenuSeparator extends IOToolsSelectItem {

    private String title;

    public IOToolsMenuSeparator(IOToolsSelectMenue menue) {
        super(menue);
        this.title = "- - - - - - - - - - - -";
    }

    public IOToolsMenuSeparator(String title) {
        this.title = title;
    }


    @Override
    public String getAnzeigeText() {
        String text = title;
        return text;
    }

    @Override
    public boolean doIt() throws Exception {
        return true;
    }
}