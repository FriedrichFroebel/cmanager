package cmanager.okapi;

import cmanager.exception.CoordinateUnparsableException;
import cmanager.exception.UnexpectedLogStatus;
import cmanager.geo.Coordinate;
import cmanager.geo.Geocache;
import cmanager.geo.GeocacheLog;
import cmanager.gui.ExceptionPanel;
import cmanager.network.ApacheHttp;
import cmanager.network.HttpResponse;
import cmanager.network.UnexpectedStatusCode;
import cmanager.oc.OcSite;
import cmanager.okapi.responses.CachesSearchNearestDocument;
import cmanager.okapi.responses.ErrorDocument;
import cmanager.okapi.responses.GeocacheDocument;
import cmanager.okapi.responses.LogDeletionDocument;
import cmanager.okapi.responses.LogDocument;
import cmanager.okapi.responses.LogSubmissionDocument;
import cmanager.okapi.responses.UserDocument;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** Handle communication with the OKAPI. */
public class Okapi {

    /** The HTTP client to use. */
    private static final ApacheHttp httpClient = new ApacheHttp();

    /**
     * Convert the given username to an user UUID.
     *
     * @param username The username to convert.
     * @return The user UUID for the given username.
     * @throws IOException Something went wrong with the network request.
     * @throws UnexpectedStatusCode Something went wrong with our OKAPI request.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    public static String usernameToUuid(String username)
            throws IOException, UnexpectedStatusCode, URISyntaxException {
        final String url = OkapiUrlBuilder.getUsernameToUuidUrl(username);

        final HttpResponse httpResponse = httpClient.get(url);
        final String responseBody = httpResponse.getBody();

        if (httpResponse.getStatusCode() != 200) {
            final ErrorDocument okapiError = new Gson().fromJson(responseBody, ErrorDocument.class);
            if (okapiError.getParameter().equals("username")) {
                return null;
            } else {
                throw new UnexpectedStatusCode(httpResponse.getStatusCode(), responseBody);
            }
        }

        final UserDocument document = new Gson().fromJson(responseBody, UserDocument.class);
        return document.getUuid();
    }

    /**
     * Get the basic cache details for the given OC code.
     *
     * @param code The OC code to get the basic cache details for.
     * @return The geocache instance with the basic cache details.
     * @throws IOException Something went wrong with the network request.
     * @throws UnexpectedStatusCode Something went wrong with our OKAPI request.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     * @throws NumberFormatException The coordinates could not be parsed.
     */
    public static Geocache getCache(String code)
            throws IOException, UnexpectedStatusCode, URISyntaxException, NumberFormatException {
        final String url = OkapiUrlBuilder.getCacheUrl(code);

        final HttpResponse httpResponse = httpClient.get(url);
        final String responseBody = httpResponse.getBody();

        if (httpResponse.getStatusCode() != 200) {
            final ErrorDocument okapiError = new Gson().fromJson(responseBody, ErrorDocument.class);
            if (okapiError.getParameter().equals("cache_code")) {
                return null;
            } else {
                throw new UnexpectedStatusCode(httpResponse.getStatusCode(), responseBody);
            }
        }

        final GeocacheDocument document = new Gson().fromJson(responseBody, GeocacheDocument.class);
        if (document == null) {
            return null;
        }

        Coordinate coordinate = null;
        if (document.getLocation() != null) {
            final String[] parts = document.getLocation().split("\\|");
            coordinate = new Coordinate(parts[0], parts[1]);
        }

        final Geocache geocache =
                new Geocache(
                        code,
                        document.getName(),
                        coordinate,
                        document.getDifficulty(),
                        document.getTerrain(),
                        document.getType());
        geocache.setCodeGc(document.getGcCode());
        geocache.setDateHidden(document.getDateHidden());

        final String status = document.getStatus();
        if (status != null) {
            switch (status) {
                case "Archived":
                    geocache.setAvailable(false);
                    geocache.setArchived(true);
                    break;
                case "Temporarily unavailable":
                    geocache.setAvailable(false);
                    geocache.setArchived(false);
                    break;
                case "Available":
                    geocache.setAvailable(true);
                    geocache.setArchived(false);
                    break;
            }
        }

        return geocache;
    }

