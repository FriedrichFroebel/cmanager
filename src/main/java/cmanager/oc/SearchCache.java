package cmanager.oc;

import cmanager.geo.Geocache;
import cmanager.global.Constants;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import org.joda.time.DateTime;

/** Search result caching. */
public class SearchCache {

    private static final String LEGACY_CACHE_FOLDER = Constants.CACHE_FOLDER;
    private static final String OKAPI_CACHE_FOLDER =
            Constants.CACHE_FOLDER + "OC.OKAPI.emptySearches/";
    private static boolean initDone = false;

    private static String searchToFileName(Geocache geocache, String excludeUuid) {
        final String name = geocache.getCode() + (excludeUuid == null ? "" : " " + excludeUuid);
        return OKAPI_CACHE_FOLDER + name;
    }

    public static synchronized void setEmptySearch(Geocache geocache, String excludeUuid)
            throws IOException {
        final String filename = searchToFileName(geocache, excludeUuid);
        final File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }

        file.createNewFile();
    }

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

        final File file = new File(searchToFileName(geocache, excludeUuid));
        if (file.exists()) {
            final int randomMonthCount = -1 * ThreadLocalRandom.current().nextInt(4, 12 + 1);
            final int randomDayCount = -1 * ThreadLocalRandom.current().nextInt(0, 31 + 1);
            DateTime now = new DateTime();
            now = now.plusMonths(randomMonthCount);
            now = now.plusDays(randomDayCount);

            // Outdated?
            if (now.isAfter(new DateTime(file.lastModified()))) {
                file.delete();
                return false;
            } else {
                return true;
            }
        }

        return false;
    }
}
