package cmanager.okapi;

import com.github.scribejava.core.model.OAuth1AccessToken;

public interface TokenProviderInterface {
    OAuth1AccessToken getOkapiToken();
}