    /**
     * Get the opencache instance referenced by its OC code. If possible, the object will be
     * returned from the runtime cache instead of requesting it again over the network.
     *
     * @param code The OC code to get the object for.
     * @param okapiRuntimeCache The runtime cache of the OKAPI holding all the opencache objects of
     *     the current application run.
     * @return The requested opencache instance.
     * @throws IOException Something went wrong with the network request.
     * @throws UnexpectedStatusCode Something went wrong with our OKAPI request.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     * @throws NumberFormatException The geocache coordinates could not be parsed.
     */
    public static Geocache getCacheBuffered(String code, List<Geocache> okapiRuntimeCache)
            throws IOException, UnexpectedStatusCode, URISyntaxException, NumberFormatException {
        synchronized (okapiRuntimeCache) {
            final int index = Collections.binarySearch(okapiRuntimeCache, code);
            if (index >= 0) {
                return okapiRuntimeCache.get(index);
            }
        }

        final Geocache geocache = getCache(code);
        if (geocache != null) {
            synchronized (okapiRuntimeCache) {
                okapiRuntimeCache.add(geocache);
                okapiRuntimeCache.sort(Comparator.comparing(Geocache::getCode));
            }
        }
        return geocache;
    }

    /**
     * Complete the details for the given opencache instance.
     *
     * @param geocache The opencache instance to complete the cache details for.
     * @return The opencache instance with the details added.
     * @throws IOException Something went wrong with the network request.
     * @throws UnexpectedStatusCode Something went wrong with our OKAPI request.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    public static Geocache completeCacheDetails(Geocache geocache)
            throws IOException, UnexpectedStatusCode, URISyntaxException {
        final String url = OkapiUrlBuilder.getCompleteCacheDetailsUrl(geocache);

        final HttpResponse httpResponse = httpClient.get(url);
        final String responseBody = httpResponse.getBody();

        if (httpResponse.getStatusCode() != 200) {
            final ErrorDocument okapiError = new Gson().fromJson(responseBody, ErrorDocument.class);
            if (okapiError.getParameter().equals("cache_code")) {
                return null;
            } else {
                throw new UnexpectedStatusCode(httpResponse.getStatusCode(), responseBody);
            }
        }

        final GeocacheDocument document = new Gson().fromJson(responseBody, GeocacheDocument.class);

        geocache.setContainer(document.getSize());
        geocache.setListingShort(document.getShortDescription());
        geocache.setListing(document.getDescription());
        geocache.setOwner(document.getOwnerUsername());
        geocache.setHint(document.getHint());
        geocache.setUrl(document.getUrl());
        geocache.setRequiresPassword(document.doesRequirePassword());
        geocache.setInternalId(document.getInternalId());

        return geocache;
    }

    /**
     * Build the OAuth service for the OKAPI.
     *
     * @return The OAuth service to use with the OKAPI.
     */
    private static OAuth10aService getOAuthService() {
        return new ServiceBuilder(OcSite.getConsumerApiKey())
                .apiSecret(OcSite.getConsumerSecretKey())
                .build(new OAuth());
    }

    /**
     * Request OAuth authorization
     *
     * @param callback The callback interface to use.
     * @return The OAuth access token instance to use with the requests further on.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     */
    public static OAuth1AccessToken requestAuthorization(
            RequestAuthorizationCallbackInterface callback)
            throws IOException, InterruptedException, ExecutionException {
        // Step One: Create the OAuthService object
        final OAuth10aService service = getOAuthService();

        // Step Two: Get the request token
        final OAuth1RequestToken requestToken = service.getRequestToken();

        // Step Three: Making the user validate your request token
        final String authUrl = service.getAuthorizationUrl(requestToken);
        callback.redirectUrlToUser(authUrl);

        final String pin = callback.getPin();
        if (pin == null) {
            return null;
        }

        // Step Four: Get the access Token
        return service.getAccessToken(requestToken, pin);
    }

    /**
     * Perform an HTTP GET request using OAuth.
     *
     * @param tokenProvider The OAuth token provider to use.
     * @param url The URL to request.
     * @return The result of the GET request.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     */
    private static String authedHttpGet(
            final TokenProviderInterface tokenProvider, final String url)
            throws InterruptedException, ExecutionException, IOException {
        final OAuth10aService service = getOAuthService();
        final OAuthRequest request = new OAuthRequest(Verb.GET, url);

        // The access token from step 4.
        service.signRequest(tokenProvider.getOkapiToken(), request);

        final Response response = service.execute(request);
        return response.getBody();
    }

