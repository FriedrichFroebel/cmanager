package cmanager.okapi;

import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.oc.OcSite;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.net.URIBuilder;

/**
 * Build the URLs for OKAPI requests.
 *
 * <p>This should improve the overall readability of the OKAPI class code. Additionally we let the
 * Apache HTTP Components library do the job of encoding our strings automatically instead of
 * specifying it manually.
 *
 * <p>This class and its methods are package-private as they do not need to be accessed from the
 * outside.
 */
class OkapiUrlBuilder {

    /**
     * Get the basic builder with the correct URL for the selected site, the charset set to UTF-8
     * and the consumer key already being passed as a parameter.
     *
     * @return The basic builder to base further URL creations on.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    private static URIBuilder getBase() throws URISyntaxException {
        return new URIBuilder(OcSite.getBaseUrl())
                .setCharset(StandardCharsets.UTF_8)
                .addParameter("consumer_key", OcSite.getConsumerApiKey());
    }

    /**
     * Get the URL to convert an username to the corresponding user UUID.
     *
     * @param username The username to get the UUID for.
     * @return The URL for converting the given username to the corresponding user ID.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getUsernameToUuidUrl(final String username) throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/users/by_username")
                .addParameter("username", username)
                .addParameter("fields", "uuid")
                .toString();
    }

    /**
     * Get the URL to get the basic cache details for the given OC code.
     *
     * @param ocCode The OC code to get the basic cache details for.
     * @return The URL for getting the basic cache details for the given OC code.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getCacheUrl(final String ocCode) throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/caches/geocache")
                .addParameter("cache_code", ocCode)
                .addParameter(
                        "fields",
                        "code|name|location|type|gc_code|difficulty|terrain|status|date_hidden")
                .toString();
    }

    /**
     * Get the URL to complete the cache details for the given opencache.
     *
     * @param opencache The opencache to complete the cache details for.
     * @return The URL for completing the cache details of the given opencache.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getCompleteCacheDetailsUrl(final Geocache opencache) throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/caches/geocache")
                .addParameter("cache_code", opencache.getCode())
                .addParameter(
                        "fields",
                        "size2|short_description|description|owner|hint2|url|req_passwd|"
                                + "internal_id")
                .toString();
    }

    /**
     * Get the URL to get the opencaches around the given coordinate.
     *
     * @param useOAuth Whether OAuth is required as caches ignored by the given user UUID should be
     *     skipped.
     * @param excludeUuuid The user UUID to use for ignoring caches. Additionally own caches will be
     *     ignored. This may be null if OAuth is disabled.
     * @param latitude The latitude of the search center.
     * @param longitude The longitude of the search center.
     * @param searchRadius The search radius to use.
     * @return The URL to get the opencaches around the given coordinate.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getCachesAroundUrl(
            final boolean useOAuth,
            final String excludeUuuid,
            final Double latitude,
            final Double longitude,
            final Double searchRadius)
            throws URISyntaxException {
        final Coordinate coordinate = new Coordinate(latitude, longitude);
        final URIBuilder builder =
                getBase()
                        .setPath("okapi/services/caches/search/nearest")
                        .addParameter("center", coordinate.toString("|"))
                        .addParameter("radius", searchRadius.toString())
                        .addParameter("status", "Available|Temporarily unavailable|Archived")
                        .addParameter("limit", String.valueOf(500));

        if (useOAuth && excludeUuuid != null) {
            builder.addParameter("ignored_status", "notignored_only")
                    .addParameter("not_found_by", excludeUuuid)
                    .addParameter("exclude_my_own", "true");
        }

        return builder.toString();
    }

    /**
     * Get the URL to check the found status of the given opencache.
     *
     * @param opencache The opencache to check the found status for.
     * @return The URL to check the found status of the given opencache.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getFoundStatusUrl(final Geocache opencache) throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/caches/geocache")
                .addParameter("cache_code", opencache.getCode())
                .addParameter("fields", "is_found")
                .toString();
    }

    /**
     * Get the URL to retrieve the user UUID of the user authenticated using OAuth.
     *
     * @return The URL to retrieve the UUID of the current user.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getUuidUrl() throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/users/user")
                .addParameter("fields", "uuid")
                .toString();
    }

    /**
     * Get the URL to retrieve the username of the user authenticated using OAuth.
     *
     * @return The URL to retrieve the username of the current user.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getUsernameUrl() throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/users/user")
                .addParameter("fields", "username")
                .toString();
    }

    /**
     * Get the URL for submitting the given log entry for the given opencache.
     *
     * @param opencache The opencache to submit the log for.
     * @param log The log to submit.
     * @return The URL for submitting the log entry for the opencache.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getLogSubmissionUrl(final Geocache opencache, final GeocacheLog log)
            throws URISyntaxException {
        final URIBuilder builder =
                getBase()
                        .setPath("okapi/services/logs/submit")
                        .addParameter("cache_code", opencache.getCode())
                        .addParameter("logtype", log.getOkapiType(opencache))
                        .addParameter("comment", log.getText())
                        .addParameter("when", log.getDateStrIso8601NoTime());

        // The GUI does not yet provide this functionality, so we keep it disabled for now.
        final Boolean doesRequirePassword = opencache.doesRequirePassword();
        if (doesRequirePassword != null && doesRequirePassword) {
            builder.addParameter("password", log.getPassword());
        }

        return builder.toString();
    }

    /**
     * Get the URL to retrieve the home coordinates of the given user.
     *
     * @param uuid The user to get the home coordinates for.
     * @return The URL to retrieve the home coordinates of the given user.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getHomeCoordinatesUrl(final String uuid) throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/users/user")
                .addParameter("user_uuid", uuid)
                .addParameter("fields", "home_location")
                .toString();
    }

    /**
     * Get the URL to convert the given log UUID to an (internal) log ID.
     *
     * @param logUuid The log UUID to get the log ID for.
     * @return The URL to convert the given log UUID to an (internal) log ID.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getLogIdUrl(final String logUuid) throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/logs/entry")
                .addParameter("log_uuid", logUuid)
                .addParameter("fields", "internal_id")
                .toString();
    }

    /**
     * Get the URL to delete the given log entry.
     *
     * @param logUuid The UUID of the log to delete.
     * @return The URL to delete the given log entry.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    static String getLogDeletionUrl(final String logUuid) throws URISyntaxException {
        return getBase()
                .setPath("okapi/services/logs/delete")
                .addParameter("log_uuid", logUuid)
                .toString();
    }
}
