package io.sterodium.sample.test;

import com.codeborne.selenide.WebDriverRunner;
import io.sterodium.extensions.client.SikuliExtensionClient;
import io.sterodium.sample.test.ui.SikuliHelper;
import io.sterodium.sample.test.ui.TextBox;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.awt.event.KeyEvent;

import static com.codeborne.selenide.Selenide.open;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
public class AceEditorTest extends AbstractTest {

    private static final String ACE_IMAGES_BUNDLE = "images/ace";
    private static final String ACE_EDITOR_URL = "https://ace.c9.io/build/kitchen-sink.html";
    private static final String JS_FUNC = "function printSomethingAwesome() {alert(\"Sterodium.io\");}";

    @Test
    public void inputFunctionToAceEditor() throws InterruptedException {
        open(ACE_EDITOR_URL);
        maximize();

        String sessionId = getSessionId();

        SikuliExtensionClient sikuliExtensionClient = new SikuliExtensionClient(host, port, sessionId);
        sikuliExtensionClient.uploadResourceBundle(ACE_IMAGES_BUNDLE);


        SikuliHelper sikuliHelper = new SikuliHelper(sikuliExtensionClient);

        TextBox editor = sikuliHelper.findTextBox("js_body.png");
        editor.click();
        editor.deleteAllText();

        editor.press(KeyEvent.VK_ENTER);

        editor.write(JS_FUNC);

        editor.press(KeyEvent.VK_ENTER);
    }


    private String getSessionId() {
        return ((RemoteWebDriver) WebDriverRunner.getWebDriver()).getSessionId().toString();
    }

    private void maximize() {
        WebDriverRunner.getWebDriver().manage().window().maximize();
    }
}
