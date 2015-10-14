package io.sterodium.extensions.client;

import io.sterodium.extensions.client.download.FileDownloadRequest;
import io.sterodium.extensions.client.upload.ResourceUploadRequest;

import java.io.File;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 14/10/2015
 *         Client for File Extension installed on Selenium Node
 */
public class FileExtensionClient {

    private FileDownloadRequest fileDownloadRequest;
    private ResourceUploadRequest resourceUploadRequest;

    /**
     * @param hubHost   selenium hub host
     * @param hubPort   selenium hub port
     * @param sessionId remote web driver session id
     */
    public FileExtensionClient(String hubHost, int hubPort, String sessionId) {
        fileDownloadRequest = new FileDownloadRequest(hubHost, hubPort, sessionId);
        resourceUploadRequest = new ResourceUploadRequest(hubHost, hubPort, sessionId);
    }

    /**
     * Download file from Selenium Node.
     *
     * @param pathToFile absolute path to file
     * @return downloaded file in temp directory
     */
    public File download(String pathToFile) {
        return fileDownloadRequest.download(pathToFile);
    }

    /**
     * Upload resource folder from classpath.
     *
     * @param resourcesPath path to resource folder in classpath
     * @return directory where files are stored on remote machine
     */
    public String upload(String resourcesPath) {
        return resourceUploadRequest.upload(resourcesPath);
    }
}
