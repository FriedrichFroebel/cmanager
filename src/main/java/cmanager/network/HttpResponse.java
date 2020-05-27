package cmanager.network;

public class HttpResponse {

    private final Integer statusCode;
    private final String body;

    public HttpResponse(final Integer statusCode, final String body) {
        this.body = body;
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
