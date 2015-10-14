package io.sterodium.extensions.client.download;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 30/09/2015
 */
class FileDownloadException extends RuntimeException {

    public FileDownloadException(String message, int code) {
        super(String.format("Response returned code %d, with message: %s", code, message));
    }

    public FileDownloadException(Throwable cause) {
        super(cause);
    }
}
