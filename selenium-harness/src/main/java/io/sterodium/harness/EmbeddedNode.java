package io.sterodium.harness;

import org.openqa.grid.common.RegistrationRequest;
import org.openqa.grid.internal.utils.SelfRegisteringRemote;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.server.SeleniumServer;

import java.util.HashMap;
import java.util.Map;

public final class EmbeddedNode {

    public void start() throws Exception {
        RegistrationRequest config = new RegistrationRequest();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put(RegistrationRequest.HUB_HOST, "127.0.0.1");
        attributes.put(RegistrationRequest.HUB_PORT, 4444);
        attributes.put(RegistrationRequest.PORT, 5555);
        attributes.put(RegistrationRequest.HOST, "127.0.0.1");
        attributes.put(RegistrationRequest.REMOTE_HOST, "http://127.0.0.1:5555");
        attributes.put(RegistrationRequest.ID, "fake-node");
        attributes.put("register", true);
        config.setConfiguration(attributes);
        SelfRegisteringRemote remote = new SelfRegisteringRemote(config);
        remote.addBrowser(DesiredCapabilities.htmlUnitWithJs(), 1);
        SeleniumServer server = new SeleniumServer(5555);
        remote.setRemoteServer(server);
        remote.startRemoteServer();
        remote.startRegistrationProcess();
    }

}
