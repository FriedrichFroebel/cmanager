package cmanager.global;

/** Some static application-wide constants. */
public class Constants {

    /** The name of the application. */
    public static final String APP_NAME = "cmanager";

    /**
     * The directory to use for caching data.
     *
     * <p>This corresponds to `~/.cmanager/cache/`.
     */
    public static final String CACHE_FOLDER =
            System.getProperty("user.home") + "/." + APP_NAME + "/cache/";

    /**
     * The user agent to use for HTTP requests.
     *
     * <p>This consists of the the application name and version.
     */
    public static final String HTTP_USER_AGENT = APP_NAME + " " + Version.VERSION;
}
