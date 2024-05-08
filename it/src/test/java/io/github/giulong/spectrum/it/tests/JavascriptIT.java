package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.enums.LocatorType;
import io.github.giulong.spectrum.it.pages.CheckboxPage;
import io.github.giulong.spectrum.it.pages.LandingPage;
import io.github.giulong.spectrum.it.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchShadowRootException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Javascript")
public class JavascriptIT extends SpectrumTest<Void> {

    private static final Logger log = LoggerFactory.getLogger(JavascriptIT.class);

    private LandingPage landingPage;
    private CheckboxPage checkboxPage;
    private LoginPage loginPage;

    @Test
    public void testWithNoDisplayName() {
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        js.click(landingPage.getCheckboxLink());
        checkboxPage.waitForPageLoading();

        final WebElement firstCheckbox = checkboxPage.getCheckboxes().getFirst();
        final WebElement secondCheckbox = checkboxPage.getCheckboxes().get(1);

        assertFalse(js.isSelected(firstCheckbox));
        assertTrue(js.isSelected(secondCheckbox));

        assertTrue(js.isEnabled(firstCheckbox));
        assertTrue(js.isDisplayed(firstCheckbox));

        js.click(firstCheckbox);
        assertTrue(js.isSelected(firstCheckbox));

        // Take a screenshot with a custom message
        screenshotInfo("After checking the first checkbox");
    }

    @Test
    public void testWebElementGetMethods() {
        driver.get(configuration.getApplication().getBaseUrl());

        js.click(landingPage.getFormLoginLink());
        loginPage.waitForPageLoading();

        final WebElement form = loginPage.getForm();
        final WebElement usernameField = loginPage.getUsername();
        final WebElement contentDiv = loginPage.getContentDiv();

        assertEquals(js.getTagName(usernameField), "input");

        // Asserting that custom js methods return same value as selenium WebElement methods
        assertEquals(js.getSize(usernameField), usernameField.getSize());
        assertEquals(js.getRect(usernameField), usernameField.getRect());

        assertEquals(js.getDomAttribute(usernameField, "name"), "username");
        assertEquals(js.getAttribute(contentDiv, "class"), "large-12 columns");
        assertEquals(js.getText(form), "Username\nPassword\n Login");

        assertEquals(js.getCssValue(usernameField, "color"), "rgba(0, 0, 0, 0.75)");
        assertNull(js.getCssValue(usernameField, "background"));

        assertThrows(NoSuchShadowRootException.class, () -> js.getShadowRoot(usernameField));
        assertEquals(usernameField, js.findElement(Optional.empty(), LocatorType.ID, "username"));
    }

    @Test
    public void testInputFieldActions() {
        driver.get(configuration.getApplication().getBaseUrl());

        js.click(landingPage.getFormLoginLink());
        loginPage.waitForPageLoading();

        final WebElement usernameField = loginPage.getUsername();
        final WebElement passwordField = loginPage.getPassword();
        final WebElement form = loginPage.getForm();

        js.sendKeys(usernameField, "tomsmith");
        js.clear(usernameField);
        assertTrue(js.getDomProperty(usernameField, "value").isEmpty());
        js.sendKeys(usernameField, "tomsmith");
        js.sendKeys(passwordField, "SuperSecretPassword!");

        js.submit(form);
        assertEquals("https://the-internet.herokuapp.com/secure", driver.getCurrentUrl());
    }

}
