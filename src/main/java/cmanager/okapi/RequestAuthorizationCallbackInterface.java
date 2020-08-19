package cmanager.okapi;

/** Callback for requesting OKAPI authorization from the user. */
public interface RequestAuthorizationCallbackInterface {

    /**
     * Redirect the user to the given URL.
     *
     * @param authUrl The URL to redirect the user to.
     */
    void redirectUrlToUser(String authUrl);

    /**
     * Get the PIN entered by the user.
     *
     * @return The PIN entered by the user.
     */
    String getPin();
}
