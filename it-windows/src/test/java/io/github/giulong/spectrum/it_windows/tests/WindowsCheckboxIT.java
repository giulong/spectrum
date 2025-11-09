package io.github.giulong.spectrum.it_windows.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_windows.pages.CheckboxPage;
import io.github.giulong.spectrum.it_windows.pages.LandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.openqa.selenium.WebElement;

@SuppressWarnings("unused")
@DisplayName("Checkbox Page")
@EnabledOnOs(WINDOWS)
class WindowsCheckboxIT extends SpectrumTest<Void> {

    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @Test
    void testWithNoDisplayName() {
        // Open the base url of the application under test
        driver.get(configuration.getApplication().getBaseUrl());
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
