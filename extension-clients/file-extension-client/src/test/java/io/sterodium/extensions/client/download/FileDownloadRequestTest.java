package io.sterodium.extensions.client.download;

import io.sterodium.extensions.client.BaseRequestTest;
import io.sterodium.extensions.hub.proxy.HubRequestsProxyingServlet;
import io.sterodium.extensions.node.download.FileDownloadServlet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.seleniumhq.jetty9.server.AbstractNetworkConnector;
import org.seleniumhq.jetty9.server.Server;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 30/09/2015
 */
@RunWith(MockitoJUnitRunner.class)
public class FileDownloadRequestTest extends BaseRequestTest {

    private static final String SESSION_ID = "sessionId";
    private static final String EXPECTED_CONTENT = "expected content";

    @Mock
    Function responseHandleFunction;

    private File fileToGet;
    private Server server;
    private int port;
    private String extensionPath;

    @Before
    public void setUp() throws Exception {
        StubServlet stubServlet = new StubServlet();
        stubServlet.setFunction(responseHandleFunction);

        fileToGet = File.createTempFile("test", ".txt");
        FileUtils.write(fileToGet, EXPECTED_CONTENT, StandardCharsets.UTF_8);

        extensionPath = String.format(PATH, HubRequestsProxyingServlet.class.getSimpleName(), SESSION_ID, FileDownloadServlet.class.getSimpleName(), "*");
        server = startServerForServlet(stubServlet, extensionPath);
        port = ((AbstractNetworkConnector) server.getConnectors()[0]).getLocalPort();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        assertTrue(fileToGet.delete());
    }

    @Test
    public void pathTemplateShouldHaveProperServletNames() {
        String resultPath = String.format(FileDownloadRequest.FILE_DOWNLOAD_EXTENSION_PATH, SESSION_ID, "*");
        assertThat(resultPath, is(extensionPath));
    }

    @Test
    public void testDownload() throws IOException {
        doAnswer(verifyRequestContent())
                .when(responseHandleFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        FileDownloadRequest fileDownloadRequest = new FileDownloadRequest("localhost", port, SESSION_ID);
        File downloadedFile = fileDownloadRequest.download(fileToGet.getAbsolutePath());

        try (FileInputStream fileInputStream = new FileInputStream(downloadedFile)) {
            String s = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
            assertThat(s, is(EXPECTED_CONTENT));
        }
        verify(responseHandleFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    private Answer verifyRequestContent() {
        return new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletRequest req = (HttpServletRequest) invocationOnMock.getArguments()[0];
                HttpServletResponse resp = (HttpServletResponse) invocationOnMock.getArguments()[1];

                String pathInfo = req.getPathInfo();
                assertThat(pathInfo, containsString(fileToGet.getAbsolutePath()));

                try (

                        FileInputStream fileInputStream = new FileInputStream(new File(pathInfo));
                        ServletOutputStream outputStream = resp.getOutputStream()) {
                    IOUtils.copy(fileInputStream, outputStream);
                }
                return null;
            }
        };
    }
}
