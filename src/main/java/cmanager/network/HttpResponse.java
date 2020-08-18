package cmanager.network;

/** Container for a HTTP response. */
public class HttpResponse {

    /** The HTTP status code. */
    private final Integer statusCode;

    /** The response body. */
    private final String body;

    /**
     * Create a new instance with the given values.
     *
     * @param statusCode The status code to set.
     * @param body The body to set.
     */
    public HttpResponse(final Integer statusCode, final String body) {
        this.body = body;
        this.statusCode = statusCode;
    }

    /**
     * Get the response body.
     *
     * @return The response body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Get the server status code.
     *
     * @return The status code.
     */
    public Integer getStatusCode() {
        return statusCode;
    }
}
