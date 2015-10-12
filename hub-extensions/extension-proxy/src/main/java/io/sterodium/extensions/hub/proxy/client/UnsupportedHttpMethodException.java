package io.sterodium.extensions.hub.proxy.client;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 22/09/2015
 */
public class UnsupportedHttpMethodException extends RuntimeException {
    public UnsupportedHttpMethodException(String method) {
        super(String.format("Method %s is not supported", method));
    }
}
