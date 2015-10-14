package io.sterodium.extensions.client;


import io.sterodium.extensions.node.rmi.TargetFactory;
import io.sterodium.rmi.protocol.client.RemoteNavigator;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.robot.Keyboard;
import org.sikuli.api.robot.Mouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.datatransfer.Clipboard;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 24/09/2015
 */
@SuppressWarnings("UnusedDeclaration")
public final class SikuliExtensionClient {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(SikuliExtensionClient.class);

    static final String SIKULI_EXTENSION_PATH = "/grid/admin/HubRequestsProxyingServlet/session/%s/SikuliExtensionServlet";

    private RemoteNavigator navigator;
    private FileExtensionClient fileExtensionClient;

    public SikuliExtensionClient(String host, int port, String sessionId) {
        navigator = new RemoteNavigator(host, port, String.format(SIKULI_EXTENSION_PATH, sessionId));
        fileExtensionClient = new FileExtensionClient(host, port, sessionId);
    }

    public Mouse getMouse() {
        return navigator.createProxy(Mouse.class, "mouse");
    }

    public Keyboard getKeyboard() {
        return navigator.createProxy(Keyboard.class, "keyboard");
    }

    public Clipboard getClipboard() {
        return navigator.createProxy(Clipboard.class, "clipboard");
    }

    public DesktopScreenRegion getDesktop() {
        return navigator.createProxy(DesktopScreenRegion.class, "desktop");
    }

    public TargetFactory getTargetFactory() {
        return navigator.createProxy(TargetFactory.class, "target-factory");
    }

    public void uploadResourceBundle(String resourceBundlePath) {
        String upload = fileExtensionClient.upload(resourceBundlePath);
        LOGGER.debug("Resource bundle uploaded to " + upload);
        getTargetFactory().setImagePrefix(upload);
    }
}
