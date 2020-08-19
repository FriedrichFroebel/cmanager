package cmanager.okapi;

import cmanager.settings.Settings;
import com.github.scribejava.core.model.OAuth1AccessToken;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** An OKAPI user required for authenticating against the OKAPI. */
public class User implements TokenProviderInterface {

    /** The current user instance. */
    private static User user = null;

    /**
     * Get the current OKAPI user.
     *
     * @return The current OKAPI user.
     */
    public static User getOkapiUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    /** The access token to use. */
    private OAuth1AccessToken okapiAccessToken;

    /**
     * Create a new instance.
     *
     * <p>This will attempt to load the token data from the settings.
     */
    private User() {
        try {
            okapiAccessToken =
                    new OAuth1AccessToken(
                            Settings.getString(Settings.Key.OKAPI_TOKEN),
                            Settings.getString(Settings.Key.OKAPI_TOKEN_SECRET));
        } catch (IllegalArgumentException e) {
            okapiAccessToken = null;
        }
    }

    /**
     * Get the access token.
     *
     * @return The access token.
     */
    public OAuth1AccessToken getOkapiToken() {
        return okapiAccessToken;
    }

    /**
     * Request an access token and save it if this has been successful.
     *
     * @param callback The callback interface to use for retrieving the token.
     * @throws IOException Something went wrong with the network request.
     * @throws InterruptedException The request has been interrupted.
     * @throws ExecutionException Something went wrong with the execution.
     */
    public void requestOkapiToken(RequestAuthorizationCallbackInterface callback)
            throws IOException, InterruptedException, ExecutionException {
        okapiAccessToken = Okapi.requestAuthorization(callback);

        if (okapiAccessToken != null) {
            Settings.set(Settings.Key.OKAPI_TOKEN, okapiAccessToken.getToken());
            Settings.set(Settings.Key.OKAPI_TOKEN_SECRET, okapiAccessToken.getTokenSecret());
        }
    }
}
