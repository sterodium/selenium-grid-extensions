package io.sterodium.extensions.node.download;

import io.sterodium.extensions.node.BaseServletTest;
import com.google.common.io.Files;
import com.google.common.net.HttpHeaders;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.seleniumhq.jetty7.server.Server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 30/09/2015
 *         <p/>
 */
public class FileDownloadServletTest extends BaseServletTest {

    private Server fileUploadServer;
    private HttpHost serverHost;

    @Before
    public void setUp() throws Exception {
        fileUploadServer = startServerForServlet(new FileDownloadServlet(), "/" + FileDownloadServlet.class.getSimpleName() + "/*");
        serverHost = new HttpHost("localhost", fileUploadServer.getConnectors()[0].getLocalPort());
    }

    @After
    public void tearDown() throws Exception {
        fileUploadServer.stop();
    }

    @Test
    public void getShouldReturnFileContentsWithNameInHeader() throws IOException {
        File fileToGet = File.createTempFile("test", ".txt");
        FileUtils.write(fileToGet, "expected_content");

        CloseableHttpClient httpClient = HttpClients.createDefault();

        String encode = URLEncoder.encode(fileToGet.getAbsolutePath(), "UTF-8");
        HttpGet httpGet = new HttpGet("/FileDownloadServlet/" + encode);

        CloseableHttpResponse execute = httpClient.execute(serverHost, httpGet);

        //check contents are properly sent
        try (
                InputStream content = execute.getEntity().getContent()) {
            String s = IOUtils.toString(content);
            assertThat(s, is("expected_content"));
        }

        //check file name is available from header
        Header contentDispositionHeader = execute.getFirstHeader(HttpHeaders.CONTENT_DISPOSITION);
        assertThat(contentDispositionHeader.getValue(), containsString("filename=" + fileToGet.getName()));
        //check file is not locked by anything
        assertTrue(fileToGet.delete());
    }

    @Test
    public void getShouldReturnBadRequestWhenFileNotExists() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String encode = URLEncoder.encode("/some/location/", "UTF-8");
        HttpGet httpGet = new HttpGet("/FileDownloadServlet/" + encode);

        CloseableHttpResponse execute = httpClient.execute(serverHost, httpGet);

        int statusCode = execute.getStatusLine().getStatusCode();
        assertThat(statusCode, is(HttpStatus.SC_BAD_REQUEST));

        //check error message is set
        try (
                InputStream content = execute.getEntity().getContent()) {
            String s = IOUtils.toString(content);
            assertThat(s, is("Requested file doesn't exist."));
        }
    }

    @Test
    public void getShouldReturnBadRequestWhenFileIsDirectory() throws IOException {
        File directory = Files.createTempDir();
        CloseableHttpClient httpClient = HttpClients.createDefault();

        String encode = URLEncoder.encode(directory.getAbsolutePath(), "UTF-8");
        HttpGet httpGet = new HttpGet("/FileDownloadServlet/" + encode);

        CloseableHttpResponse execute = httpClient.execute(serverHost, httpGet);

        int statusCode = execute.getStatusLine().getStatusCode();
        assertThat(statusCode, is(HttpStatus.SC_BAD_REQUEST));

        //check error message is set
        try (
                InputStream content = execute.getEntity().getContent()) {
            String s = IOUtils.toString(content);
            assertThat(s, is("Requested file is directory."));
        }
        //check file is not locked by anything
        assertTrue(directory.delete());
    }

}
