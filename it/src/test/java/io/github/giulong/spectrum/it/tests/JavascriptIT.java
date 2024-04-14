package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it.pages.CheckboxPage;
import io.github.giulong.spectrum.it.pages.LandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Javascript")
public class JavascriptIT extends SpectrumTest<Void> {

    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @Test
    public void testWithNoDisplayName() {
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        js.click(landingPage.getCheckboxLink());
        checkboxPage.waitForPageLoading();

        final WebElement firstCheckbox = checkboxPage.getCheckboxes().getFirst();
        final WebElement secondCheckbox = checkboxPage.getCheckboxes().get(1);

        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        js.click(firstCheckbox);
        assertTrue(firstCheckbox.isSelected());

        // Take a screenshot with a custom message
        screenshotInfo("After checking the first checkbox");
    }
}
