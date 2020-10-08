package cmanager.okapi.helper;

/**
 * The credentials for testing purposes
 *
 * <p>You should never access these values directly. Please use the methods provided by the {@link
 * cmanager.okapi.helper.SiteHelper} class from the <code>test</code> directory instead which
 * automatically chooses the correct values depending on the selected Opencaching site.
 */
public class TestClientCredentials {

    /** The username to use for the German Opencaching site. */
    public static final String USERNAME_DE = "${oc_de_test_client_username}";

    /** The password to use for the German Opencaching site. */
    public static final String PASSWORD_DE = "${oc_de_test_client_password}";

    /** The username to use for the German Opencaching testing site. */
    public static final String USERNAME_DE_TESTING = "${oc_de_testing_test_client_username}";

    /** The password to use for the German Opencaching testing site. */
    public static final String PASSWORD_DE_TESTING = "${oc_de_testing_test_client_password}";
}
