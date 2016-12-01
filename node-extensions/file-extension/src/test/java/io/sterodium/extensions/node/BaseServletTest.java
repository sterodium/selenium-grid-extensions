package io.sterodium.extensions.node;

import org.apache.commons.io.IOUtils;
import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.servlet.ServletContextHandler;
import org.seleniumhq.jetty9.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/09/2015
 */
public abstract class BaseServletTest {

    public static final String ZIP_FILE_NAME = "test_entry.txt";

    protected Server startServerForServlet(HttpServlet servlet, String path) throws Exception {
        Server server = new Server(0);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(servlet), path);
        server.start();

        return server;
    }

    protected File createZipArchiveWithTextFile() throws IOException {
        final File zipArchive = File.createTempFile("temp_zip_", ".zip");
        try (
                final ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipArchive))) {
            ZipEntry e = new ZipEntry(ZIP_FILE_NAME);
            out.putNextEntry(e);
            IOUtils.write("test data", out, StandardCharsets.UTF_8);
            out.closeEntry();
        }
        return zipArchive;
    }

    protected void deleteIfExists(File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                assertThat("Couldn't clean file after tests", file.delete(), is(true));
            }
        }
    }
}
