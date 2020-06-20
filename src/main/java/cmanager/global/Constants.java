package cmanager.global;

public class Constants {

    public static final String APP_NAME = "cmanager";

    public static final String CACHE_FOLDER =
            System.getProperty("user.home") + "/." + APP_NAME + "/cache/";

    public static final String HTTP_USER_AGENT = APP_NAME + " " + Version.VERSION;
}
