package io.github.giulong.spectrum.it.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.OutputType.BYTES;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it.pages.CheckboxPage;
import io.github.giulong.spectrum.it.pages.LandingPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

@DisplayName("Checkbox Page")
class CheckboxIT extends SpectrumTest<Void> {

    // You just need to declare your pages here: Spectrum will take care of instantiating them
    // and will inject all the needed fields like the driver
    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @Test
    void testWithNoDisplayName() {
        // Open the base url of the application under test
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        // Taking a screenshot with the native Selenium API to prove it's the same as using the helper methods
        takesScreenshot.getScreenshotAs(BYTES);

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
