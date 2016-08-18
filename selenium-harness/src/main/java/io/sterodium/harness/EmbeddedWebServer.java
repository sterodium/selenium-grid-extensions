package io.sterodium.harness;

import org.seleniumhq.jetty9.server.Handler;
import org.seleniumhq.jetty9.server.Server;
import org.seleniumhq.jetty9.server.handler.DefaultHandler;
import org.seleniumhq.jetty9.server.handler.HandlerList;
import org.seleniumhq.jetty9.server.handler.ResourceHandler;
import org.seleniumhq.jetty9.util.resource.Resource;

public final class EmbeddedWebServer {

    public void init() {
        Server server = new Server(8080);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setBaseResource(Resource.newClassPathResource("index.html"));
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});

        server.setHandler(handlers);

        try {
            server.start();
        } catch (Exception e) {
            throw new SeleniumHarnessException(e);
        }
    }

}
