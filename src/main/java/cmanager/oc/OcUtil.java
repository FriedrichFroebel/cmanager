package cmanager.oc;

import cmanager.geo.Geocache;
import cmanager.geo.GeocacheComparator;
import cmanager.global.Constants;
import cmanager.list.CacheListModel;
import cmanager.okapi.Okapi;
import cmanager.okapi.User;
import cmanager.util.LoggingUtil;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class OcUtil {

    private static final Logger LOGGER = LoggingUtil.getLogger(OcUtil.class);

    static final List<Geocache> OKAPI_RUNTIME_CACHE = new ArrayList<>();

    /**
     * @param stopBackgroundThread Processing is interrupted if this boolean is set true
     * @param cacheListModel The model supplying the caches to check
     * @param outputInterface Callback functions
     * @param user OCUser object for OKAPI authentication
     * @param uuid The uuid of the OC user to exclude caches already found by this user
     * @throws Throwable Something went wrong.
     */
    public static void findOnOc(
            final AtomicBoolean stopBackgroundThread,
            final CacheListModel cacheListModel,
            final OutputInterface outputInterface,
            final User user,
            final String uuid,
            final ShadowList shadowList)
            throws Throwable {
        // Number of found duplicates.
        final AtomicInteger count = new AtomicInteger(0);
        // Thread pool which establishes 10 concurrent connection at max.
        final ExecutorService service = Executors.newFixedThreadPool(10);
        // Variable to hold an exception throwable if one is thrown by a task.
        final AtomicReference<Throwable> throwable = new AtomicReference<>(null);

        // Create a task for each cache and submit it to the thread pool.
        for (final Geocache geocache : cacheListModel.getList()) {
            if (throwable.get() != null) {
                break;
            }

            if (stopBackgroundThread.get()) {
                break;
            }

            final Callable<Void> callable =
                    () ->
                            findSingleGeocache(
                                    stopBackgroundThread,
                                    cacheListModel,
                                    outputInterface,
                                    user,
                                    uuid,
                                    shadowList,
                                    count,
                                    geocache,
                                    throwable);
            service.submit(callable);
        }

        service.shutdown();

        // Incredible high delay but still ugly.
        service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        if (throwable.get() != null) {
            throw throwable.get();
        }

        outputInterface.setProgress(
                cacheListModel.getList().size(), cacheListModel.getList().size());
    }

    public static Void findSingleGeocache(
            final AtomicBoolean stopBackgroundThread,
            final CacheListModel cacheListModel,
            final OutputInterface outputInterface,
            final User user,
            final String uuid,
            final ShadowList shadowList,
            final AtomicInteger count,
            final Geocache geocache,
            final AtomicReference<Throwable> throwableReference) {
        if (stopBackgroundThread.get()) {
            return null;
        }

        try {
            outputInterface.setProgress(count.get(), cacheListModel.getList().size());
            count.getAndIncrement();

            if (SearchCache.isEmptySearch(geocache, uuid)) {
                return null;
            }

            // Search shadow list for a duplicate.
            // TODO: Enable if API works again.
            /*final String ocCode = shadowList.getMatchingOcCode(geocache.getCode());
            if (ocCode != null) {
                Geocache oc = Okapi.getCacheBuffered(ocCode, OKAPI_RUNTIME_CACHE);
                Okapi.completeCacheDetails(oc);
                Okapi.updateFoundStatus(user, oc);
                // Found status can not be retrieved without user so we have a match when there is
                // no user or the user has not found the cache.
                if (user == null || !oc.getIsFound()) {
                    outputInterface.match(geocache, oc);
                    return null;
                }
            }*/

            // Search for duplicate using the OKAPI.
            final double searchRadius = geocache.hasVolatileStart() ? 1 : 0.05;
            final List<Geocache> similar =
                    Okapi.getCachesAround(user, uuid, geocache, searchRadius, OKAPI_RUNTIME_CACHE);
            LOGGER.info(
                    MessageFormat.format(
                            "Found {0} candidates for {1}.", similar.size(), geocache.getCode()));

            boolean match = false;
            for (final Geocache opencache : similar) {
                // Use the basic copy for the opencache to make the search more reliable across
                // multiple runs without closing the application in between.
                if (GeocacheComparator.areSimilar(opencache.getBasicCopy(), geocache)) {
                    Okapi.completeCacheDetails(opencache);
                    outputInterface.match(geocache, opencache);
                    match = true;
                }
            }

            if (!match) {
                SearchCache.setEmptySearch(geocache, uuid);
            }
        } catch (Throwable throwable) {
            throwableReference.set(throwable);
        }

        return null;
    }

    /**
     * Determine the URL of the given geocache log.
     *
     * @param opencache The Opencache instance to retrieve the internal cache ID from.
     * @param logId The internal log ID to link to.
     * @return The log URL for the given cache.
     */
    public static String determineLogUrl(final Geocache opencache, final String logId) {
        return Constants.SITE_BASE
                + "viewcache.php?cacheid="
                + opencache.getInternalId()
                + "&log"
                + "=A#log"
                + logId;
    }
}
