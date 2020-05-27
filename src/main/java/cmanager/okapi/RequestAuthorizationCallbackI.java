package cmanager.okapi;

public interface RequestAuthorizationCallbackI {

    void redirectUrlToUser(String authUrl);

    String getPin();
}
