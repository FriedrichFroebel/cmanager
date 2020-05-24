package cmanager.okapi;

import cmanager.okapi.Okapi.RequestAuthorizationCallbackI;
import cmanager.okapi.Okapi.TokenProviderI;
import cmanager.settings.Settings;
import com.github.scribejava.core.model.OAuth1AccessToken;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class User implements TokenProviderI {

    private static User user = null;

    public static User getOKAPIUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    private OAuth1AccessToken okapiAccessToken;

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

    public OAuth1AccessToken getOkapiToken() {
        return okapiAccessToken;
    }

    public void requestOkapiToken(RequestAuthorizationCallbackI callback)
            throws IOException, InterruptedException, ExecutionException {
        okapiAccessToken = Okapi.requestAuthorization(callback);
        if (okapiAccessToken != null) {
            Settings.set(Settings.Key.OKAPI_TOKEN, okapiAccessToken.getToken());
            Settings.set(Settings.Key.OKAPI_TOKEN_SECRET, okapiAccessToken.getTokenSecret());
        }
    }
}
