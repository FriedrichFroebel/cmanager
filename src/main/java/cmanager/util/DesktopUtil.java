package cmanager.util;

import cmanager.gui.ExceptionPanel;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/** Utility methods for desktop actions. */
public class DesktopUtil {

    /**
     * Open the given URL in the default web browser.
     *
     * @param uriString The URL to open as a string.
     */
    public static void openUrl(final String uriString) {
        // First step: Validate the given URI and abort on error.
        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException exception) {
            ExceptionPanel.showErrorDialog(null, exception);
            return;
        }

        // Try opening the browser itself.
        try {
            // Try to easiest approach at first.
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(uri);
                    return;
                } catch (UnsupportedOperationException | IOException exception) {
                    // Add the exception anyway.
                    exception.printStackTrace();
                }
            }

            // If something did not work (an exception occurred or desktop is not supported), try
            // the fallback method.
            openUrlFallback(uriString);
        } catch (UnsupportedOperationException | IOException exception) {
            // Both approaches failed, so report the last error from the fallback method.
            ExceptionPanel.showErrorDialog(null, exception);
        }
    }

    /**
     * Open the given URL in the default web browser using an own operating system specific
     * approach.
     *
     * @param uriString The URL to open as a string.
     * @throws UnsupportedOperationException The operating system is unknown.
     * @throws IOException Executing the command itself failed.
     */
    private static void openUrlFallback(final String uriString)
            throws UnsupportedOperationException, IOException {
        // Retrieve the name of the operating system.
        final String os = System.getProperty("os.name").toLowerCase();

        // Handle each operating system separately.
        if (os.contains("windows")) {
            // Try to use the corresponding DLL.
            new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", uriString).start();
        } else if (os.contains("mac")) {
            new ProcessBuilder("open", uriString).start();
        } else if (os.contains("nix") || os.contains("nux")) {
            // Try to use the `xdg-open` command from `xdg-utils`.
            new ProcessBuilder("xdg-open", uriString).start();
        } else {
            // This is an operating system where we do not know how to open an URL.
            throw new UnsupportedOperationException("Unknown operating system: '" + os + "'.");
        }
    }
}
