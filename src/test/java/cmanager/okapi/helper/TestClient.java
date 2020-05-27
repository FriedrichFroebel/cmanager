package cmanager.okapi.helper;

import cmanager.network.ApacheHttp;
import cmanager.network.UnexpectedStatusCode;
import cmanager.okapi.Okapi;
import cmanager.okapi.Okapi.TokenProviderI;
import cmanager.okapi.RequestAuthorizationCallbackI;
import com.github.scribejava.core.model.OAuth1AccessToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/** Client implementation for the OAuth-based tests. */
public class TestClient implements TokenProviderI {

    /** The networking instance to use for our requests. */
    private final ApacheHttp http = new ApacheHttp();

    /** The OAuth token to use. */
    private OAuth1AccessToken token = null;

    /**
     * Login into the Opencaching.de site.
     *
     * @return Whether the login worked or not.
     * @throws UnexpectedStatusCode The status code of the response is not 200, so there seems to be
     *     a problem.
     * @throws IOException Something I/O-related failed - maybe the internet connection is not
     *     working.
     */
    public boolean login() throws UnexpectedStatusCode, IOException {
        final List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("email", TestClientCredentials.USERNAME));
        nvps.add(new BasicNameValuePair("password", TestClientCredentials.PASSWORD));
        nvps.add(new BasicNameValuePair("action", "login"));
        http.post("https://www.opencaching.de/login.php", nvps);

        final String response = http.get("https://www.opencaching.de/index.php").getBody();
        return response.contains(TestClientCredentials.USERNAME);
    }

    /**
     * Extract the requested parameter from the given URL.
     *
     * @param url The URL to extract the parameter from.
     * @param parameter The name of the parameter to extract.
     * @return The extracted parameter or an empty string if the parameter has not been found.
     */
    private String extractParameter(final String url, final String parameter) {
        final String parameters = url.split("\\?")[1];
        final String[] pairs = parameters.split("&");
        for (final String pair : pairs) {
            final String[] split = pair.split("=");
            if (split[0].equals(parameter)) {
                return split[1];
            }
        }
        return "";
    }

    /**
     * Request the OAuth access token from the Opencaching.de site. This is an automated way of
     * getting the required PIN without the user having to manually do anything (at least if
     * everything works fine).
     *
     * @return The access token to use.
     * @throws IOException Something I/O-related failed - maybe the internet connection is not
     *     working.
     * @throws InterruptedException The request has been interrupted.
     * @throws ExecutionException Something went wrong with the execution.
     */
    public OAuth1AccessToken requestToken()
            throws IOException, InterruptedException, ExecutionException {
        final OAuth1AccessToken token =
                Okapi.requestAuthorization(
                        new RequestAuthorizationCallbackI() {
                            private String pin = null;

                            @Override
                            public void redirectUrlToUser(String authUrl) {
                                // Determine the URL to use for the request.
                                final String oauth_token = extractParameter(authUrl, "oauth_token");
                                final String url =
                                        "https://www.opencaching.de/okapi/apps/authorize?interactivity=minimal&oauth_token="
                                                + oauth_token;

                                // Open the authorization page.
                                String response;
                                try {
                                    response = http.get(url).getBody();
                                } catch (IOException e) {
                                    return;
                                }

                                // If this application has not been authorized before, we will get a
                                // form where we have to accept that the application will have
                                // access to our account.
                                // Previously this assumed that the application has been registered
                                // manually beforehand, which in fact should nearly always be the
                                // case. So we are sending the required POST request here manually
                                // if needed.
                                if (response.contains(
                                        "<form id='authform' method='POST' class='form'>")) {
                                    final List<NameValuePair> parameters = new ArrayList<>(3);
                                    /*parameters.add(
                                            new BasicNameValuePair("interactivity", "minimal"));
                                    parameters.add(
                                            new BasicNameValuePair("oauth_token", oauth_token));*/
                                    parameters.add(
                                            new BasicNameValuePair(
                                                    "authorization_result", "granted"));
                                    try {
                                        response = http.post(url, parameters).getBody();
                                    } catch (IOException exception) {
                                        return;
                                    }
                                }

                                // Retrieve the PIN value from the web page.
                                final Matcher matcher =
                                        Pattern.compile("<div class='pin'>(\\d*)</div>")
                                                .matcher(response);
                                final boolean success = matcher.find();
                                if (!success) {
                                    return;
                                }
                                pin = matcher.group(1);
                            }

                            @Override
                            public String getPin() {
                                return pin;
                            }
                        });

        this.token = token;
        return token;
    }

    /**
     * Get the token.
     *
     * @return The token.
     */
    @Override
    public OAuth1AccessToken getOkapiToken() {
        return token;
    }
}
