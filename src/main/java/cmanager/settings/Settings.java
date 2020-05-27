package cmanager.settings;

import cmanager.global.Constants;
import cmanager.util.FileHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.prefs.Preferences;
import org.apache.commons.codec.binary.Base64;

public class Settings {

    private static final Preferences prefs = Preferences.userRoot().node(Constants.APP_NAME);

    public enum Key {
        HEAP_SIZE,

        GC_USERNAME,
        OC_USERNAME,
        FILE_CHOOSER_LOAD_GPX,

        OKAPI_TOKEN,
        OKAPI_TOKEN_SECRET,

        LOCATION_LIST,

        CLC_LIST
    }

    public static String key(Key key) {
        switch (key) {
            case HEAP_SIZE:
                return "javaHeapSize";
            case GC_USERNAME:
                return "gcUsername";
            case OC_USERNAME:
                return "ocUsername";
            case FILE_CHOOSER_LOAD_GPX:
                return "fileChooserGpx";
            case OKAPI_TOKEN:
                return "okapiToken";
            case OKAPI_TOKEN_SECRET:
                return "okapiTokenSecret";
            case LOCATION_LIST:
                return "locationList";
            case CLC_LIST:
                return "clcList";

            default:
                return null;
        }
    }

    public static String getDefaultString(Key key) {
        switch (key) {
            case GC_USERNAME:
            case FILE_CHOOSER_LOAD_GPX:
                return "";

            default:
                return null;
        }
    }

    public static void set(Key key, String val) {
        prefs.put(key(key), val);
    }

    public static String getString(Key key) {
        return prefs.get(key(key), getDefaultString(key));
    }

    public static void setSerialized(Key key, Serializable value) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileHelper.serialize(value, byteArrayOutputStream);
        final byte[] bytes = byteArrayOutputStream.toByteArray();

        final String base64 = Base64.encodeBase64String(bytes);
        Settings.set(key, base64);
    }

    public static <T extends Serializable> T getSerialized(Key key)
            throws ClassNotFoundException, IOException {
        final String base64 = Settings.getString(key);

        if (base64 == null) {
            return null;
        }

        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(Base64.decodeBase64(base64));
        return FileHelper.deserialize(byteArrayInputStream);
    }
}
