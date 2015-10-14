package io.sterodium.extensions.client;

import io.sterodium.extensions.hub.proxy.HubRequestsProxyingServlet;
import io.sterodium.extensions.node.SikuliExtensionServlet;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SikuliExtensionClientTest {

    public static final String PATH_TEMPLATE = "/grid/admin/%s/session/%s/%s";

    @Test
    public void sikuliExtensionPathIsCorrect() {
        String expectedPath = String.format(PATH_TEMPLATE,
                HubRequestsProxyingServlet.class.getSimpleName(),
                "sessionId",
                SikuliExtensionServlet.class.getSimpleName());

        String resultPath = String.format(SikuliExtensionClient.SIKULI_EXTENSION_PATH, "sessionId");
        assertThat(resultPath, is(expectedPath));
    }
}
