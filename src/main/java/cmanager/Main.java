package cmanager;

import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.gui.MainWindow;
import cmanager.util.ForkUtil;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {

    public static void main(String[] arguments) {
        nagToUpdateFromJava7();

        try {
            ForkUtil.forkWithRezeidHeapAndExit(arguments);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        // Try to set the look and feel to follow the system style.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                | InstantiationException
                | IllegalAccessException
                | ClassCastException
                | UnsupportedLookAndFeelException exception) {
            System.out.println(
                    "Failed setting system look and feel. Falling back to Java default.");
        }

        // Display the frame.
        final MainWindow frame = new MainWindow();
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(Constants.APP_NAME + " " + Version.VERSION);
        frame.setVisible(true);
    }

    private static void nagToUpdateFromJava7() {
        if (System.getProperty("java.version").startsWith("1.7.")) {
            final String message =
                    "You are using the outdated Java version 1.7.\n"
                            + "Please update to Java 1.8 or later.";

            JOptionPane.showMessageDialog(null, message, "Java version", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}
