package cmanager.okapi;

/**
 * The OKAPI consumer keys.
 *
 * <p>You should never access these values directly. Please use the methods provided by the {@link
 * cmanager.oc.OcSite} class instead which automatically chooses the correct values depending on the
 * selected Opencaching site.
 */
public class ConsumerKeys {

    public static final String OC_OKAPI_DE_CONSUMER_KEY = "${oc_okapi_de_consumer_key}";
    public static final String OC_OKAPI_DE_CONSUMER_SECRET = "${oc_okapi_de_consumer_secret}";

    public static final String OC_OKAPI_DE_TESTING_CONSUMER_KEY =
            "${oc_okapi_de_testing_consumer_key}";
    public static final String OC_OKAPI_DE_TESTING_CONSUMER_SECRET =
            "${oc_okapi_de_testing_consumer_secret}";
}
