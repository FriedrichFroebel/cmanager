package cmanager.oc;

import cmanager.geo.Geocache;
import cmanager.global.Constants;
import cmanager.util.DateTimeUtil;
import java.io.File;
import java.io.IOException;

/** Search result caching. */
public class SearchCache {

    /** The old directory containing the search cache. */
    private static final String LEGACY_CACHE_FOLDER = Constants.CACHE_FOLDER;

    /** The directory containing the search cache. */
    private static final String OKAPI_CACHE_FOLDER =
            Constants.CACHE_FOLDER + "OC.OKAPI.emptySearches/";

    /**
     * Status variable to indicate whether the cache directory has already been initialized/checked
     * for existence in this application run.
     */
    private static boolean initDone = false;

    /**
     * Get the filename for the given search request.
     *
     * @param geocache The geocache instance to search for.
     * @param excludeUuid The ID of the user to exclude.
     * @return The filename for the given search request.
     */
    private static String searchToFileName(Geocache geocache, String excludeUuid) {
        final String name = geocache.getCode() + (excludeUuid == null ? "" : " " + excludeUuid);
        return OKAPI_CACHE_FOLDER + name;
    }

    /**
     * Remember that the given search has been empty by creating the corresponding cache file (which
     * basically is an empty file).
     *
     * @param geocache The geocache instance to search for.
     * @param excludeUuid The ID of the user to exclude.
     */
    public static synchronized void setEmptySearch(Geocache geocache, String excludeUuid)
            throws IOException {
        final String filename = searchToFileName(geocache, excludeUuid);
        final File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();
    }

    /**
     * Check if the given search is known to be empty.
     *
     * <p>A search is considered empty if the corresponding cache file is not older than 6 months.
     *
     * @param geocache The geocache instance to search for.
     * @param excludeUuid The ID of the user to exclude.
     * @return Whether the given search is empty or not.
     */
    public static synchronized boolean isEmptySearch(Geocache geocache, String excludeUuid) {
        if (!initDone) {
            new File(OKAPI_CACHE_FOLDER).mkdirs();

            // If there are files in the legacy folder, move them into the new folder.
            final File[] legacyFiles = new File(LEGACY_CACHE_FOLDER).listFiles();
            if (legacyFiles != null) {
                for (final File file : legacyFiles) {
                    if (file.getName().startsWith("GC")) {
                        final String filename = OKAPI_CACHE_FOLDER + file.getName();
                        final boolean renamingSuccess = file.renameTo(new File(filename));
                        if (!renamingSuccess) {
                            System.out.println(
                                    "Error renaming file "
                                            + file.getName()
                                            + " to "
                                            + filename
                                            + ".");
                        }
                    }
                }
            }

            initDone = true;
        }

        // Perform a request to the cache/cache file itself.
        final File file = new File(searchToFileName(geocache, excludeUuid));
        if (file.exists()) {
            // Older than 6 months?
            if (DateTimeUtil.isTooOldWithMonths(file, 6)) {
                file.delete();
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
