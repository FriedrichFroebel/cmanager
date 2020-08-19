package cmanager.okapi;

import cmanager.oc.OcSite;
import com.github.scribejava.core.builder.api.DefaultApi10a;

/** OAuth 1.0 API endpoints. */
public class OAuth extends DefaultApi10a {

    /**
     * Get the URL that receives the access token requests.
     *
     * @return The URL receiving the access token requests.
     */
    @Override
    public String getAccessTokenEndpoint() {
        return OcSite.getBaseUrl() + "okapi/services/oauth/access_token";
    }

    /**
     * Get the URL that receives the request token requests.
     *
     * @return The URL receiving the request token requests.
     */
    @Override
    public String getRequestTokenEndpoint() {
        return OcSite.getBaseUrl() + "okapi/services/oauth/request_token";
    }

    /**
     * Get the URL where to redirect the user to authenticate this application.
     *
     * @return The URL where to redirect the user for authenticating this application.
     */
    @Override
    public String getAuthorizationBaseUrl() {
        return OcSite.getBaseUrl() + "okapi/services/oauth/authorize";
    }
}
