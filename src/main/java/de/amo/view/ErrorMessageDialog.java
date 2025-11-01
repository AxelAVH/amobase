package de.amo.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Created by private on 06.09.2015.
 */
public class ErrorMessageDialog {

    JButton closeButton;
    ActionListener actionListener;
    JDialog dialog;

    public ErrorMessageDialog(String titel, String message, Exception except) {

        actionListener = new ActionListener();

        int w = 700;
        int h = except == null ? 150 : 500;

        dialog = new JDialog();

        if (AmoStyle.isGuiTestMode()) {

            dialog.setBackground(Color.cyan);
        }

        dialog.setTitle(titel);
        dialog.setSize(w, h);

        JTextArea messageArea = new JTextArea();
        messageArea.setText(message);
        messageArea.setAlignmentX(SwingConstants.CENTER);
        messageArea.setAlignmentY(SwingConstants.CENTER);

        if (except == null) {
            //JScrollPane scrollPane = new JScrollPane(messageArea);
            dialog.add(messageArea);
        } else {
            dialog.setLayout(new java.awt.BorderLayout());
            Dimension dimension = new Dimension(500, 100);
            messageArea.setPreferredSize(dimension);
            dialog.add(messageArea, BorderLayout.NORTH);
            JTextArea textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            String text = except.getMessage();
            StackTraceElement[] stackTrace = except.getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                StackTraceElement stackTraceElement = stackTrace[i];
                text += "\nat " + stackTraceElement.toString();
            }
            textArea.setText(text);

            JScrollPane scrollPane = new JScrollPane(textArea);
            dialog.add(scrollPane, BorderLayout.CENTER);
        }

        JPanel buttonPanel = new JPanel();
        closeButton = new JButton("Schlie�en");
        closeButton.addActionListener(actionListener);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);


        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }


    class ActionListener implements java.awt.event.ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                if (actionEvent.getSource() == closeButton) {
                    dialog.dispose();
                }
            } catch (Exception e) {
                new ErrorMessageDialog("Fehler beim Schlie�en der Fehlermeldung", e.getMessage(), e);
            }

        }
    }

}
