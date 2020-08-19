package cmanager;

import cmanager.global.Constants;
import cmanager.global.Version;
import cmanager.gui.MainWindow;
import cmanager.util.ForkUtil;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/** The main entry point. */
public class Main {

    /**
     * The main method.
     *
     * @param arguments The terminal arguments passed to the application.
     */
    public static void main(String[] arguments) {
        try {
            ForkUtil.forkWithResizedHeapAndExit(arguments);
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
}
