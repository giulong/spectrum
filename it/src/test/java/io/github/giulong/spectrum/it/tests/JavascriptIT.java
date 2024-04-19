package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it.pages.CheckboxPage;
import io.github.giulong.spectrum.it.pages.KeyPressesPage;
import io.github.giulong.spectrum.it.pages.LandingPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Javascript")
public class JavascriptIT extends SpectrumTest<Void> {

    private static final Logger log = LoggerFactory.getLogger(JavascriptIT.class);
    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    private KeyPressesPage keyPressesPage;

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

    @Test
    public void testWebElementGetMethods() {
        driver.get(configuration.getApplication().getBaseUrl());

        js.click(landingPage.getCheckboxLink());
        checkboxPage.waitForPageLoading();

        final WebElement testwebElement = checkboxPage.getCheckboxes().getFirst();

        // Asserting that custom js methods return same value as selenium WebElement methods
        assertEquals(js.getTagName(testwebElement), testwebElement.getTagName());
        assertEquals(js.getSize(testwebElement), testwebElement.getSize());
        assertEquals(js.getRect(testwebElement), testwebElement.getRect());
    }

    @Test
    public void testInputFieldActions() {
        driver.get(configuration.getApplication().getBaseUrl());

        js.click(landingPage.getKeyPressesLink());
        keyPressesPage.waitForPageLoading();

        final WebElement inputField = keyPressesPage.getInputField();

        js.sendKeys(inputField, "testKeys");
        assertEquals("testKeys", inputField.getAttribute("value"));

        js.clear(inputField);
        assertTrue(inputField.getAttribute("value").isEmpty());
    }

}
