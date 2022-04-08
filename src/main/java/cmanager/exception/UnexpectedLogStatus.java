package cmanager.exception;

/** Error message for an unexpected log submission status. */
public class UnexpectedLogStatus extends Exception {

    private static final long serialVersionUID = -1132973286480626832L;

    /** The user message from the log submission. */
    private final String responseMessage;

    /**
     * @param responseMessage The user message from the log submission.
     */
    public UnexpectedLogStatus(String responseMessage) {
        super("Unexpected log status");
        this.responseMessage = responseMessage;
    }

    /**
     * Get the message to show to the user.
     *
     * @return The message to show to the user.
     */
    public String getResponseMessage() {
        return responseMessage;
    }
}
