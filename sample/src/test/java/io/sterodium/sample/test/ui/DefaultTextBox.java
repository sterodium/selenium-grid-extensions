package io.sterodium.sample.test.ui;

import io.sterodium.extensions.node.rmi.TargetFactory;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.robot.Keyboard;
import org.sikuli.api.robot.Mouse;

import java.awt.event.KeyEvent;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
public class DefaultTextBox extends DefaultUiComponent implements TextBox {

    private Keyboard keyboard;

    public DefaultTextBox(Mouse mouse, Keyboard keyboard, TargetFactory targetFactory, ScreenRegion screenRegion) {
        super(mouse, targetFactory, screenRegion);
        this.keyboard = keyboard;
    }

    public void write(String text) {
        keyboard.type(text);
    }

    public void press(int key) {
        keyboard.keyDown(key);
        keyboard.keyUp(key);
    }

    public void deleteAllText() {
        keyboard.keyDown(KeyEvent.VK_CONTROL);
        keyboard.keyDown(KeyEvent.VK_A);
        keyboard.keyUp(KeyEvent.VK_CONTROL);
        keyboard.keyUp(KeyEvent.VK_A);
        keyboard.keyDown(KeyEvent.VK_DELETE);
        keyboard.keyUp(KeyEvent.VK_DELETE);
    }
}
