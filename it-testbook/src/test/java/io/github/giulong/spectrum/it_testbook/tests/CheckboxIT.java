package io.github.giulong.spectrum.it_testbook.tests;

import static org.junit.jupiter.api.Assertions.*;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_testbook.pages.CheckboxPage;
import io.github.giulong.spectrum.it_testbook.pages.LandingPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

@SuppressWarnings("unused")
@DisplayName("Checkbox Page")
class CheckboxIT extends SpectrumTest<Void> {

    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @Test
    void testWithNoDisplayName() {
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        screenshot();
        landingPage.getCheckboxLink().click();

        final WebElement firstCheckbox = checkboxPage.getCheckboxes().getFirst();
        final WebElement secondCheckbox = checkboxPage.getCheckboxes().get(1);

        screenshot();
        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        screenshotInfo("After checking the first checkbox");
    }
}
