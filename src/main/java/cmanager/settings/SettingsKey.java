package cmanager.settings;

/** The available settings. */
public enum SettingsKey {
    /** The size of the heap to use (in MB). */
    HEAP_SIZE("javaHeapSize", null),

    /** The username for GC. */
    GC_USERNAME("gcUsername", ""),

    /** The username for OC. */
    OC_USERNAME("ocUsername", null),

    /** The last directory used for loading the GPX file. */
    FILE_CHOOSER_LOAD_GPX("fileChooserGpx", ""),

    /** The OKAPI token of the user. */
    OKAPI_TOKEN("okapiToken", null),

    /** The OKAPI token secret of the user. */
    OKAPI_TOKEN_SECRET("okapiTokenSecret", null),

    /** The list of configured locations. */
    LOCATION_LIST("locationList", null),

    /** The cache list controller instances. */
    CLC_LIST("clcList", null);

    /** The name of the key as a string. */
    private final String nameString;

    /**
     * The default string for this key.
     *
     * <p>This is either an empty string or <code>null</code>.
     */
    private final String defaultString;

    /**
     * Create a new key.
     *
     * @param nameString The name to set.
     * @param defaultString The default string to set.
     */
    SettingsKey(final String nameString, final String defaultString) {
        this.nameString = nameString;
        this.defaultString = defaultString;
    }

    /**
     * Get the name for the key.
     *
     * @return The name for the current key.
     */
    public String getNameString() {
        return nameString;
    }

    /**
     * Get the default string value for the key.
     *
     * @return The default string value for the current key.
     */
    public String getDefaultString() {
        return defaultString;
    }
}
