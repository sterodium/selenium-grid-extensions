package io.sterodium.extensions.node.delete;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Sameer Jethvani
 *         Date: 22/06/2017
 *         <p>
 *         Deletes file by providing it's path in GET request.
 */
public class FileDeleteServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(FileDeleteServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        LOGGER.info("Request for file delete received with path: " + pathInfo);

        File file = new File(pathInfo);
        if (!fileExistsAndNotDirectory(file, resp)) {
            return;
        }

        file.delete();
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
