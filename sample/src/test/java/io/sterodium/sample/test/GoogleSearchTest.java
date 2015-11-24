package io.sterodium.sample.test;

import com.codeborne.selenide.WebDriverRunner;
import io.sterodium.extensions.client.SikuliExtensionClient;
import io.sterodium.sample.test.ui.SikuliHelper;
import io.sterodium.sample.test.ui.TextBox;
import io.sterodium.sample.test.ui.UiComponent;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import static com.codeborne.selenide.Selenide.open;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/11/2015
 */
public class GoogleSearchTest extends AbstractTest {

    private static final String GOOGLE_IMAGES_BUNDLE = "images/google";
    private static final String GOOGLE_IMAGES_URL = "https://images.google.com/";
    private static final String SIKULI_LOGO_SEARCH = "sikuli logo";

    private static final String SEARCH_FIELD_IMG = "search_field.png";
    private static final String SEARCH_BUTTON_IMG = "search_button.png";
    private static final String SIKULI_LOGO_IMG = "sikuli_logo.png";

    @Test
    public void searchGoogleForSikuliLogo() throws InterruptedException {
        open(GOOGLE_IMAGES_URL);

        String sessionId = getSessionId();

        SikuliExtensionClient sikuliExtensionClient = new SikuliExtensionClient(host, port, sessionId);
        sikuliExtensionClient.uploadResourceBundle(GOOGLE_IMAGES_BUNDLE);
        SikuliHelper sikuliHelper = new SikuliHelper(sikuliExtensionClient);

        TextBox searchInput = sikuliHelper.findTextBox(SEARCH_FIELD_IMG);
        searchInput.click();
        searchInput.write(SIKULI_LOGO_SEARCH);

        UiComponent searchButton = sikuliHelper.find(SEARCH_BUTTON_IMG);
        searchButton.click();

        UiComponent uiComponent = sikuliHelper.find(SIKULI_LOGO_IMG);

        uiComponent.click();
    }

    private String getSessionId() {
        return ((RemoteWebDriver) WebDriverRunner.getWebDriver()).getSessionId().toString();
    }
}
