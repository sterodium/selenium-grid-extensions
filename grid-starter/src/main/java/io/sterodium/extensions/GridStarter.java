package io.sterodium.extensions;

import io.sterodium.extensions.spi.GridConfigurator;
import org.openqa.grid.selenium.GridLauncher;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.ServiceLoader;

/**
 * Installs JUL to SLF4J logging bridge and configures command line arguments before launching Selenium Grid server.
 *
 * @author Vladimir Ilyin ilyin371@gmail.com
 *         Date: 20/11/2015
 */
public final class GridStarter {

    private String[] args;

    private GridStarter(String[] args) {
        this.args = args;
    }

    public static void main(String[] args) throws Exception {
        bridgeJulToSlf4j();

        GridStarter starter = new GridStarter(args);
        starter.configure();
        starter.start();
    }

    private void configure() {
        ServiceLoader<GridConfigurator> services = ServiceLoader.load(GridConfigurator.class);
        for (GridConfigurator gridConfigurator : services) {
            args = gridConfigurator.configure(args);
        }
    }

    static void bridgeJulToSlf4j() {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
        }
    }

    private void start() throws Exception {
        GridLauncher.main(args);
    }

}
