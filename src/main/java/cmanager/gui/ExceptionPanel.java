package cmanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class ExceptionPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static ExceptionPanel THIS = null;

    private final JPanel panelDetails;
    private final JPanel panelMessage;
    private final JTextPane textDetails;
    private final JScrollPane scrollPane;

    public static ExceptionPanel getPanel() {
        if (THIS == null) {
            THIS = new ExceptionPanel();
        }
        return THIS;
    }

    /** Create the panel. */
    private ExceptionPanel() {
        setLayout(new BorderLayout(0, 0));

        panelMessage = new JPanel();
        add(panelMessage, BorderLayout.NORTH);

        final JButton buttonEnlarge =
                new JButton("One or more exceptions occurred. Click to show/hide.");
        buttonEnlarge.setForeground(Color.RED);
        buttonEnlarge.setOpaque(false);
        buttonEnlarge.setContentAreaFilled(false);
        buttonEnlarge.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        panelDetails.setVisible(!panelDetails.isVisible());
                    }
                });
        panelMessage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelMessage.add(buttonEnlarge);

        final JButton buttonClose = new JButton("x");
        panelMessage.add(buttonClose);
        buttonClose.addActionListener(actionEvent -> hideUs());

        panelDetails = new JPanel();
        add(panelDetails, BorderLayout.CENTER);
        panelDetails.setLayout(new BorderLayout(0, 0));

        textDetails = new JTextPane();
        textDetails.setForeground(Color.RED);
        scrollPane =
                new JScrollPane(
                        textDetails,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelDetails.add(scrollPane);

        this.addComponentListener(
                new ComponentAdapter() {
                    public void componentResized(ComponentEvent componentEvent) {
                        final Dimension dimension = new Dimension(THIS.getWidth(), 200);
                        scrollPane.setPreferredSize(dimension);
                    }
                });

        hideUs();
    }

    private void hideUs() {
        panelDetails.setVisible(false);
        panelMessage.setVisible(false);
        textDetails.setText("");
    }

    private void displayInternal(String string) {
        String text = textDetails.getText();
        if (text.length() > 0) {
            text += "\n";
        }

        text += string;
        textDetails.setText(text);
        panelMessage.setVisible(true);
    }

    public static void display(Exception exception) {
        exception.printStackTrace();

        String string = exception.getClass().getName() + "\n";
        if (exception.getMessage() != null) {
            string += exception.getMessage() + "\n";
        }
        string += toString(exception);
        THIS.displayInternal(string);
    }

    public static void display(StackTraceElement[] stack) {
        THIS.displayInternal(toString(stack));
    }

    public static void display(String string) {
        System.err.println(string);
        THIS.displayInternal(string);
    }

    public static void showErrorDialog(Component parent, String errorMessage, String title) {
        JOptionPane.showMessageDialog(parent, errorMessage, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorDialog(Component parent, Throwable exceptionError) {
        String errorMessage = exceptionError.getMessage();
        errorMessage = errorMessage != null ? errorMessage : exceptionError.getClass().getName();
        errorMessage =
                "Message: " + errorMessage + "\n\nStackTrace: " + toShortString(exceptionError);

        final String title = exceptionError.getClass().getName();

        showErrorDialog(parent, errorMessage, title);

        if (exceptionError instanceof OutOfMemoryError) {
            final String message =
                    "You experienced the previous crash due to insufficient memory.\n"
                            + "You might want to change your memory settings under Menu->Settings->General.";
            JOptionPane.showMessageDialog(
                    parent, message, "Memory Settings", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static String toShortString(Throwable throwable) {
        final StringBuilder res = new StringBuilder();
        int lineNumber = 0;
        for (final StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            lineNumber++;
            res.append(stackTraceElement.toString()).append("\n");
            if (lineNumber == 12) {
                res.append("...");
                break;
            }
        }
        return res.toString();
    }

    public static String toString(Throwable throwable) {
        return toString(throwable.getStackTrace());
    }

    public static String toString(StackTraceElement[] stack) {
        final StringBuilder res = new StringBuilder();
        for (final StackTraceElement stackTraceElement : stack) {
            res.append(stackTraceElement.toString()).append("\n");
        }
        return res.toString();
    }
}
