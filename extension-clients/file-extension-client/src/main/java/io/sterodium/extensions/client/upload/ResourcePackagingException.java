package io.sterodium.extensions.client.upload;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 25/09/2015
 */
class ResourcePackagingException extends RuntimeException {

    public ResourcePackagingException(String message) {
        super(message);
    }

    public ResourcePackagingException(Throwable cause) {
        super(cause);
    }
}
