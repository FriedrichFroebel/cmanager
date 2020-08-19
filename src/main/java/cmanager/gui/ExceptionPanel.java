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

/** Panel for displaying exception messages. */
public class ExceptionPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /** The current instance. */
    private static ExceptionPanel THIS = null;

    /** The panel with the exception details. */
    private final JPanel panelDetails;

    /** The panel with the error message. */
    private final JPanel panelMessage;

    /** The actual stacktrace data. */
    private final JTextPane textDetails;

    /** Allow scrolling the stacktrace. */
    private final JScrollPane scrollPane;

    /**
     * Get the panel.
     *
     * @return The panel.
     */
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

        // Add button to show/hide exception details.
        final JButton buttonEnlarge =
                new JButton("One or more exceptions occurred. Click to show/hide.");
        buttonEnlarge.setForeground(Color.RED);
        buttonEnlarge.setOpaque(false);
        buttonEnlarge.setContentAreaFilled(false);
        buttonEnlarge.addActionListener(
                new ActionListener() {
                    public void actionPerformed(final ActionEvent actionEvent) {
                        panelDetails.setVisible(!panelDetails.isVisible());
                    }
                });
        panelMessage.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelMessage.add(buttonEnlarge);

        // Add button to close the panel.
        final JButton buttonClose = new JButton("x");
        panelMessage.add(buttonClose);
        buttonClose.addActionListener(actionEvent -> hideUs());

        panelDetails = new JPanel();
        add(panelDetails, BorderLayout.CENTER);
        panelDetails.setLayout(new BorderLayout(0, 0));

        // Add the details container.
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
                    public void componentResized(final ComponentEvent componentEvent) {
                        final Dimension dimension = new Dimension(THIS.getWidth(), 200);
                        scrollPane.setPreferredSize(dimension);
                    }
                });

        // Hide at the beginning.
        hideUs();
    }

    /** Hide the panel and remove the text. */
    private void hideUs() {
        panelDetails.setVisible(false);
        panelMessage.setVisible(false);
        textDetails.setText("");
    }

    /**
     * Append the given string and make the panel visible.
     *
     * @param string The string to append.
     */
    private void displayInternal(final String string) {
        String text = textDetails.getText();
        if (text.length() > 0) {
            text += "\n";
        }

        text += string;
        textDetails.setText(text);
        panelMessage.setVisible(true);
    }

    /**
     * Display the given exception.
     *
     * @param exception The exception to display.
     */
    public static void display(final Exception exception) {
        exception.printStackTrace();

        String string = exception.getClass().getName() + "\n";
        if (exception.getMessage() != null) {
            string += exception.getMessage() + "\n";
        }
        string += toString(exception);
        THIS.displayInternal(string);
    }

    /**
     * Display the given stacktrace.
     *
     * @param stack The stacktrace to display.
     */
    public static void display(final StackTraceElement[] stack) {
        THIS.displayInternal(toString(stack));
    }

    /**
     * Display the given string.
     *
     * @param string The string to display.
     */
    public static void display(final String string) {
        System.err.println(string);
        THIS.displayInternal(string);
    }

    /**
     * Show an error dialog with the given message.
     *
     * @param parent The parent frame/component.
     * @param errorMessage The error message to show.
     * @param title The title of the dialog.
     */
    public static void showErrorDialog(
            final Component parent, final String errorMessage, final String title) {
        JOptionPane.showMessageDialog(parent, errorMessage, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show an error dialog for the given exception.
     *
     * @param parent The parent frame/component.
     * @param exceptionError The error to show.
     */
    public static void showErrorDialog(final Component parent, final Throwable exceptionError) {
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

    /**
     * Convert the given throwable into a shortened string.
     *
     * @param throwable The throwable to convert.
     * @return The short string for the given throwable.
     */
    public static String toShortString(final Throwable throwable) {
        final StringBuilder stringBuilder = new StringBuilder();
        int lineNumber = 0;

        for (final StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            lineNumber++;
            stringBuilder.append(stackTraceElement.toString()).append("\n");
            if (lineNumber == 12) {
                stringBuilder.append("...");
                break;
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Convert the given throwable to a string.
     *
     * @param throwable The throwable to convert.
     * @return The string for the given throwable.
     */
    public static String toString(final Throwable throwable) {
        return toString(throwable.getStackTrace());
    }

    /**
     * Convert the given stacktrace to a string.
     *
     * @param stack The stacktrace to convert.
     * @return The string for the given stacktrace.
     */
    public static String toString(final StackTraceElement[] stack) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (final StackTraceElement stackTraceElement : stack) {
            stringBuilder.append(stackTraceElement.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
