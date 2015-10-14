package io.sterodium.extensions.node.rmi;

import io.sterodium.rmi.protocol.MethodInvocationDto;
import io.sterodium.rmi.protocol.MethodInvocationResultDto;
import io.sterodium.rmi.protocol.server.RmiFacade;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.robot.desktop.DesktopKeyboard;
import org.sikuli.api.robot.desktop.DesktopMouse;

import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Mihails Volkovs mihails.volkovs@gmail.com
 *         Date: 24/09/2015
 */
public class SikuliApplication {

    private static final Logger LOGGER = Logger.getLogger(SikuliApplication.class.getName());

    private final RmiFacade rmiFacade;

    public SikuliApplication() {

        // base Sikuli operations
        rmiFacade = new RmiFacade();
        rmiFacade.add("mouse", new DesktopMouse());
        rmiFacade.add("keyboard", new DesktopKeyboard());
        try {
            rmiFacade.add("desktop", new DesktopScreenRegion());
            rmiFacade.add("clipboard", Toolkit.getDefaultToolkit().getSystemClipboard());
        } catch (ExceptionInInitializerError e) {
            LOGGER.log(Level.SEVERE, "Sikuli operations are not available on this environment.", e);
        }
        rmiFacade.add("target-factory", new TargetFactory());
    }

    public MethodInvocationResultDto invoke(String objectId, MethodInvocationDto invocation) {
        return rmiFacade.invoke(objectId, invocation);
    }

}
