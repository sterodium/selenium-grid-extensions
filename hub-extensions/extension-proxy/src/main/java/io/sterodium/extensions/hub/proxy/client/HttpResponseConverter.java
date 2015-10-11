package io.sterodium.extensions.hub.proxy.client;

import com.google.common.primitives.Ints;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 21/09/2015
 *         <p/>
 */
public final class HttpResponseConverter {

    private static final Logger LOGGER = Logger.getLogger(HttpResponseConverter.class.getName());

    private HttpResponseConverter() {
    }

    public static void copy(HttpResponse source, HttpServletResponse target) {
        int statusCode = source.getStatusLine().getStatusCode();
        target.setStatus(statusCode);
        LOGGER.info("Response from extension returned " + statusCode + " status code");

        HttpEntity entity = source.getEntity();

        Header contentType = entity.getContentType();
        if (contentType != null) {
            target.setContentType(contentType.getValue());
            LOGGER.info("Response from extension returned " + contentType.getValue() + " content type");
        }

        long contentLength = entity.getContentLength();
        target.setContentLength(Ints.checkedCast(contentLength));
        LOGGER.info("Response from extension has " + contentLength + " content length");

        LOGGER.info("Copying body content to original servlet response");
        try (InputStream content = entity.getContent();
             OutputStream response = target.getOutputStream()) {
            IOUtils.copy(content, response);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to copy response body content", e);
        }
    }
}
