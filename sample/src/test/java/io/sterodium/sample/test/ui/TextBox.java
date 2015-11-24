package io.sterodium.sample.test.ui;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
public interface TextBox extends UiComponent {
    void write(String text);

    void deleteAllText();

    void press(int key);
}
