package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Container for information on a single log entry.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/logs/entry.html">OKAPI documentation</a>
 */
public class LogDocument {

    /**
     * The internal ID of the log entry.
     *
     * <p>Although it is not recommended to use this, it seems like we need this for log
     * hyperlinking.
     */
    @SerializedName("internal_id")
    String internalId;

    /**
     * Get the internal ID.
     *
     * @return The internal ID.
     */
    public String getInternalId() {
        return internalId;
    }
}
