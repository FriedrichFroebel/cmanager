package cmanager.okapi;

import cmanager.oc.OcSite;
import com.github.scribejava.core.builder.api.DefaultApi10a;

public class OAuth extends DefaultApi10a {

    @Override
    public String getAccessTokenEndpoint() {
        return OcSite.getBaseUrl() + "okapi/services/oauth/access_token";
    }

    @Override
    public String getRequestTokenEndpoint() {
        return OcSite.getBaseUrl() + "okapi/services/oauth/request_token";
    }

    @Override
    public String getAuthorizationBaseUrl() {
        return OcSite.getBaseUrl() + "okapi/services/oauth/authorize";
    }
}
