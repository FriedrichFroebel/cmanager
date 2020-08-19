package cmanager.okapi;

import com.github.scribejava.core.model.OAuth1AccessToken;

/** Interface for handling OKAPI access tokens. */
public interface TokenProviderInterface {

    /**
     * Get the OKAPI access token.
     *
     * @return The access token.
     */
    OAuth1AccessToken getOkapiToken();
}
