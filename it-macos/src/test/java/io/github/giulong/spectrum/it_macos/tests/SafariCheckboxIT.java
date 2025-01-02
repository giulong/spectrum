package io.github.giulong.spectrum.it_macos.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_macos.pages.CheckboxPage;
import io.github.giulong.spectrum.it_macos.pages.LandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.OS.MAC;

@SuppressWarnings("unused")
@DisplayName("Checkbox Page")
@EnabledOnOs(MAC)
@EnabledIf("onMac")
class SafariCheckboxIT extends SpectrumTest<Void> {

    private static final String DRIVER_PROPERTY = "spectrum.driver";

    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    static boolean onMac() {
        return "safari".equals(System.getProperty(DRIVER_PROPERTY, System.getenv(DRIVER_PROPERTY)));
    }

    @Test
    void testWithNoDisplayName() {
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        screenshot();
        js.click(landingPage.getCheckboxLink());

        checkboxPage.waitForPageLoading();

        final WebElement firstCheckbox = checkboxPage.getCheckboxes().getFirst();
        final WebElement secondCheckbox = checkboxPage.getCheckboxes().get(1);

        screenshot();
        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        js.click(firstCheckbox);
        assertTrue(firstCheckbox.isSelected());

        screenshotInfo("After checking the first checkbox");
    }
}
