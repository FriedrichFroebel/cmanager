package cmanager;

import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.gui.MainWindow;
import cmanager.util.ForkUtil;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] arguments) {
        nagToUpdateFromJava7();

        try {
            ForkUtil.forkWithRezeidHeapAndExit(arguments);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        final MainWindow frame = new MainWindow();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
