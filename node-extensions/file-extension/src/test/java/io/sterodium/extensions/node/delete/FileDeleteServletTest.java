package io.sterodium.extensions.node.delete;

import io.sterodium.extensions.node.BaseServletTest;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.seleniumhq.jetty9.server.AbstractNetworkConnector;
import org.seleniumhq.jetty9.server.Server;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Sameer Jethvani (s.jethvani@gmail.com)
 *         Date: 22/06/2017
 */
public class FileDeleteServletTest extends BaseServletTest {

    private Server filedeleteServer;
    private HttpHost serverHost;

    @Before
    public void setUp() throws Exception {
        filedeleteServer = startServerForServlet(new FileDeleteServlet(), "/" + FileDeleteServlet.class.getSimpleName() + "/*");
        serverHost = new HttpHost("localhost", ((AbstractNetworkConnector) filedeleteServer.getConnectors()[0]).getLocalPort());
    }

    @After
    public void tearDown() throws Exception {
        filedeleteServer.stop();
    }

    @Test
    public void getShouldDeleteFile() throws IOException {
        File fileTobeDeleted = File.createTempFile("testDeleteFile", ".txt");
        FileUtils.write(fileTobeDeleted, "file_to_be_deleted_content", StandardCharsets.UTF_8);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        String encode = URLEncoder.encode(fileTobeDeleted.getAbsolutePath(), "UTF-8");
        HttpGet httpGet = new HttpGet("/FileDeleteServlet/" + encode);

        CloseableHttpResponse execute = httpClient.execute(serverHost, httpGet);

        //check file got deleted
        //Assert.assertFalse(fileTobeDeleted.getAbsolutePath()+" File should have been deleted ",fileTobeDeleted.exists());
        assertThat(fileTobeDeleted+" File should have been deleted . It should not exist at this point",fileTobeDeleted.exists(), is(false));
    }
}
