package io.sterodium.extensions.hub.proxy;


import io.sterodium.extensions.hub.proxy.client.HttpClientProvider;
import io.sterodium.extensions.hub.proxy.client.RequestForwardingClient;
import io.sterodium.extensions.hub.proxy.client.RequestForwardingClientProvider;
import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.grid.internal.Registry;
import org.openqa.grid.internal.TestSession;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 21/09/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class HubRequestsProxyingServletPathsTest {

    @Mock
    HttpServletRequest req;
    @Mock
    HttpServletResponse resp;
    @Mock
    Registry registry;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    TestSession activeSession1;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    TestSession activeSession2;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HttpClientProvider httpClientProvider;
    @Mock
    RequestForwardingClientProvider requestForwardingClientProvider;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    CloseableHttpClient closeableHttpClient;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    CloseableHttpResponse closeableHttpResponse;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    HttpEntity entity;

    private HubRequestsProxyingServlet servlet;

    private final ByteArrayOutputStream httpServletResponseOutputStream = new ByteArrayOutputStream(4096);

    private final ServletOutputStream outputStream = new ServletOutputStream() {
        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public void write(int b) throws IOException {
            httpServletResponseOutputStream.write(b);
        }
    };

    private InputStream httpServletRequestInputStream;
    private final ServletInputStream inputStream = new ServletInputStream() {
        @Override
        public boolean isFinished() {
            try {
                return 0 == httpServletRequestInputStream.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        public int read() throws IOException {
            return httpServletRequestInputStream.read();
        }
    };

    @Before
    public void setUp() throws IOException {
        httpServletRequestInputStream = IOUtils.toInputStream("httpServletRequestInputStream", "UTF-8");
        servlet = new HubRequestsProxyingServlet(registry);
        servlet.requestForwardingClientProvider = requestForwardingClientProvider;

        RequestForwardingClient requestForwardingClient = new RequestForwardingClient("test:5555", httpClientProvider);

        URL url = new URL("http://localhost:5555/");

        when(registry.getActiveSessions()).thenReturn(Sets.newHashSet(activeSession1, activeSession2));
        when(activeSession1.getExternalKey().getKey()).thenReturn("uuid1");
        when(activeSession1.getSlot().getProxy().getRemoteHost()).thenReturn(url);
        when(activeSession2.getExternalKey().getKey()).thenReturn("uuid2");

        when(req.getContentType()).thenReturn("application/json");
        when(req.getInputStream()).thenReturn(inputStream);
        when(req.getContentLength()).thenReturn(29);

        when(requestForwardingClientProvider.provide(anyString(), anyInt())).thenReturn(requestForwardingClient);
        when(httpClientProvider.provide()).thenReturn(closeableHttpClient);

        when(closeableHttpClient.execute(Mockito.<HttpUriRequest>anyObject())).thenReturn(closeableHttpResponse);

        when(closeableHttpResponse.getStatusLine().getStatusCode()).thenReturn(200);
        when(closeableHttpResponse.getEntity()).thenReturn(entity);

        when(entity.getContent()).thenReturn(IOUtils.toInputStream("valid stream", "UTF-8"));
        when(entity.getContentLength()).thenReturn(12L);

        when(resp.getOutputStream()).thenReturn(outputStream);
    }

    @Test
    public void doGetWithValidSessionIdInPath() throws ServletException, IOException {
        when(req.getPathInfo()).thenReturn("/session/uuid1");
        when(req.getMethod()).thenReturn("GET");
        servlet.doGet(req, resp);

        assertThat(httpServletResponseOutputStream.toString(), is("valid stream"));
    }

    @Test
    public void doPostWithValidSessionIdInPath() throws ServletException, IOException {
        when(req.getPathInfo()).thenReturn("/session/uuid1");
        when(req.getMethod()).thenReturn("POST");
        servlet.doPost(req, resp);

        assertThat(httpServletResponseOutputStream.toString(), is("valid stream"));
    }

    @Test
    public void doGetWithInvalidSessionIdInPath() throws ServletException, IOException {
        when(req.getPathInfo()).thenReturn("/session");
        servlet.doGet(req, resp);

        verify(resp).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
    }

    @Test
    public void doGetWithInvalidSessionIdInPath_2() throws ServletException, IOException {
        when(req.getPathInfo()).thenReturn("/session/");
        servlet.doGet(req, resp);

        verify(resp).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
    }

    @Test
    public void doGetWithInvalidSessionIdInPath_3() throws ServletException, IOException {
        when(req.getPathInfo()).thenReturn("/8fba10d9-e2e4-498d-a192-555314658ab6/");
        servlet.doGet(req, resp);

        verify(resp).sendError(eq(HttpServletResponse.SC_BAD_REQUEST), anyString());
    }

}
