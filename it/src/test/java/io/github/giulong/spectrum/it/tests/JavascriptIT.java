package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.enums.LocatorType;
import io.github.giulong.spectrum.it.pages.CheckboxPage;
import io.github.giulong.spectrum.it.pages.LandingPage;
import io.github.giulong.spectrum.it.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchShadowRootException;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
    public void testFindElementMethod() {
        driver.get(configuration.getApplication().getBaseUrl());

        assertEquals(landingPage.getFormLoginLink(), js.findElement(LocatorType.linkText, "Form Authentication"));
        assertEquals(landingPage.getFormLoginLink(), js.findElement(LocatorType.partialLinkText, "Form Aut"));

        js.click(landingPage.getFormLoginLink());
        loginPage.waitForPageLoading();

        final WebElement usernameField = loginPage.getUsername();
        final WebElement subHeader = loginPage.getSubHeader();
        final WebElement contentDiv = loginPage.getContentDiv();
        final WebElement form = loginPage.getForm();

        assertEquals(subHeader, js.findElement(contentDiv, LocatorType.className, "subheader"));
        assertEquals(form, js.findElement(contentDiv, LocatorType.tagName, "form"));
        assertEquals(usernameField, js.findElement(LocatorType.name, "username"));
        assertEquals(usernameField, js.findElement(LocatorType.cssSelector, "input[id='username'"));
        assertEquals(usernameField, js.findElement(LocatorType.xpath, "//*[@id='username']"));

        assertNotEquals(js.findElement(LocatorType.className, "row"), js.findElement(contentDiv, LocatorType.className, "row"));
    }

    @Test
    public void testFindElementsMethod() {
        driver.get(configuration.getApplication().getBaseUrl());

        final WebElement mainContentDiv = js.findElement(LocatorType.Id, "content");

        assertEquals(1, js.findElements(mainContentDiv, LocatorType.linkText, "Dropdown").size());
        assertEquals(1, js.findElements(LocatorType.linkText, "Dropdown").size());
        assertEquals(3, js.findElements(mainContentDiv, LocatorType.partialLinkText, "File").size());
        assertEquals(3, js.findElements(LocatorType.partialLinkText, "File").size());

        js.click(landingPage.getFormLoginLink());
        loginPage.waitForPageLoading();

        final WebElement form = loginPage.getForm();
        final WebElement contentDiv = loginPage.getContentDiv();
        final WebElement usernameField = loginPage.getUsername();

        // Every test use two test cases, one with context=document (not passed) and other one with context passed
        assertEquals(5, js.findElements(LocatorType.className, "row").size());
        assertEquals(2, js.findElements(form, LocatorType.className, "row").size());
        assertEquals(1, js.findElements(LocatorType.name, "login").size());
        assertEquals(1, js.findElements(contentDiv, LocatorType.name, "login").size());
        assertEquals(12, js.findElements(LocatorType.tagName, "div").size());
        assertEquals(4, js.findElements(form, LocatorType.tagName, "div").size());
        assertEquals(usernameField, js.findElements(LocatorType.cssSelector, "input[id='username'").getFirst());
        assertEquals(usernameField, js.findElements(form, LocatorType.cssSelector, "input[id='username'").getFirst());
        assertEquals(5, js.findElements(LocatorType.cssSelector, "div[class='row'").size());
        assertEquals(usernameField, js.findElements(LocatorType.xpath, "//*[@id='username']").getFirst());
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
