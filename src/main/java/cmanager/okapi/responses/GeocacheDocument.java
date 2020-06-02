package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Container for the details of a specific geocache.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/caches/geocache.html">OKAPI
 *     documentation</a>
 */
public class GeocacheDocument {

    /** Unique Opencaching code of the geocache. */
    private String code;

    /** Name of the geocache. */
    private String name;

    /**
     * Location of the cache in the "latitude|longitude" format with latitude and longitude being in
     * full degrees with a dot as a decimal point.
     */
    private String location;

    /** Cache type. */
    private String type;

    /**
     * Geocaching.com code (GC code) of the geocache or null if the cache is not listed on GC or the
     * GC code is unknown. This information is supplied by the cache owner and may be missing,
     * obsolete or otherwise incorrect.
     */
    @SerializedName("gc_code")
    private String gcCode;

    /** Difficulty rating of the cache. */
    private Double difficulty;

    /** Terrain rating of the cache. */
    private Double terrain;

    /** Cache status, Can be "Available", "Temporarily unavailable" or "Archived". */
    private String status;

    /** String indicating the size of the container. */
    String size2;

    /** User fields. */
    UserDocument owner;

    /** A plaintext string with a single line (very short) description of the cache. */
    String short_description;

    /** HTML string, description of the cache. Includes some attribution notice. */
    String description;

    /** Plain-text string, cache hints/spoilers. */
    String hint2;

    /** The URL of the cache's web page. */
    String url;

    /**
     * The internal ID of the cache.
     *
     * <p>Although it is not recommended to use this, it seems like we need this for log
     * hyperlinking.
     */
    @SerializedName("internal_id")
    String internalId;

    /** State if this cache requires a password in order to submit a "Found it" log entry. */
    @SerializedName("req_passwd")
    Boolean requiresPassword;

    /** Whether the user has already found this cache. */
    @SerializedName("is_found")
    private boolean found;

    /**
     * Get the OC code.
     *
     * @return The OC code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the location string.
     *
     * @return The location string.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Get the type.
     *
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the GC code.
     *
     * @return The GC code.
     */
    public String getGcCode() {
        return gcCode;
    }

    /**
     * Get the difficulty rating.
     *
     * @return The difficulty rating.
     */
    public Double getDifficulty() {
        return difficulty;
    }

    /**
     * Get the terrain rating.
     *
     * @return The terrain rating.
     */
    public Double getTerrain() {
        return terrain;
    }

    /**
     * Get the status string.
     *
     * @return The status string.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Get the name of the user.
     *
     * @return The name of the user.
     */
    public String getOwnerUsername() {
        return owner.username;
    }

    /**
     * Get the cache size.
     *
     * @return The cache size.
     */
    public String getSize() {
        return size2;
    }

    /**
     * Get the owner instance.
     *
     * @return The owner instance.
     */
    public UserDocument getOwner() {
        return owner;
    }

    /**
     * Get the short description.
     *
     * @return The short description.
     */
    public String getShortDescription() {
        return short_description;
    }

    /**
     * Get the (full) description.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the hint.
     *
     * @return The hint.
     */
    public String getHint() {
        return hint2;
    }

    /**
     * Get the URL.
     *
     * @return The URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Get the internal ID.
     *
     * @return The internal ID.
     */
    public String getInternalId() {
        return internalId;
    }

    /**
     * Get the password requirement status.
     *
     * @return Whether a password is required or not.
     */
    public Boolean doesRequirePassword() {
        return requiresPassword;
    }

    /**
     * Check whether the user has already found this cache.
     *
     * @return Whether the user has already found this cache.
     */
    public boolean isFound() {
        return found;
    }
}
