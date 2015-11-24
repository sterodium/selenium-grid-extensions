package io.sterodium.sample.test.ui;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
public class ElementNotFoundException extends RuntimeException {
    public ElementNotFoundException(String s) {
        super(String.format("Ui element from image %s not found on the screen", s));
    }
}
