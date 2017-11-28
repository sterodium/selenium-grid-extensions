package io.sterodium.extensions.hub.proxy;

import com.google.common.collect.Sets;
import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.TestSession;
import org.seleniumhq.jetty9.server.AbstractNetworkConnector;
import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.servlet.ServletContextHandler;
import org.seleniumhq.jetty9.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/09/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class HubRequestsProxyingServletTest {

    @Mock
    private Function mockedFunction;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private GridRegistry mockedRegistry;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TestSession mockedSession;

    private int hubPort;
    private Server hubServer;
    private Server stubServer;

    @Before
    public void setUp() throws Exception {
        StubServlet stubServlet = new StubServlet(mockedFunction);
        HubRequestsProxyingServlet hubRequestsProxyingServlet = new HubRequestsProxyingServlet(mockedRegistry);

        hubServer = startServerForServlet(hubRequestsProxyingServlet, "/" + HubRequestsProxyingServlet.class.getSimpleName() + "/*");
        hubPort = ((AbstractNetworkConnector) hubServer.getConnectors()[0]).getLocalPort();

        stubServer = startServerForServlet(stubServlet, "/extra/stubbyExtension/*");

        URL url = new URIBuilder("http://localhost:" + ((AbstractNetworkConnector) stubServer.getConnectors()[0]).getLocalPort())
                .build()
                .toURL();

        //Mock that registry contains session with url to redirect to
        when(mockedRegistry.getActiveSessions()).thenReturn(Sets.newHashSet(mockedSession));
        when(mockedSession.getExternalKey().getKey()).thenReturn("session_id");
        when(mockedSession.getSlot().getProxy().getRemoteHost()).thenReturn(url);
    }

    @After
    public void tearDown() throws Exception {
        hubServer.stop();
        stubServer.stop();
    }

    @Test
    public void shouldRedirectGetRequestAndTrimPathParams() throws IOException {
        doAnswer(verifyRequestPath())
                .when(mockedFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/%s/session/%s/%s/proper/get/path/params", hubPort,
                HubRequestsProxyingServlet.class.getSimpleName(), "session_id",
                "stubbyExtension"));
        httpClient.execute(httpGet);

        verify(mockedFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void shouldRedirectPostRequestAndTrimPathParams() throws IOException {
        doAnswer(verifyRequestPath())
                .when(mockedFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(String.format("http://localhost:%d/%s/session/%s/%s/proper/get/path/params", hubPort,
                HubRequestsProxyingServlet.class.getSimpleName(), "session_id",
                "stubbyExtension"));
        setEntityWithExpectedContent(httpPost);
        httpClient.execute(httpPost);

        verify(mockedFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void shouldRedirectPutRequestAndTrimPathParams() throws IOException {
        doAnswer(verifyRequestPath())
                .when(mockedFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPut httpPut = new HttpPut(String.format("http://localhost:%d/%s/session/%s/%s/proper/get/path/params", hubPort,
                HubRequestsProxyingServlet.class.getSimpleName(), "session_id",
                "stubbyExtension"));
        httpClient.execute(httpPut);

        verify(mockedFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    public void shouldRedirectDeleteRequestAndTrimPathParams() throws IOException {
        doAnswer(verifyRequestPath())
                .when(mockedFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpDelete httpPut = new HttpDelete(String.format("http://localhost:%d/%s/session/%s/%s/proper/get/path/params", hubPort,
                HubRequestsProxyingServlet.class.getSimpleName(), "session_id",
                "stubbyExtension"));
        httpClient.execute(httpPut);

        verify(mockedFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    private Answer verifyRequestPath() {
        return new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletRequest req = (HttpServletRequest) invocationOnMock.getArguments()[0];
                assertThat(req.getPathInfo(), is("/proper/get/path/params"));
                return null;
            }
        };
    }

    @Test
    public void shouldRedirectPostRequestWithSameContents() throws IOException {
        doAnswer(verifyRequestContent(MediaType.OCTET_STREAM.toString()))
                .when(mockedFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(String.format("http://localhost:%d/%s/session/%s/%s/proper/get/path/params", hubPort,
                HubRequestsProxyingServlet.class.getSimpleName(), "session_id",
                "stubbyExtension"));
        setEntityWithExpectedContent(httpPost);
        httpClient.execute(httpPost);

        verify(mockedFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    private void setEntityWithExpectedContent(HttpPost httpPost) {
        InputStream inputStream = new ByteArrayInputStream("expected_content".getBytes());
        InputStreamEntity entity = new InputStreamEntity(inputStream);
        entity.setContentType(MediaType.OCTET_STREAM.toString());
        httpPost.setEntity(entity);
    }

    private Answer verifyRequestContent(final String contentType) {
        return new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletRequest req = (HttpServletRequest) invocationOnMock.getArguments()[0];
                assertThat(req.getContentType(), equalTo(contentType));
                String reqContent = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
                assertThat(reqContent, is("expected_content"));
                return null;
            }
        };
    }

    @Test
    public void shouldReturnPostResponseWithProperContents() throws IOException {
        doAnswer(constructResponse(MediaType.OCTET_STREAM.toString()))
                .when(mockedFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(String.format("http://localhost:%d/%s/session/%s/%s/proper/get/path/params", hubPort,
                HubRequestsProxyingServlet.class.getSimpleName(), "session_id",
                "stubbyExtension"));
        setEntityWithExpectedContent(httpPost);
        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);

        verify(mockedFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        HttpEntity httpEntity = httpResponse.getEntity();
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(HttpServletResponse.SC_CREATED));
        assertThat(httpEntity.getContentType().getValue(), is(MediaType.OCTET_STREAM.toString()));

        String returnedContent = IOUtils.toString(httpEntity.getContent(), StandardCharsets.UTF_8);
        assertThat(returnedContent, is("expected_content"));
    }

    private Answer constructResponse(final String contentType) {
        return new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletResponse resp = (HttpServletResponse) invocationOnMock.getArguments()[1];
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getOutputStream().write("expected_content".getBytes());
                resp.setContentType(contentType);
                return null;
            }
        };
    }

    private static class StubServlet extends HttpServlet {

        private final Function function;

        public StubServlet(Function function) {
            this.function = function;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            function.apply(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            function.apply(req, resp);
        }

        @Override
        protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            function.apply(req, resp);
        }

        @Override
        protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            function.apply(req, resp);
        }
    }

    private interface Function {
        void apply(HttpServletRequest req, HttpServletResponse resp);
    }

    private Server startServerForServlet(HttpServlet servlet, String path) throws Exception {
        Server server = new Server(0);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(servlet), path);
        server.start();

        return server;
    }
}
