package io.sterodium.extensions.client.upload;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 25/09/2015
 */
class ResourceUploadException extends RuntimeException {
    public ResourceUploadException(int code, String message) {
        super(String.format("Resource upload returned status %d with message: %s", code, message));
    }
}
