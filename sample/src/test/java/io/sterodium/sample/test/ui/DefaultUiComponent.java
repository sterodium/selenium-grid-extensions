package io.sterodium.sample.test.ui;

import io.sterodium.extensions.node.rmi.TargetFactory;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.robot.Mouse;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
public class DefaultUiComponent implements UiComponent {

    protected Mouse mouse;
    protected TargetFactory targetFactory;
    protected ScreenRegion screenRegion;

    DefaultUiComponent(Mouse mouse, TargetFactory targetFactory, ScreenRegion screenRegion) {
        this.mouse = mouse;
        this.targetFactory = targetFactory;
        this.screenRegion = screenRegion;
    }

    public void click() {
        mouse.click(screenRegion.getCenter());
    }
}
