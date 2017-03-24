package io.sterodium.sample;

import com.google.common.io.Resources;
import org.openqa.grid.selenium.GridLauncherV3;

import java.io.File;
import java.net.URL;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
public final class LocalSeleniumGridLauncher {

    private static final String LOG_DIR = "target/selenium-logs";
    private static final String HUB_PARAMS = "-role hub -hubConfig %s";
    private static final String NODE_PARAMS = "-role node -nodeConfig %s";

    public static void main(String[] args) throws Exception {
        launchGrid();
    }

    private static String getConfigPath(String configName) {
        URL resource = Resources.getResource(configName);
        return resource.getPath();
    }

    public static void launchGrid() throws Exception {
        new File(LOG_DIR).mkdirs();

        String[] hubParams = String.format(HUB_PARAMS, getConfigPath("hubConfig.json")).split(" ");
        GridLauncherV3.main(hubParams);
        System.out.println("Hub started");

        String[] nodeParams = String.format(NODE_PARAMS, getConfigPath("nodeConfig.json")).split(" ");
        GridLauncherV3.main(nodeParams);
        System.out.println("Node started");
    }

    private LocalSeleniumGridLauncher() {
    }
}
