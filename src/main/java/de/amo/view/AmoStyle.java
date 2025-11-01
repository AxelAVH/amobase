package de.amo.view;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by amo on 28.08.2015.
 */
    public class AmoStyle {

        public static boolean isGuiTestMode() {
            return false;
        }

        public static final Color COLOR_DBH_RED         = new Color(206, 0, 31);
        public static final Color COLOR_DBH_GREY        = new Color(135, 136, 138);
        public static final Color COLOR_DARK_GREY       = new Color(64, 64, 64);
        public static final Color COLOR_HELP_BACKGROUND = new Color(250, 246, 229);

        public static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
        public static final Color LINK_TEXT_COLOR    = Color.BLUE;
        public static final Color COLOR_LIGHT_GREEN  = new Color(136, 206, 136);
        public static final Color COLOR_DARK_GREEN   = new Color(78, 129, 81);
        public static final Color COLOR_ORANGE       = new Color(255, 236, 128);
        public static final Color COLOR_ORANGE_LIGHT = new Color(255, 247, 191);
        public static final Color COLOR_NO_ERROR     = SystemColor.window;
        public static final Color COLOR_NOT_EDITABLE = Color.gray;
        public static final Color COLOR_SYNTAX_ERROR = new Color(255, 108, 71);
        public static final Color COLOR_LINK_BLUE    = new Color(102, 129, 192);


        public static final Border ACTIVE_BORDER            = BorderFactory.createLineBorder(COLOR_LINK_BLUE, 2);
        public static final Border ACTIVE_NAVIGATION_BORDER = BorderFactory.createLineBorder(LINK_TEXT_COLOR, 2);
        public static final Border ERROR_BORDER             = BorderFactory.createLineBorder(COLOR_ORANGE, 2);
        public static       Border DEFAULT_TEXTFIELD_BORDER = null;

        public static final int NUMERIC_ALIGNMENT_EDITOR = SwingConstants.RIGHT;
        public static final int NUMERIC_ALIGNMENT_TABLE  = SwingConstants.RIGHT;

        public Border getActiveBorder() {
            return ACTIVE_BORDER;
        }

        public Border getDefaultTextFieldBorder() {
            if (DEFAULT_TEXTFIELD_BORDER == null) {
                DEFAULT_TEXTFIELD_BORDER = new JTextField().getBorder();
            }
            return DEFAULT_TEXTFIELD_BORDER;
        }

        public Border getErrorBorder() {
            return ERROR_BORDER;
        }

        public Color getColorError() {
            return COLOR_ORANGE;
        }

        public Color getColorSyntaxError() {
            return COLOR_SYNTAX_ERROR;
        }

        public Color getColorNoError() {
            return COLOR_NO_ERROR;
        }

        public Color getColorNotEditable() {
            return COLOR_NOT_EDITABLE;
        }

        public Color getColorMandatory() {
            return COLOR_NO_ERROR;
        }

        public Color getColorReadOnly() {
            return COLOR_NOT_EDITABLE;
        }

        public Color getColorIllegal() {
            return COLOR_NOT_EDITABLE;
        }

        public boolean useSingleEditor() {
            return false;
        }

        public int getNumericAlignmentEditor() {
            return SwingConstants.RIGHT;
        }

        public int getNumericAlignmentTable() {
            return SwingConstants.RIGHT;
        }
}
