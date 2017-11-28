package io.sterodium.extensions.hub.proxy;

import com.google.common.annotations.VisibleForTesting;
import io.sterodium.extensions.hub.proxy.client.RequestForwardingClient;
import io.sterodium.extensions.hub.proxy.client.RequestForwardingClientProvider;
import io.sterodium.extensions.hub.proxy.session.SeleniumSessions;
import org.openqa.grid.web.servlet.RegistryBasedServlet;
import org.openqa.grid.internal.GridRegistry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 21/09/2015
 */
public class HubRequestsProxyingServlet extends RegistryBasedServlet {

    private static final Logger LOGGER = Logger.getLogger(HubRequestsProxyingServlet.class.getName());

    @VisibleForTesting
    RequestForwardingClientProvider requestForwardingClientProvider;

    @SuppressWarnings("unused")
    public HubRequestsProxyingServlet() {
        this(null);
    }

    public HubRequestsProxyingServlet(GridRegistry registry) {
        super(registry);
        requestForwardingClientProvider = new RequestForwardingClientProvider();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardRequest(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardRequest(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardRequest(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        forwardRequest(req, resp);
    }

    private void forwardRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RequestForwardingClient requestForwardingClient;
        try {
            requestForwardingClient = createExtensionClient(req.getPathInfo());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        try {
            requestForwardingClient.forwardRequest(req, resp);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception during request forwarding", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    private RequestForwardingClient createExtensionClient(String path) {
        LOGGER.info("Forwarding request with path: " + path);
        String sessionId = SeleniumSessions.getSessionIdFromPath(path);
        LOGGER.info("Retrieving remote host for session: " + sessionId);

        SeleniumSessions sessions = new SeleniumSessions(getRegistry());
        sessions.refreshTimeout(sessionId);

        URL remoteHost = sessions.getRemoteHostForSession(sessionId);
        String host = remoteHost.getHost();
        int port = remoteHost.getPort();
        LOGGER.info("Remote host retrieved: " + host + ":" + port);

        return requestForwardingClientProvider.provide(host, port);
    }
}
