package cmanager.okapi.helper;

import cmanager.oc.SupportedSite;

public class SiteHelper {

    /** The currently selected site. */
    private static SupportedSite selectedSite = SupportedSite.OPENCACHING_DE;

    /**
     * Set the specified site.
     *
     * @param site The site to set.
     */
    public static void setSite(SupportedSite site) {
        selectedSite = site;
    }

    /**
     * Get the username for the currently selected site.
     *
     * @return The username for the currently selected site.
     * @throws IllegalArgumentException The currently selected site is not supported at the moment.
     */
    public static String getUsername() {
        switch (selectedSite) {
            case OPENCACHING_DE:
                return TestClientCredentials.USERNAME_DE;
            case OPENCACHING_DE_TESTING:
                return TestClientCredentials.USERNAME_DE_TESTING;
            default:
                throw new IllegalArgumentException("Unsupported OC site.");
        }
    }

    /**
     * Get the password for the currently selected site.
     *
     * @return The password for the currently selected site.
     * @throws IllegalArgumentException The currently selected site is not supported at the moment.
     */
    public static String getPassword() {
        switch (selectedSite) {
            case OPENCACHING_DE:
                return TestClientCredentials.PASSWORD_DE;
            case OPENCACHING_DE_TESTING:
                return TestClientCredentials.PASSWORD_DE_TESTING;
            default:
                throw new IllegalArgumentException("Unsupported OC site.");
        }
    }
}
