package io.sterodium.extensions.client.upload;

import com.google.common.io.Files;
import io.sterodium.extensions.client.BaseRequestTest;
import io.sterodium.extensions.hub.proxy.HubRequestsProxyingServlet;
import io.sterodium.extensions.node.upload.FileUploadServlet;
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
import org.seleniumhq.jetty7.server.Server;
import org.zeroturnaround.zip.ZipUtil;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 28/09/2015
 *
 *         Test verifies that ResoureUploadRequest makes proper upload request with zipped resource folder.
 *         As result it should return contents of response as String.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceUploadRequestTest extends BaseRequestTest {


    private static final String EXPECTED_RETURN_FOLDER = "C:/Expected/Folder/";
    private static final String SESSION_ID = "sessionId";

    @Mock
    Function responseHandleFunction;

    private Server server;
    private int port;
    private String extensionPath;

    @Before
    public void setUp() throws Exception {
        StubServlet stubServlet = new StubServlet();
        stubServlet.setFunction(responseHandleFunction);

        extensionPath = String.format(PATH, HubRequestsProxyingServlet.class.getSimpleName(), SESSION_ID, FileUploadServlet.class.getSimpleName(), "*");
        server = startServerForServlet(stubServlet, extensionPath);
        port = server.getConnectors()[0].getLocalPort();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void pathTemplateShouldHaveProperServletNames() {
        String resultPath = String.format(ResourceUploadRequest.FILE_UPLOAD_EXTENSION_PATH, SESSION_ID);
        assertThat(extensionPath, startsWith(resultPath));
    }

    @Test
    public void verifyUploadedContentHasProperStructureAndCanBeUnzipped() {
        doAnswer(verifyRequestContent())
                .when(responseHandleFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        ResourceUploadRequest resourceUploadRequest = new ResourceUploadRequest("localhost", port, SESSION_ID);
        String responseWithPath = resourceUploadRequest.upload("files_for_upload");

        verify(responseHandleFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));
        assertThat(responseWithPath, is(EXPECTED_RETURN_FOLDER));
    }

    private Answer verifyRequestContent() {
        return new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletRequest req = (HttpServletRequest) invocationOnMock.getArguments()[0];
                HttpServletResponse resp = (HttpServletResponse) invocationOnMock.getArguments()[1];

                File tempFile = File.createTempFile("temp_resources", ".zip");
                try (ServletInputStream inputStream = req.getInputStream();
                     OutputStream outputStream = new FileOutputStream(tempFile)) {
                    IOUtils.copy(inputStream, outputStream);
                }
                File tempDir = Files.createTempDir();
                ZipUtil.unpack(tempFile, tempDir);

                File firstTxtFile = new File(tempDir, "files_for_upload/first.txt");
                File secondTxtFile = new File(tempDir, "files_for_upload/directory/second.txt");

                assertTrue(firstTxtFile.exists());
                assertTrue(secondTxtFile.exists());

                //verify content
                String firstFileContent = Files.toString(firstTxtFile, Charset.defaultCharset());
                assertThat(firstFileContent, containsString("content1"));
                String secondFileContent = Files.toString(secondTxtFile, Charset.defaultCharset());
                assertThat(secondFileContent, containsString("content2"));

                //clean temp directories
                FileUtils.deleteDirectory(tempDir);
                assertTrue("Failed to delete uploaded zip", tempFile.delete());


                //form response
                resp.getWriter().write(EXPECTED_RETURN_FOLDER);
                return null;
            }
        };
    }
}
