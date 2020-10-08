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

    /**
     * Set the given value for the given key.
     *
     * @param key The key of the setting to set.
     * @param value The value to set for the key.
     */
    public static void set(final SettingsKey key, final String value) {
        preferences.put(key.getNameString(), value);
    }

    /**
     * Get the string value for the given key.
     *
     * @param key The key to get the string value for.
     * @return The requested string value from the settings. This will fall back to the default
     *     value if the key has not yet been saved and therefore could not be found.
     */
    public static String getString(final SettingsKey key) {
        return preferences.get(key.getNameString(), key.getDefaultString());
    }

    /**
     * Set the given object for the given key.
     *
     * @param key The key of the setting to set.
     * @param value The object to save in serialized format.
     * @throws IOException Something went wrong with the serialization.
     */
    public static void setSerialized(final SettingsKey key, final Serializable value)
            throws IOException {
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
    public static <T extends Serializable> T getSerialized(final SettingsKey key)
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
