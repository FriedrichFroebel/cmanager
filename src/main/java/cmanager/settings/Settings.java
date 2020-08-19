package cmanager.settings;

import cmanager.global.Constants;
import cmanager.util.FileHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.prefs.Preferences;
import org.apache.commons.codec.binary.Base64;

/** Handle persistent settings. */
public class Settings {

    /** The preferences to use for the settings. */
    private static final Preferences preferences = Preferences.userRoot().node(Constants.APP_NAME);

    /** The available settings. */
    // TODO: Move this to an own file.
    public enum Key {
        /** The size of the heap to use (in MB). */
        HEAP_SIZE,

        /** The username for GC. */
        GC_USERNAME,

        /** The username for OC. */
        OC_USERNAME,

        /** The last directory used for loading the GPX file. */
        FILE_CHOOSER_LOAD_GPX,

        /** The OKAPI token of the user. */
        OKAPI_TOKEN,

        /** The OKAPI token secret of the user. */
        OKAPI_TOKEN_SECRET,

        /** The list of configured locations. */
        LOCATION_LIST,

        /** The cache list controller instances. */
        CLC_LIST
    }

    /**
     * Get the name for the given key.
     *
     * @param key The key to get the name for.
     * @return The name for the given key.
     */
    // TODO: Move this into the enumeration.
    public static String key(final Key key) {
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

    /**
     * Get the default string value for the given key.
     *
     * @param key The key to get the default string value for.
     * @return The default string value for the given key.
     */
    // TODO: Move this into the enumeration.
    public static String getDefaultString(final Key key) {
        switch (key) {
            case GC_USERNAME:
            case FILE_CHOOSER_LOAD_GPX:
                return "";

            default:
                return null;
        }
    }

    /**
     * Set the given value for the given key.
     *
     * @param key The key of the setting to set.
     * @param value The value to set for the key.
     */
    public static void set(final Key key, final String value) {
        preferences.put(key(key), value);
    }

    /**
     * Get the string value for the given key.
     *
     * @param key The key to get the string value for.
     * @return The requested string value from the settings. This will fall back to the default
     *     value if the key has not yet been saved and therefore could not be found.
     */
    public static String getString(final Key key) {
        return preferences.get(key(key), getDefaultString(key));
    }

    /**
     * Set the given object for the given key.
     *
     * @param key The key of the setting to set.
     * @param value The object to save in serialized format.
     * @throws IOException Something went wrong with the serialization.
     */
    public static void setSerialized(final Key key, final Serializable value) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileHelper.serialize(value, byteArrayOutputStream);
        final byte[] bytes = byteArrayOutputStream.toByteArray();

        final String base64 = Base64.encodeBase64String(bytes);
        Settings.set(key, base64);
    }

    /**
     * Get the object for the given key.
     *
     * @param key The key to get the object for.
     * @return The requested object from the settings. This will return <code>null</code> if the key
     *     has not yet been saved and therefore could not be found.
     * @throws ClassNotFoundException The corresponding class to deserialize the data could not be
     *     found.
     * @throws IOException Something went wrong with the deserialization.
     */
    public static <T extends Serializable> T getSerialized(final Key key)
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
