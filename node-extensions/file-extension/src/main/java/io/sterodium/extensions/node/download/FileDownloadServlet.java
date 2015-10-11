package io.sterodium.extensions.node.download;

import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 22/09/2015
 *         <p/>
 *         Allows to download file by providing it's path in GET request.
 */
public class FileDownloadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(FileDownloadServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        LOGGER.info("Request for file download received with path: " + pathInfo);

        File file = new File(pathInfo);
        if (!fileExistsAndNotDirectory(file, resp)) {
            return;
        }

        String contentType = identifyFileContentType(file);
        resp.setContentType(contentType);
        resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        try (
                FileInputStream fileInputStream = new FileInputStream(file);
                ServletOutputStream outputStream = resp.getOutputStream()) {
            IOUtils.copy(fileInputStream, outputStream);
        }
    }

    private String identifyFileContentType(File file) throws IOException {
        String contentType = Files.probeContentType(file.toPath());
        return contentType != null ? contentType : MediaType.OCTET_STREAM.toString();
    }

    private boolean fileExistsAndNotDirectory(File requestedFile, HttpServletResponse resp) throws IOException {
        if (!requestedFile.exists()) {
            LOGGER.info("Requested file doesn't exist: " + requestedFile.getAbsolutePath());
            sendError(resp, "Requested file doesn't exist.");
            return false;
        }
        if (requestedFile.isDirectory()) {
            LOGGER.info("Requested file is directory: " + requestedFile.getAbsolutePath());
            sendError(resp, "Requested file is directory.");
            return false;
        }
        if (!requestedFile.canRead()) {
            LOGGER.info("Requested file cannot bet read: " + requestedFile.getAbsolutePath());
            sendError(resp, "Requested file can be read.");
            return false;
        }
        return true;
    }

    private void sendError(HttpServletResponse resp, String msg) throws IOException {
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        resp.getWriter().write(msg);
    }
}
