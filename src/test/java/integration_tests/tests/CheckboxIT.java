package integration_tests.tests;

import com.github.giulong.spectrum.SpectrumTest;
import integration_tests.pages.CheckboxPage;
import integration_tests.pages.LandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checkbox Page")
public class CheckboxIT extends SpectrumTest<Void> {

    // You just need to declare your pages here: Spectrum will take care of instantiating them
    // and will inject all the needed fields like the webDriver
    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @Test
    public void testWithNoDisplayName() {
        // Open the base url of the application under test
        webDriver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        landingPage.getCheckboxLink().click();

        final WebElement firstCheckbox = checkboxPage.getCheckboxes().get(0);
        final WebElement secondCheckbox = checkboxPage.getCheckboxes().get(1);

        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        // Take a screenshot with a custom message
        screenshotInfo("After checking the first checkbox");
    }
}
