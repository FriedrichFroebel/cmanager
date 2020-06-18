package cmanager.util;

import cmanager.Main;
import cmanager.settings.Settings;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

public class ForkUtil {

    private static final String PARAM_HEAP_RESIZED = "--memory-already-resized";

    /**
     * Returns the folder containing the .class file.
     *
     * <p>This is the JAR path if run from a JAR.
     */
    public static String getCodeSource() {
        final File jarFile =
                new java.io.File(
                        Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return jarFile.getAbsolutePath();
    }

    private static void showInvalidJarPathMessage(String jarPath) {
        final String message =
                "Unable to restart cmanager. Settings could not be applied.\n"
                        + "Expected path: "
                        + jarPath;
        JOptionPane.showMessageDialog(null, message, "jar path", JOptionPane.ERROR_MESSAGE);
    }

    public static void runCopyAndExit() throws IOException {
        final String jarPath = getCodeSource();
        if (!new File(jarPath).exists()) {
            showInvalidJarPathMessage(jarPath);
            return;
        }

        // Run new VM.
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", jarPath);
        processBuilder.start();

        System.exit(0);
    }

    public static void forkWithResizedHeapAndExit(String[] arguments) throws IOException {
        for (final String argument : arguments) {
            if (argument.equals(PARAM_HEAP_RESIZED)) {
                return;
            }
        }

        // Read settings.
        final String heapSizeString = Settings.getString(Settings.Key.HEAP_SIZE);
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

        // Run new VM.
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
