package cmanager.network;

import cmanager.global.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

public class ApacheHttp {

    private final CloseableHttpClient httpClient;
    
    public ApacheHttp() {
        if (System.getProperty("https.proxyHost") != null) {
            HttpHost httpsProxy = new HttpHost(System.getProperty("https.proxyHost"), Integer.parseInt(System.getProperty("https.proxyPort")));
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(httpsProxy);
            httpClient = HttpClients.custom().setRoutePlanner(routePlanner).build();
        } else {
            httpClient = HttpClients.createDefault();
        }
    }

    public static class HttpResponse {

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

    // HTTP GET request
    public HttpResponse get(String url) throws IOException {
        final HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HttpHeaders.USER_AGENT, Constants.HTTP_USER_AGENT);
        final CloseableHttpResponse response = httpClient.execute(httpGet);

        int statusCode;
        final StringBuilder http = new StringBuilder();
        try {
            statusCode = response.getStatusLine().getStatusCode();

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

    // HTTP POST request
    public HttpResponse post(String url, List<NameValuePair> nvps) throws IOException {
        final HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader(HttpHeaders.USER_AGENT, Constants.HTTP_USER_AGENT);
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        final CloseableHttpResponse response = httpClient.execute(httpPost);

        int statusCode;
        final StringBuilder http = new StringBuilder();
        try {
            statusCode = response.getStatusLine().getStatusCode();

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
