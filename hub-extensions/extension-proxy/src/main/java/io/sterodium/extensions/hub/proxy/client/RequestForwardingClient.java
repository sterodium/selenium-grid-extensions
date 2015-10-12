package io.sterodium.extensions.hub.proxy.client;

import io.sterodium.extensions.hub.proxy.session.SeleniumSessions;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.logging.Logger;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 22/09/2015
 */
public class RequestForwardingClient {

    private static final Logger LOGGER = Logger.getLogger(RequestForwardingClient.class.getName());

    private static final String NODE_HOST = "http://%s:%d/extra";

    private final HttpClientProvider httpClientProvider;
    private final String endpoint;

    public RequestForwardingClient(String host, int port) {
        this(String.format(NODE_HOST, host, port), new HttpClientProvider());
    }

    public RequestForwardingClient(String endpoint, HttpClientProvider httpClientProvider) {
        this.httpClientProvider = httpClientProvider;
        this.endpoint = endpoint;
    }

    public void forwardRequest(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        try (
                CloseableHttpClient httpClient = httpClientProvider.provide()
        ) {
            HttpRequestBase httpRequest = createHttpRequest(servletRequest);

            CloseableHttpResponse extensionResponse = httpClient.execute(httpRequest);
            HttpResponseConverter.copy(extensionResponse, servletResponse);
        }
    }

    private HttpRequestBase createHttpRequest(HttpServletRequest request) throws IOException {
        String method = request.getMethod();
        LOGGER.info("Creating " + method + " request to forward");
        HttpRequestBase httpRequestBase =   HttpPost.METHOD_NAME.equals(method)     ?    createPostRequest(request) :
                                            HttpGet.METHOD_NAME.equals(method)      ?    new HttpGet() :
                                            HttpPut.METHOD_NAME.equals(method)      ?    new HttpPut() :
                                            HttpDelete.METHOD_NAME.equals(method)   ?    new HttpDelete() : null;

        if (httpRequestBase == null) {
            throw new UnsupportedHttpMethodException(method);
        }
        URI uri = URI.create(endpoint + SeleniumSessions.trimSessionPath(request.getPathInfo()));
        LOGGER.info("Trimming session id from path, new path: " + uri.toString());
        httpRequestBase.setURI(uri);

        return httpRequestBase;
    }

    private HttpRequestBase createPostRequest(HttpServletRequest request) throws IOException {
        HttpPost httpPost = new HttpPost();
        InputStreamEntity entity = new InputStreamEntity(request.getInputStream(),
                request.getContentLength(),
                ContentType.create(request.getContentType()));
        httpPost.setEntity(entity);

        return httpPost;
    }
}