    /**
     * Get the opencaches around the center given by the geocache instance.
     *
     * @param tokenProvider The OAuth token provider to use.
     * @param excludeUuid Set to an user UUID to exclude caches ignored by this user. If this is
     *     set, the token provider cannot be null.
     * @param geocache The geocache to use the coordinates from as the search center.
     * @param searchRadius The search radius to use.
     * @param okapiRuntimeCache The runtime cache to use for faster access to opencache instances.
     * @return The list of opencaches around the given geocache.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     * @throws UnexpectedStatusCode Something went wrong with our OKAPI request.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    public static List<Geocache> getCachesAround(
            TokenProviderInterface tokenProvider,
            String excludeUuid,
            Geocache geocache,
            double searchRadius,
            List<Geocache> okapiRuntimeCache)
            throws IOException, UnexpectedStatusCode, URISyntaxException, InterruptedException,
                    ExecutionException {
        final Coordinate coordinate = geocache.getCoordinate();
        return getCachesAround(
                tokenProvider,
                excludeUuid,
                coordinate.getLatitude(),
                coordinate.getLongitude(),
                searchRadius,
                okapiRuntimeCache);
    }

    /**
     * Get the opencaches around the given center.
     *
     * @param tokenProvider The OAuth token provider to use.
     * @param excludeUuid Set to an user UUID to exclude caches ignored by this user. If this is
     *     set, the token provider cannot be null.
     * @param latitude The latitude of the search center.
     * @param longitude The longitude of the search center.
     * @param searchRadius The search radius to use.
     * @param okapiCacheDetailsCache The runtime cache to use for faster access to opencache
     *     instances.
     * @return The list of opencaches around the given geocache.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     * @throws UnexpectedStatusCode Something went wrong with our OKAPI request.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    public static List<Geocache> getCachesAround(
            TokenProviderInterface tokenProvider,
            String excludeUuid,
            Double latitude,
            Double longitude,
            Double searchRadius,
            List<Geocache> okapiCacheDetailsCache)
            throws IOException, UnexpectedStatusCode, URISyntaxException, InterruptedException,
                    ExecutionException {
        final boolean useOAuth = tokenProvider != null && excludeUuid != null;
        final String url =
                OkapiUrlBuilder.getCachesAroundUrl(
                        useOAuth, excludeUuid, latitude, longitude, searchRadius);

        String responseBody;
        if (useOAuth) {
            responseBody = authedHttpGet(tokenProvider, url);
        } else {
            final HttpResponse httpResponse = httpClient.get(url);
            responseBody = httpResponse.getBody();

            if (httpResponse.getStatusCode() != 200) {
                throw new UnexpectedStatusCode(httpResponse.getStatusCode(), responseBody);
            }
        }

        final CachesSearchNearestDocument document =
                new Gson().fromJson(responseBody, CachesSearchNearestDocument.class);
        if (document == null) {
            return null;
        }

        final List<Geocache> caches = new ArrayList<>();
        for (final String code : document.getResults()) {
            try {
                final Geocache geocache = getCacheBuffered(code, okapiCacheDetailsCache);
                if (geocache != null) {
                    caches.add(geocache);
                }
            } catch (NumberFormatException exception) {
                ExceptionPanel.display(exception);
            }
        }
        return caches;
    }

    /**
     * Retrieve the found status of the given opencache instance and update the object accordingly.
     *
     * @param tokenProvider The OAuth token provider to use.
     * @param opencache The opencache to search for and to update.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    public static void updateFoundStatus(TokenProviderInterface tokenProvider, Geocache opencache)
            throws IOException, InterruptedException, ExecutionException, URISyntaxException {
        if (tokenProvider == null) {
            return;
        }

        final String url = OkapiUrlBuilder.getFoundStatusUrl(opencache);
        final String responseBody = authedHttpGet(tokenProvider, url);

        final GeocacheDocument document = new Gson().fromJson(responseBody, GeocacheDocument.class);

        opencache.setIsFound(document.isFound());
    }

    /**
     * Get the UUID of the currently authenticated user.
     *
     * @param tokenProvider The OAuth token provider to use.
     * @return The UUID of the currently authenticated user.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    public static String getUuid(TokenProviderInterface tokenProvider)
            throws IOException, InterruptedException, ExecutionException, URISyntaxException {
        final String url = OkapiUrlBuilder.getUuidUrl();
        final String responseBody = authedHttpGet(tokenProvider, url);

        final UserDocument document = new Gson().fromJson(responseBody, UserDocument.class);
        if (document == null) {
            return null;
        }
        return document.getUuid();
    }

    /**
     * Get the username of the currently authenticated user.
     *
     * @param tokenProvider The OAuth token provider to use.
     * @return The username of the currently authenticated user.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    public static String getUsername(TokenProviderInterface tokenProvider)
            throws IOException, InterruptedException, ExecutionException, URISyntaxException {
        final String url = OkapiUrlBuilder.getUsernameUrl();
        final String responseBody = authedHttpGet(tokenProvider, url);

        final UserDocument document = new Gson().fromJson(responseBody, UserDocument.class);
        if (document == null) {
            return null;
        }
        return document.getUsername();
    }

    /**
     * Post the log for the given opencache.
     *
     * @param tokenProvider The OAuth token provider to use.
     * @param cache The opencache to post the log for.
     * @param log The log to post.
     * @param returnInternalIdInsteadOfUuid If this is true, the internal log ID will be returned
     *     instead of the log UUID.
     * @return Either the internal or the UUID of the posted log, depending on the boolean variable.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     * @throws UnexpectedStatusCode Something went wrong with our OKAPI request.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     * @throws UnexpectedLogStatus Posting the log has not been successful according to the OKAPI
     *     response.
     */
    public static String postLog(
            TokenProviderInterface tokenProvider,
            Geocache cache,
            GeocacheLog log,
            boolean returnInternalIdInsteadOfUuid)
            throws InterruptedException, ExecutionException, IOException, UnexpectedLogStatus,
                    UnexpectedStatusCode, URISyntaxException {
        String url = OkapiUrlBuilder.getLogSubmissionUrl(cache, log);

        final String responseBody = authedHttpGet(tokenProvider, url);

        // Retrieve the responseBody document.
        final LogSubmissionDocument document =
                new Gson().fromJson(responseBody, LogSubmissionDocument.class);

        // The document itself is null.
        if (document == null) {
            throw new NullPointerException(
                    "Problems with handling posted log. Response document is null.");
        }

        if (document.isSuccess() == null) {
            throw new NullPointerException(responseBody);
        }

        // Check success status.
        if (!document.isSuccess()) {
            throw new UnexpectedLogStatus(document.getMessage());
        }

        if (returnInternalIdInsteadOfUuid) {
            return Okapi.getLogId(document.getLogUuid());
        }
        return document.getLogUuid();
    }

