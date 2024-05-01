package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it.pages.JsCheckboxPage;
import io.github.giulong.spectrum.it.pages.JsLandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsWebElement")
public class JsWebElementIT extends SpectrumTest<Void> {

    private JsLandingPage jsLandingPage;

    private JsCheckboxPage jsCheckboxPage;

    @Test
    public void checkingJsWebElements() {
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", jsLandingPage.getTitle().getText());

        jsLandingPage.getCheckboxLink().click();
        jsCheckboxPage.waitForPageLoading();

        final WebElement firstCheckbox = jsCheckboxPage.getCheckboxes().getFirst();
        final WebElement secondCheckbox = jsCheckboxPage.getCheckboxes().get(1);

        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        // Take a screenshot with a custom message
        screenshotInfo("After checking the first checkbox");
    }
}
