package io.sterodium.extensions.client.download;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.commons.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 30/09/2015
 */
public class FileDownloadRequest {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileDownloadRequest.class);

    public static final String FILE_DOWNLOAD_EXTENSION_PATH = "/grid/admin/HubRequestsProxyingServlet/session/%s/FileDownloadServlet/%s";

    private final HttpHost httpHost;
    private final String sessionId;

    public FileDownloadRequest(String host, int port, String sessionId) {
        this.httpHost = new HttpHost(host, port);
        this.sessionId = sessionId;
    }

    public File download(String pathToFile) {
        String encodedPath;
        try {
            encodedPath = URLEncoder.encode(pathToFile, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Failed to encode path", e);
            return null;
        }

        HttpGet request = new HttpGet(String.format(FILE_DOWNLOAD_EXTENSION_PATH, sessionId, encodedPath));
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse execute = httpClient.execute(httpHost, request);
            int statusCode = execute.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                File downloadResult = File.createTempFile("download_result", ".tmp");
                try (
                        FileOutputStream outputStream = new FileOutputStream(downloadResult);
                        InputStream responseStream = execute.getEntity().getContent()) {
                    IOUtils.copy(responseStream, outputStream);
                }
                return downloadResult;
            } else {
                String message = IOUtils.toString(execute.getEntity().getContent(), "UTF-8");
                throw new FileDownloadException(message, statusCode);
            }
        } catch (IOException e) {
            throw new FileDownloadException(e);
        }
    }
}
