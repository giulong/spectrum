package io.github.giulong.spectrum.it_testbook.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_testbook.pages.CheckboxPage;
import io.github.giulong.spectrum.it_testbook.pages.LandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checkbox Page")
public class CheckboxIT extends SpectrumTest<Void> {

    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @Test
    public void testWithNoDisplayName() {
        webDriver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        landingPage.getCheckboxLink().click();

        final WebElement firstCheckbox = checkboxPage.getCheckboxes().get(0);
        final WebElement secondCheckbox = checkboxPage.getCheckboxes().get(1);

        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        screenshotInfo("After checking the first checkbox");
    }
}
