package io.sterodium.sample.test;

import com.codeborne.selenide.WebDriverRunner;
import com.google.common.io.Resources;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
abstract class AbstractTest {
    protected String host;
    protected int port;

    @Before
    public void setUp() throws IOException {
        readGridHostPort();

        DesiredCapabilities desiredCapabilities = firefoxWithSikuli();
        URL url = new URL(String.format("http://%s:%d/wd/hub", host, port));

        RemoteWebDriver remoteWebDriver = new RemoteWebDriver(url, desiredCapabilities);
        WebDriverRunner.setWebDriver(remoteWebDriver);
    }

    private void readGridHostPort() throws IOException {
        URL resource = Resources.getResource("grid.properties");
        Properties properties = new Properties();
        try (InputStream i = resource.openStream()) {
            properties.load(i);
        }

        host = properties.get("grid.host").toString();
        port = Integer.parseInt(properties.get("grid.port").toString());
    }

    @After
    public void tearDown() {
        WebDriverRunner.closeWebDriver();
    }

    private DesiredCapabilities firefoxWithSikuli() {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setBrowserName("firefox");
        desiredCapabilities.setPlatform(Platform.ANY);
        desiredCapabilities.setCapability("sikuliExtension", true);
        return desiredCapabilities;
    }
}
