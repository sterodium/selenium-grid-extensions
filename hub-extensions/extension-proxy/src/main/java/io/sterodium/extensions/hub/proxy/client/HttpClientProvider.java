package io.sterodium.extensions.hub.proxy.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 21/09/2015
 *         <p/>
 */
public class HttpClientProvider {

    public CloseableHttpClient provide() {
        return HttpClients.createDefault();
    }
}
