package cmanager.okapi.responses;

/**
 * Container for deleting logs.
 *
 * @see <a href="https://www.opencaching.de/okapi/services/logs/delete.html">OKAPI documentation</a>
 */
public class LogDeletionDocument {

    /** Whether the log entry was deleted successfully or not. */
    private Boolean success;

    /**
     * Plain-text string with a message for the user, which acknowledges success or describes an
     * error.
     */
    private String message;

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
}
