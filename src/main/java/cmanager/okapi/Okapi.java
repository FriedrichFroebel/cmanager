package cmanager.okapi;

import cmanager.exception.CoordinateUnparsableException;
import cmanager.exception.MalFormedException;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Okapi {

    private static final ApacheHttp httpClient = new ApacheHttp();

    public static String usernameToUuid(String username) throws Exception {
        final String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/users/by_username"
                        + "?consumer_key="
                        + OcSite.getConsumerApiKey()
                        + "&username="
                        + URLEncoder.encode(username, "UTF-8")
                        + "&fields=uuid";

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

    public static Geocache getCache(String code) throws Exception {
        final String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/caches/geocache"
                        + "?consumer_key="
                        + OcSite.getConsumerApiKey()
                        + "&cache_code="
                        + code
                        + "&fields="
                        + URLEncoder.encode(
                                "code|name|location|type|gc_code|difficulty|terrain|status",
                                "UTF-8");

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

    public static Geocache getCacheBuffered(String code, List<Geocache> okapiRuntimeCache)
            throws Exception {
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

    public static Geocache completeCacheDetails(Geocache geocache) throws Exception {
        final String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/caches/geocache"
                        + "?consumer_key="
                        + OcSite.getConsumerApiKey()
                        + "&cache_code="
                        + geocache.getCode()
                        + "&fields="
                        + URLEncoder.encode(
                                "size2|short_description|description|owner|hint2|url|req_passwd"
                                        + "|internal_id",
                                "UTF-8");

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

    private static OAuth10aService getOAuthService() {
        return new ServiceBuilder(OcSite.getConsumerApiKey())
                .apiSecret(OcSite.getConsumerSecretKey())
                .build(new OAuth());
    }

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

    public static List<Geocache> getCachesAround(
            TokenProviderInterface tokenProvider,
            String excludeUuid,
            Geocache geocache,
            double searchRadius,
            List<Geocache> okapiRuntimeCache)
            throws Exception {
        final Coordinate coordinate = geocache.getCoordinate();
        return getCachesAround(
                tokenProvider,
                excludeUuid,
                coordinate.getLatitude(),
                coordinate.getLongitude(),
                searchRadius,
                okapiRuntimeCache);
    }

    public static List<Geocache> getCachesAround(
            TokenProviderInterface tokenProvider,
            String excludeUuid,
            Double latitude,
            Double longitude,
            Double searchRadius,
            List<Geocache> okapiCacheDetailsCache)
            throws Exception {
        final boolean useOAuth = tokenProvider != null && excludeUuid != null;
        final String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/caches/search/nearest"
                        + "?consumer_key="
                        + OcSite.getConsumerApiKey()
                        + "&center="
                        + URLEncoder.encode(
                                latitude.toString() + "|" + longitude.toString(), "UTF-8")
                        + "&radius="
                        + searchRadius.toString()
                        + "&status="
                        + URLEncoder.encode("Available|Temporarily unavailable|Archived", "UTF-8")
                        + "&limit=500"
                        + (useOAuth ? "&ignored_status=notignored_only" : "")
                        + (useOAuth ? "&not_found_by=" + excludeUuid : "");

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
            } catch (MalFormedException exception) {
                ExceptionPanel.display(exception);
            }
        }
        return caches;
    }

    public static void updateFoundStatus(TokenProviderInterface tokenProvider, Geocache oc)
            throws IOException, InterruptedException, ExecutionException {
        if (tokenProvider == null) {
            return;
        }

        final String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/caches/geocache"
                        + "?consumer_key="
                        + OcSite.getConsumerApiKey()
                        + "&cache_code="
                        + oc.getCode()
                        + "&fields=is_found";
        final String responseBody = authedHttpGet(tokenProvider, url);

        final GeocacheDocument document = new Gson().fromJson(responseBody, GeocacheDocument.class);

        oc.setIsFound(document.isFound());
    }

    public static String getUuid(TokenProviderInterface tokenProvider)
            throws IOException, InterruptedException, ExecutionException {
        final String url = OcSite.getBaseUrl() + "okapi/services" + "/users/user" + "?fields=uuid";
        final String responseBody = authedHttpGet(tokenProvider, url);

        final UserDocument document = new Gson().fromJson(responseBody, UserDocument.class);
        if (document == null) {
            return null;
        }
        return document.getUuid();
    }

    public static String getUsername(TokenProviderInterface tokenProvider)
            throws IOException, InterruptedException, ExecutionException {
        final String url =
                OcSite.getBaseUrl() + "okapi/services" + "/users/user" + "?fields=username";
        final String responseBody = authedHttpGet(tokenProvider, url);

        final UserDocument document = new Gson().fromJson(responseBody, UserDocument.class);
        if (document == null) {
            return null;
        }
        return document.getUsername();
    }

    public static String postLog(
            TokenProviderInterface tokenProvider,
            Geocache cache,
            GeocacheLog log,
            boolean returnInternalIdInsteadOfUuid)
            throws InterruptedException, ExecutionException, IOException, UnexpectedLogStatus,
                    UnexpectedStatusCode {
        String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/logs/submit"
                        + "?cache_code="
                        + URLEncoder.encode(cache.getCode(), "UTF-8")
                        + "&logtype="
                        + URLEncoder.encode(log.getOkapiType(cache), "UTF-8")
                        + "&comment="
                        + URLEncoder.encode(log.getText(), "UTF-8")
                        + "&when="
                        + URLEncoder.encode(log.getDateStrIso8601NoTime(), "UTF-8");

        /*if (cache.doesRequirePassword() != null && cache.doesRequirePassword()) {
            url += "&password=" + URLEncoder.encode(log.getPassword(), "UTF-8");
        }*/

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

    public static Coordinate getHomeCoordinates(TokenProviderInterface tokenProvider)
            throws CoordinateUnparsableException, IOException, InterruptedException,
                    ExecutionException {
        final String uuid = getUuid(tokenProvider);

        final String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/users/user"
                        + "?fields=home_location"
                        + "&user_uuid="
                        + uuid;
        final String responseBody = authedHttpGet(tokenProvider, url);

        final UserDocument document = new Gson().fromJson(responseBody, UserDocument.class);

        return document.getHomeLocationAsCoordinate();
    }

    /** Convert the given log UUID to a real (internal) log ID. */
    public static String getLogId(String logUuid) throws IOException, UnexpectedStatusCode {
        final String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/logs/entry?consumer_key="
                        + OcSite.getConsumerApiKey()
                        + "&fields=internal_id"
                        + "&log_uuid="
                        + URLEncoder.encode(logUuid, "UTF-8");

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
     */
    public static void deleteLog(TokenProviderInterface tokenProvider, String logUuid)
            throws IOException, ExecutionException, InterruptedException, UnexpectedLogStatus {
        String url =
                OcSite.getBaseUrl()
                        + "okapi/services"
                        + "/logs/delete"
                        + "?log_uuid="
                        + URLEncoder.encode(logUuid, "UTF-8");

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
