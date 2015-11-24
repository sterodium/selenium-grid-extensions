package io.sterodium.sample.test.ui;

import io.sterodium.extensions.client.SikuliExtensionClient;
import io.sterodium.extensions.node.rmi.TargetFactory;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.robot.Keyboard;
import org.sikuli.api.robot.Mouse;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
public class SikuliHelper {

    public static final int TIMEOUT = 6 * 1000;
    public static final int QUERY = 500;

    private SikuliExtensionClient client;
    private final DesktopScreenRegion desktop;
    private final TargetFactory targetFactory;
    private final Mouse mouse;
    private final Keyboard keyboard;

    public SikuliHelper(SikuliExtensionClient client) {
        this.client = client;
        desktop = client.getDesktop();
        targetFactory = client.getTargetFactory();
        mouse = client.getMouse();
        keyboard = client.getKeyboard();
    }


    public TextBox findTextBox(String resource) {
        ScreenRegion screenRegion = waitForElement(resource);
        return new DefaultTextBox(mouse, keyboard, targetFactory, screenRegion);
    }

    public UiComponent find(String resource) {
        ScreenRegion screenRegion = waitForElement(resource);
        return new DefaultUiComponent(mouse, targetFactory, screenRegion);
    }


    private ScreenRegion waitForElement(String resource) {
        ImageTarget imageTarget = targetFactory.createImageTarget(resource);

        ScreenRegion screenRegion = tryToFind(desktop, imageTarget);
        if (screenRegion != null) {
            return screenRegion;
        }
        throw new ElementNotFoundException(resource);

    }

    static ScreenRegion tryToFind(ScreenRegion desktop, ImageTarget imageTarget) {
        for (int i = 0; i < TIMEOUT / QUERY; i++) {
            try {
                ScreenRegion screenRegion = desktop.find(imageTarget);
                if (screenRegion != null) {
                    return screenRegion;
                }
                Thread.sleep(QUERY);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
        }
        return null;
    }


}
