package cmanager.oc;

import cmanager.okapi.ConsumerKeys;

/**
 * Simplify access to the parameters of the current Opencaching site.
 *
 * <p>This should make it easier to handle different sites in parallel. For now this is intended for
 * testing only, when OKAPI write operations (like posting logs) should be tested, but this may
 * change in a future version.
 */
public class OcSite {

    /**
     * The currently selected site.
     *
     * <p>This is the German Opencaching site by default, as this is the only site supported by the
     * GUI for now.
     */
    private static SupportedSite selectedSite = SupportedSite.OPENCACHING_DE;

    /**
     * Set the specified site.
     *
     * <p>This is intended for testing write operations with the OKAPI for now to use a test server
     * there, but this may change later on.
     *
     * @param site The site to set.
     */
    public static void setSite(SupportedSite site) {
        selectedSite = site;
    }

    /**
     * Get the base URL for the currently selected site. This will contain a trailing slash.
     *
     * @return The base URL for the currently selected site, including a trailing slash.
     * @throws IllegalArgumentException The currently selected site is not supported at the moment.
     */
    public static String getBaseUrl() {
        switch (selectedSite) {
            case OPENCACHING_DE:
                return "https://www.opencaching.de/";
            case OPENCACHING_DE_TESTING:
                return "https://test.opencaching.de/";
            default:
                throw new IllegalArgumentException("Unsupported OC site.");
        }
    }

    /**
     * Get the OKAPI consumer key for the currently selected site.
     *
     * @return The OKAPI consumer key for the currently selected site.
     * @throws IllegalArgumentException The currently selected site is not supported at the moment.
     */
    public static String getConsumerApiKey() {
        switch (selectedSite) {
            case OPENCACHING_DE:
                return ConsumerKeys.OC_OKAPI_DE_CONSUMER_KEY;
            case OPENCACHING_DE_TESTING:
                return ConsumerKeys.OC_OKAPI_DE_TESTING_CONSUMER_KEY;
            default:
                throw new IllegalArgumentException("Unsupported OC site.");
        }
    }

    /**
     * Get the OKAPI consumer secret key for the currently selected site.
     *
     * @return The OKAPI consumer secret key for the currently selected site.
     * @throws IllegalArgumentException The currently selected site is not supported at the moment.
     */
    public static String getConsumerSecretKey() {
        switch (selectedSite) {
            case OPENCACHING_DE:
                return ConsumerKeys.OC_OKAPI_DE_CONSUMER_SECRET;
            case OPENCACHING_DE_TESTING:
                return ConsumerKeys.OC_OKAPI_DE_TESTING_CONSUMER_SECRET;
            default:
                throw new IllegalArgumentException("Unsupported OC site.");
        }
    }
}
