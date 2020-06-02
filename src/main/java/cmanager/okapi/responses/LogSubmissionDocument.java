package cmanager.okapi.responses;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Container for the logging success.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/logs/submit.html">OKAPI documentation</a>
 */
public class LogSubmissionDocument {

    /** Whether the log entry was submitted successfully or not. */
    private Boolean success;

    /**
     * Plain-text string with a message for the user, which acknowledges success or describes an
     * error.
     */
    private String message;

    /** ID of the newly created log entry or null in case of an error. */
    @SerializedName("log_uuid")
    private String logUuid;

    /** A list of the IDs of the newly created log entries or an empty list in case of an error. */
    @SerializedName("log_uuids")
    private List<String> logUuids;

    /**
     * Get the success status.
     *
     * @return The success status.
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * Get the user message.
     *
     * @return The user message.
     */
    public String getMessage() {
        return message;
    }

    public String getLogUuid() {
        return logUuid;
    }
}
