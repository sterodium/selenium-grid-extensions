package io.sterodium.extensions.client.delete;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sameer Jethvani
 * Date 22/06/2017
 *
 */
public class FileDeleteRequest {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileDeleteRequest.class);

    public static final String FILE_DELETE_EXTENSION_PATH = "/grid/admin/HubRequestsProxyingServlet/session/%s/FileDeleteServlet/%s";

    private final HttpHost httpHost;
    private final String sessionId;

    public FileDeleteRequest(String host, int port, String sessionId) {
        this.httpHost = new HttpHost(host, port);
        this.sessionId = sessionId;
    }

    public boolean delete(String pathToFile) {
        String encodedPath;
        try {
            encodedPath = URLEncoder.encode(pathToFile, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Failed to encode path", e);
            return false;
        }

        HttpGet request = new HttpGet(String.format(FILE_DELETE_EXTENSION_PATH, sessionId, encodedPath));
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse execute = httpClient.execute(httpHost, request);
            int statusCode = execute.getStatusLine().getStatusCode();
            return HttpStatus.SC_OK == statusCode;
        } catch (IOException e) {
            return false;
        }
    }
}
