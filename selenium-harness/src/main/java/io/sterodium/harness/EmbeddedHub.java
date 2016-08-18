package io.sterodium.harness;

import org.openqa.grid.internal.utils.GridHubConfiguration;
import org.openqa.grid.web.Hub;

public class EmbeddedHub {

    private Hub hub;

    public void start() throws Exception {
        GridHubConfiguration configuration = new GridHubConfiguration();
        hub = new Hub(configuration);
        hub.start();
    }

    public void stop() throws Exception {
        hub.stop();
    }

}
