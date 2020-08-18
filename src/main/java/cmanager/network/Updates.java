package cmanager.network;

import cmanager.global.Version;
import cmanager.xml.Element;
import cmanager.xml.Parser;
import java.util.List;

/**
 * Update checking handlers.
 *
 * <p>Please note that all of the code inside this class is specifically tailored to this project,
 * id est the version string format and the version string of new versions being set as the title of
 * each release. Otherwise this update check might fail with unexpected results.
 */
public class Updates {

    /**
     * Whether an update is available.
     *
     * <p>This is used for caching the result during one run to avoid having to request the data
     * multiple times.
     */
    private static Boolean updateAvailable = null;

    /** The version string for the new version. */
    private static String newVersion = null;

    /**
     * Check whether an update is available.
     *
     * <p>This will fall back to a cached result whenever possible.
     *
     * @return Whether an update is available.
     */
    public static synchronized boolean updateAvailable_block() {
        if (updateAvailable == null) {
            try {
                final String url = "https://github.com/FriedrichFroebel/cmanager/releases.atom";
                final String http = Http.get(url);

                final Element root = Parser.parse(http);

                newVersion = Updates.findLatestRelease(root);
                updateAvailable = Updates.isUpdateAvailable(Version.VERSION, newVersion);
            } catch (Throwable ignored) {
                // Errors might be due to missing internet connection.
                // This will be called for not parseable version strings as well.
                updateAvailable = false;
            }
        }

        return updateAvailable;
    }

    /**
     * Get the name of the new version.
     *
     * @return The name of the new version.
     */
    public static String getNewVersion() {
        return newVersion;
    }

    /**
     * Find the name of the latest release.
     *
     * <p>This requires parsing the XML for all entries, starting with the latest, and checking the
     * title as Travis might introduce pre-releases.
     *
     * @param root The root element of the XML file.
     * @return The latest version number.
     * @throws NumberFormatException No suitable version could be found.
     */
    private static String findLatestRelease(Element root) throws NumberFormatException {
        final List<Element> entries = root.getChild("feed").getChildren("entry");
        for (final Element entry : entries) {
            final String title = entry.getChild("title").getUnescapedBody();
            if (title != null && !title.isEmpty() && Character.isDigit(title.charAt(0))) {
                return title;
            }
        }
        throw new NumberFormatException("Could not find suitable version.");
    }

    /**
     * Compare the two version strings to check whether an update is available.
     *
     * @param oldVersion The version string for the old version.
     * @param newVersion The version string for the new version.
     * @return Whether there is an update available or not. Empty new versions will correspond to no
     *     update, as well as newer old versions.
     * @throws NumberFormatException One of the groups does not hold a valid integer value.
     */
    private static boolean isUpdateAvailable(String oldVersion, String newVersion)
            throws NumberFormatException {
        // Something went wrong during the download.
        if (newVersion == null || newVersion.isEmpty()) {
            return false;
        }

        // Split the old version name.
        final String[] oldParts = oldVersion.split("\\.");
        final int oldLength = oldParts.length;

        // Split the new version name.
        final String[] newParts = newVersion.split("\\.");
        final int newLength = newParts.length;

        // Determine the maximum split length.
        final int maxLength = Math.max(oldLength, newLength);

        // Compare groupwise.
        for (int i = 0; i < maxLength; i++) {
            final int oldEntry = i < oldLength ? Integer.parseInt(oldParts[i]) : 0;
            final int newEntry = i < newLength ? Integer.parseInt(newParts[i]) : 0;

            if (oldEntry < newEntry) {
                return true;
            }
            if (oldEntry > newEntry) {
                return false;
            }
        }

        return false;
    }
}