    /**
     * Get the home coordinates for the currently authenticated user.
     *
     * @param tokenProvider The OAuth token provider to use.
     * @return The home coordinates of the currently authenticated user.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     * @throws CoordinateUnparsableException The coordinates could not be parsed.
     */
    public static Coordinate getHomeCoordinates(TokenProviderInterface tokenProvider)
            throws CoordinateUnparsableException, IOException, InterruptedException,
                    ExecutionException, URISyntaxException {
        final String uuid = getUuid(tokenProvider);

        final String url = OkapiUrlBuilder.getHomeCoordinatesUrl(uuid);
        final String responseBody = authedHttpGet(tokenProvider, url);

        final UserDocument document = new Gson().fromJson(responseBody, UserDocument.class);

        return document.getHomeLocationAsCoordinate();
    }

    /**
     * Convert the given log UUID to a real (internal) log ID.
     *
     * @param logUuid The log UUID to convert.
     * @return The real (internal) log ID for the given log UUID.
     * @throws IOException Something went wrong with the network request.
     * @throws UnexpectedStatusCode Something went wrong with our OKAPI request.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     */
    public static String getLogId(String logUuid)
            throws IOException, UnexpectedStatusCode, URISyntaxException {
        final String url = OkapiUrlBuilder.getLogIdUrl(logUuid);

        final HttpResponse httpResponse = httpClient.get(url);
        final String responseBody = httpResponse.getBody();

        if (httpResponse.getStatusCode() != 200) {
            throw new UnexpectedStatusCode(httpResponse.getStatusCode(), responseBody);
        }

        final LogDocument document = new Gson().fromJson(responseBody, LogDocument.class);
        return document.getInternalId();
    }

    /**
     * Delete the log given by its UUID.
     *
     * <p>This is only needed for the automated tests. It will probably be never implemented in the
     * GUI (and it probably should not be implemented there anyway).
     *
     * @param tokenProvider The OAuth token provider to use.
     * @param logUuid The UUID of the log to delete.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The execution has been interrupted.
     * @throws ExecutionException The result of the interrupted/aborted task should be accessed.
     * @throws URISyntaxException The URL is invalid. In theory this should never be the case for
     *     us.
     * @throws UnexpectedLogStatus Deleting the log has not been successful according to the OKAPI
     *     response.
     */
    public static void deleteLog(TokenProviderInterface tokenProvider, String logUuid)
            throws IOException, ExecutionException, InterruptedException, UnexpectedLogStatus,
                    URISyntaxException {
        String url = OkapiUrlBuilder.getLogDeletionUrl(logUuid);

        final String responseBody = authedHttpGet(tokenProvider, url);

        // Retrieve the responseBody document.
        final LogDeletionDocument document =
                new Gson().fromJson(responseBody, LogDeletionDocument.class);

        // The document itself is null.
        if (document == null) {
            throw new NullPointerException(
                    "Problems with deletion of log. Response document is null.");
        }

        if (document.isSuccess() == null) {
            throw new NullPointerException(responseBody);
        }

        // Check success status.
        if (!document.isSuccess()) {
            throw new UnexpectedLogStatus(document.getMessage());
        }
    }
}
