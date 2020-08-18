package cmanager.network;

/** Error message for unexpected status code. */
public class UnexpectedStatusCode extends Exception {

    private static final long serialVersionUID = -1132973286480626832L;

    /** The HTTP status code. */
    private final int statusCode;

    /** The response body. */
    private final String body;

    /**
     * Create a new instance with the given values.
     *
     * @param statusCode The status code to set.
     * @param body The body to set.
     */
    public UnexpectedStatusCode(final int statusCode, final String body) {
        super("Unexpected status code " + Integer.valueOf(statusCode).toString());
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * Get the server status code.
     *
     * @return The status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Check if the server responded with "400 Bad Request".
     *
     * @return Whether the status code is 400.
     */
    public boolean is400BadRequest() {
        return statusCode == 400;
    }

    /**
     * Get the response body.
     *
     * @return The response body.
     */
    public String getBody() {
        return body;
    }
}
