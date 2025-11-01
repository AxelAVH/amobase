package de.amo.view;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Created by private on 05.01.2016.
 */
public class AFieldPane extends JPanel {

    private int TOTALHIGHT = 40;
    private int LeadingLabelLength  = 150;
    private JLabel leadingLabel;
    private int InputLength  = 100;
    private int TrailingLabelLength  = 120;
    private JLabel trailingLabel;

    public AFieldPane(String leadingLabelText, JComponent inputField, int totalhight, int leadingLabelLength, int inputLength, int trailingLabelLength) {

        TOTALHIGHT              = totalhight;
        LeadingLabelLength      = leadingLabelLength;
        InputLength             = inputLength;
        TrailingLabelLength     = trailingLabelLength;

        FlowLayout mgr = new FlowLayout(FlowLayout.LEFT, 3, 3);

        setLayout(mgr);

        JPanel mainPanel = this;

        if (AmoStyle.isGuiTestMode()) {
            mainPanel.setBackground(Color.blue);
        }

        JPanel basePanel;

        leadingLabel = createLabel(leadingLabelText);
        basePanel = createBasePanel(LeadingLabelLength);
        basePanel.add(leadingLabel);
        mainPanel.add(basePanel);

        basePanel = createBasePanel(InputLength);
        basePanel.add(inputField);
        mainPanel.add(basePanel);

        trailingLabel = createLabel("Das ist ein Hinterlabel :-)");
        basePanel = createBasePanel(TrailingLabelLength);
        basePanel.add(trailingLabel);
        mainPanel.add(basePanel);
    }

    private JPanel createBasePanel(int width) {
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        jPanel.setPreferredSize(new Dimension(width, TOTALHIGHT));

        outerPanel.add(jPanel);
        outerPanel.setPreferredSize(new Dimension(width, TOTALHIGHT));

        if (AmoStyle.isGuiTestMode()) {
            jPanel.setBackground(Color.YELLOW);
            outerPanel.setBackground(Color.green);
        }

        return outerPanel;
    }


    public JLabel getLeadingLabel() {
        return leadingLabel;
    }

    public JLabel getTrailingLabel() {
        return trailingLabel;
    }


    protected JLabel createLabel(String fuehrungstext) {
        JLabel label = new JLabel(fuehrungstext, JLabel.LEFT);
        if (AmoStyle.isGuiTestMode()) {
            label.setBackground(Color.CYAN);
        } else {

        }
        label.setOpaque(true);
        return label;
    }


    public static void main(String[] args) {

        TestDialog td = new TestDialog();
        td.setModal(true);
        td.setLayout(null);
        td.setTitle("Test-Dialog");
        td.setBounds(50,50,500,400);

        AStringInputField stringInputField = AStringInputField.create("defaulttext");

        AFieldPane fp = new AFieldPane("Label vorn" ,stringInputField, 25, 150, 200, 120);
        td.add(fp);

        BigDecimal bigDecimal = new BigDecimal(12345).movePointLeft(2);


        ANumberInputField numberInputField1 = ANumberInputField.create(34567,3);
        fp = new AFieldPane("Vorne für Zahl", numberInputField1, 25, 150, 200, 120);
        td.add(fp);

        ANumberInputField numberInputField2 = ANumberInputField.create(77,0);
        fp = new AFieldPane("Vorne für Zahl", numberInputField2, 25, 150, 200, 120);
        td.add(fp);

        ADateInputField dateInputField = ADateInputField.create("20151231");
        fp = new AFieldPane("Datum", dateInputField, 25, 150, 200, 120);
        td.add(fp);

        td.setVisible(true);

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        fp.getTrailingLabel().setText("HINTEN");

        System.out.println("Value aus Date " + dateInputField.getDateString());
        System.out.println("Value aus BigDecimal 1 " + numberInputField1.getIntValue());
        System.out.println("Value aus BigDecimal 2 " + numberInputField2.getIntValue());




    }



    static class TestDialog extends JDialog {





    }
}
