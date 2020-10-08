package cmanager.util;

import cmanager.Main;
import cmanager.settings.Settings;
import cmanager.settings.SettingsKey;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

/** Handle restarts of the own applications with modified parameters. */
public class ForkUtil {

    /** Argument to indicate that the heap has already been resized. */
    private static final String PARAM_HEAP_RESIZED = "--memory-already-resized";

    /**
     * Return the directory containing the .class file.
     *
     * <p>This is the JAR path if run from a JAR.
     *
     * @return The directory containing the class file.
     */
    public static String getCodeSource() {
        final File jarFile =
                new java.io.File(
                        Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return jarFile.getAbsolutePath();
    }

    /**
     * Report an error with restarting the application.
     *
     * @param jarPath The JAR path to display in the report.
     */
    private static void showInvalidJarPathMessage(final String jarPath) {
        final String message =
                "Unable to restart cmanager. Settings could not be applied.\n"
                        + "Expected path: "
                        + jarPath;
        JOptionPane.showMessageDialog(null, message, "jar path", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Run a copy of the application itself and exit the current application.
     *
     * @throws IOException Something went wrong when trying to do so.
     */
    public static void runCopyAndExit() throws IOException {
        final String jarPath = getCodeSource();
        if (!new File(jarPath).exists()) {
            showInvalidJarPathMessage(jarPath);
            return;
        }

        // Run a new VM.
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarPath);
        processBuilder.start();

        // Exit the old application.
        System.exit(0);
    }

    /**
     * Run a copy of the application with a resized heap and exit the current application.
     *
     * @param arguments The terminal arguments passed to the application.
     * @throws IOException Something went wrong when trying to do so.
     */
    public static void forkWithResizedHeapAndExit(String[] arguments) throws IOException {
        // Only do this once.
        for (final String argument : arguments) {
            if (argument.equals(PARAM_HEAP_RESIZED)) {
                return;
            }
        }

        // Read settings.
        final String heapSizeString = Settings.getString(SettingsKey.HEAP_SIZE);
        Integer heapSizeInteger = null;
        try {
            heapSizeInteger = Integer.valueOf(heapSizeString);
        } catch (Throwable ignored) {
        }

        if (heapSizeInteger == null || heapSizeInteger < 128) {
            return;
        }

        // Query path.
        final String jarPath = getCodeSource();
        if (!new File(jarPath).exists()) {
            showInvalidJarPathMessage(jarPath);
            return;
        }

        // Run a new VM.
        final String originalArguments = String.join(" ", arguments);
        final ProcessBuilder processBuilder =
                new ProcessBuilder()
                        .inheritIO()
                        .command(
                                "java",
                                "-Xmx" + heapSizeInteger.toString() + "m",
                                "-jar",
                                jarPath,
                                PARAM_HEAP_RESIZED,
                                originalArguments);
        final Process process = processBuilder.start();
        int retval = -1;
        try {
            retval = process.waitFor();
        } catch (InterruptedException ignored) {
        }

        if (retval == 0) {
            System.exit(0); // New VM ran fine.
        } else {
            String message =
                    "The chosen heap size could not be applied. \n"
                            + "Maybe there are insufficient system resources.";
            JOptionPane.showMessageDialog(
                    null, message, "Memory Settings", JOptionPane.INFORMATION_MESSAGE);

            if (System.getProperty("sun.arch.data.model").equals("32")) {
                message =
                        "You are running a 32 bit Java VM. \n"
                                + "This limits your available memory to less than 4096 MB \n"
                                + "and in some configurations to less than 2048 MB. \n\n"
                                + "Install a 64 bit VM to get rid of this limitation!";
                JOptionPane.showMessageDialog(
                        null, message, "Memory Settings", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
