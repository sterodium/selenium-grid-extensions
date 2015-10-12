package io.sterodium.extensions.hub.proxy.client;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 22/09/2015
 */
public class RequestForwardingClientProvider {
    public RequestForwardingClient provide(String host, int port) {
        return new RequestForwardingClient(host, port);
    }
}
