package io.sterodium.extensions.node.upload;

import com.google.common.io.Files;
import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 22/09/2015
 *         <p/>
 *         Allows to upload zip archive with resources.
 *         Zip contents will be stored in temporary folder,
 *         absolute path will be returned in response body.
 */
public class FileUploadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(FileUploadServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        LOGGER.info(String.format("Request Content-Type: %s, Content-Length:%d", req.getContentType(), req.getContentLength()));

        if (!MediaType.OCTET_STREAM.toString().equals(req.getContentType())) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Content type " + req.getContentType() + " is not supported");
            return;
        }

        LOGGER.info("Creating temporary file");
        File tempFile = File.createTempFile("selenium_node", ".zip");
        try (ServletInputStream inputStream = req.getInputStream();
             OutputStream outputStream = new FileOutputStream(tempFile)) {
            LOGGER.info("Copying request input stream to file");
            IOUtils.copy(inputStream, outputStream);
        }

        LOGGER.info("Unzipping zip archive");
        File imagesBaseDir = unZip(tempFile);
        if (!tempFile.delete()) {
            throw new IOException("Unable to delete file: " + tempFile);
        }

        LOGGER.info("Writing directory to response");
        PrintWriter writer = resp.getWriter();
        writer.write(imagesBaseDir.getAbsolutePath());
    }

    private static File unZip(final File zippedFile) throws IOException {
        File outputFolder = Files.createTempDir();
        try (ZipFile zipFile = new ZipFile(zippedFile)) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                final File entryDestination = new File(outputFolder, entry.getName());
                if (entry.isDirectory()) {
                    //noinspection ResultOfMethodCallIgnored
                    entryDestination.mkdirs();
                } else {
                    //noinspection ResultOfMethodCallIgnored
                    entryDestination.getParentFile().mkdirs();
                    final InputStream in = zipFile.getInputStream(entry);
                    final OutputStream out = new FileOutputStream(entryDestination);
                    IOUtils.copy(in, out);
                    IOUtils.closeQuietly(in);
                    out.close();
                }
            }
        }
        return outputFolder;
    }
}
