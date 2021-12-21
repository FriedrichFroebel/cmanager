package cmanager.network;

import cmanager.global.Constants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/** HTTP requests with multiple retries. */
public class Http {

    /**
     * Perform a HTTP GET request.
     *
     * <p>This will use up to 3 connection attempts.
     *
     * @param url The URL to perform the request against.
     * @return The connection of the response.
     * @throws Exception Something went wrong with the request.
     */
    public static String get(final String url) throws Exception {
        ConnectException connectException;

        int count = 0;
        do {
            try {
                return getInternal(url);
            } catch (ConnectException exception) {
                connectException = exception;
            }
        } while (++count < 3);

        throw connectException;
    }

    /**
     * Perform a single HTTP GET request.
     *
     * @param url The URL to perform the request against.
     * @return The connection of the response.
     * @throws UnexpectedStatusCode The server did not respond with status code 200.
     * @throws IOException Something went wrong with the request.
     */
    private static String getInternal(final String url) throws UnexpectedStatusCode, IOException {
        final URL urlObject = new URL(url);
        final HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

        // Optional default is GET.
        connection.setRequestMethod("GET");

        // Add request header.
        connection.setRequestProperty("User-Agent", Constants.HTTP_USER_AGENT);

        // Get the response reader.
        BufferedReader bufferedReader;
        try {
            bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException exception) {
            bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    connection.getErrorStream(), StandardCharsets.UTF_8));
        }

        // Read the response.
        String inputLine;
        final StringBuilder response = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            response.append(inputLine);
        }
        bufferedReader.close();

        // Handle the status code.
        final int statusCode = connection.getResponseCode();
        if (statusCode != 200) {
            throw new UnexpectedStatusCode(statusCode, response.toString());
        }

        return response.toString();
    }
}
