package cmanager.network;

import cmanager.global.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.NameValuePair;

/** Simple wrapper around the Apache HTTP functionality. */
public class ApacheHttp {

    /** The HTTP client to use. */
    private final CloseableHttpClient httpClient;

    /** Create a new instance, including the configured proxy data. */
    public ApacheHttp() {
        if (System.getProperty("https.proxyHost") != null) {
            // Use proxy configuration.
            final HttpHost httpsProxy =
                    new HttpHost(
                            System.getProperty("https.proxyHost"),
                            Integer.parseInt(System.getProperty("https.proxyPort")));
            final DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpsProxy);
            httpClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
        } else {
            // Use no proxy.
            httpClient = HttpClients.createDefault();
        }
    }

    /**
     * Perform a HTTP GET request.
     *
     * @param url The URL to perform the request against.
     * @return The server response.
     * @throws IOException Something went wrong with the request.
     */
    public HttpResponse get(final String url) throws IOException {
        // Perform the request with our custom user agent.
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HttpHeaders.USER_AGENT, Constants.HTTP_USER_AGENT);
        final HttpResponse httpResponse =
                httpClient.execute(
                        httpGet,
                        response -> {
                            return handleResponse(response);
                        });

        return httpResponse;
    }

    /**
     * Perform a HTTP POST request.
     *
     * @param url The URL to perform the request against.
     * @param nameValuePairs The parameters to pass inside the body.
     * @return The server response.
     * @throws IOException Something went wrong with the request.
     */
    public HttpResponse post(final String url, final List<NameValuePair> nameValuePairs)
            throws IOException {
        // Perform the request with our custom user agent.
        // The parameters will be encoded using UTF-8.
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HttpHeaders.USER_AGENT, Constants.HTTP_USER_AGENT);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, StandardCharsets.UTF_8));
        final HttpResponse httpResponse =
                httpClient.execute(
                        httpPost,
                        response -> {
                            return handleResponse(response);
                        });

        return httpResponse;
    }

    /**
     * Handle the given response.
     *
     * <p>This will close the response at the end.
     *
     * @param response The response to handle.
     * @return The "parsed" response.
     * @throws IOException Something went wrong with the handling.
     */
    private HttpResponse handleResponse(ClassicHttpResponse response) throws IOException {
        int statusCode;
        final StringBuilder http = new StringBuilder();
        try {
            statusCode = response.getCode();

            BufferedReader bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    response.getEntity().getContent(), StandardCharsets.UTF_8));

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                http.append(inputLine);
            }
            bufferedReader.close();
        } finally {
            response.close();
        }

        return new HttpResponse(statusCode, http.toString());
    }
}
