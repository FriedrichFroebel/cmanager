package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Container for the information on a single log entry.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/logs/entry.html">OKAPI documentation</a>
 */
public class LogDocument {

    @SerializedName("internal_id")
    String internalId;

    public String getInternalId() {
        return internalId;
    }
}
