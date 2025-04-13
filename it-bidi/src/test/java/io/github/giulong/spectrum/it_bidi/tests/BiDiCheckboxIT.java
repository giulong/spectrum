package io.github.giulong.spectrum.it_bidi.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_bidi.pages.CheckboxPage;
import io.github.giulong.spectrum.it_bidi.pages.LandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.browsingcontext.NavigationResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.bidi.browsingcontext.ReadinessState.COMPLETE;

@SuppressWarnings("unused")
@DisplayName("Checkbox Page")
public class BiDiCheckboxIT extends SpectrumTest<Void> {

    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @Test
    public void testWithNoDisplayName() {
        final BrowsingContext browsingContext = new BrowsingContext(driver, driver.getWindowHandle());
        final NavigationResult info = browsingContext.navigate(configuration.getApplication().getBaseUrl(), COMPLETE);

        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        extentTest.info("Custom step that should not be highlighted on video playback");
        landingPage.getCheckboxLink().click();
        extentTest.info("Custom step that should not be highlighted on video playback");

        final WebElement firstCheckbox = checkboxPage.getCheckboxes().getFirst();
        final WebElement secondCheckbox = checkboxPage.getCheckboxes().get(1);

        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        extentTest.info("Custom step that should not be highlighted on video playback");
        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        // Take a screenshot with a custom message
        screenshotInfo("After checking the first checkbox");
    }
}
