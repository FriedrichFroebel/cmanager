package cmanager.global;

import java.awt.Toolkit;

/** Compatibility definitions for older Java versions. */
public class Compatibility {

    /**
     * The modifier key being the appropriate accelerator key for menu shortcuts.
     *
     * <p>The old method is deprecated since Java 10.
     */
    public static int SHORTCUT_KEY_MASK = determineShortcutKeyMask();

    /**
     * Determine the shortcut for the key mask.
     *
     * @return The shortcut for the key mask.
     */
    private static int determineShortcutKeyMask() {
        final int javaVersion = Compatibility.getJavaVersionMain();
        if (javaVersion >= 10) {
            return Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        }

        System.out.println(
                "Falling back to the deprecated getMenuShortcutKeyMask() method. "
                        + "This compatibility function will probably get removed in a later version. "
                        + "Please consider upgrading to the latest Java version if possible. "
                        + "(At the moment Java 10 should be sufficient, but may lacks security updates.)");
        return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    }

    /**
     * Get the main Java version this application is currently running on.
     *
     * @return The Java version this is currently running on or -1 if there is an error.
     */
    private static int getJavaVersionMain() {
        final String[] parts = System.getProperty("java.version").split("\\.");

        // Try to retrieve the major version as an integer.
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException exception) {
            System.out.println("Could not determine Java version.");
            return -1;
        }
    }
}
