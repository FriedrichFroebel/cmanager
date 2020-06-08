package cmanager.okapi;

public interface RequestAuthorizationCallbackInterface {

    void redirectUrlToUser(String authUrl);

    String getPin();
}
