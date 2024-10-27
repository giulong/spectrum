package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.enums.LocatorType;
import io.github.giulong.spectrum.it.pages.CheckboxPage;
import io.github.giulong.spectrum.it.pages.LandingPage;
import io.github.giulong.spectrum.it.pages.LoginPage;
import io.github.giulong.spectrum.it.pages.ShadowDomPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;

public class JavascriptIT extends SpectrumTest<Void> {

    @SuppressWarnings("unused")
    private LandingPage landingPage;

    @SuppressWarnings("unused")
    private CheckboxPage checkboxPage;

    @SuppressWarnings("unused")
    private LoginPage loginPage;

    @SuppressWarnings("unused")
    private ShadowDomPage shadowDomPage;

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

        assertEquals(landingPage.getFormLoginLink(), js.findElement(LocatorType.LINK_TEXT, "Form Authentication"));
        assertEquals(landingPage.getFormLoginLink(), js.findElement(LocatorType.PARTIAL_LINK_TEXT, "Form Aut"));

        js.click(landingPage.getFormLoginLink());
        loginPage.waitForPageLoading();

        final WebElement usernameField = loginPage.getUsername();
        final WebElement subHeader = loginPage.getSubHeader();
        final WebElement contentDiv = loginPage.getContentDiv();
        final WebElement form = loginPage.getForm();

        assertEquals(subHeader, js.findElement(contentDiv, LocatorType.CLASS_NAME, "subheader"));
        assertEquals(form, js.findElement(contentDiv, LocatorType.TAG_NAME, "form"));
        assertEquals(usernameField, js.findElement(LocatorType.NAME, "username"));
        assertEquals(usernameField, js.findElement(LocatorType.CSS_SELECTOR, "input[id='username']"));
        assertEquals(usernameField, js.findElement(LocatorType.XPATH, "//*[@id='username']"));

        assertNotEquals(js.findElement(LocatorType.CLASS_NAME, "row"), js.findElement(contentDiv, LocatorType.CLASS_NAME, "row"));
    }

    @Test
    public void testFindElementsMethod() {
        driver.get(configuration.getApplication().getBaseUrl());

        final WebElement mainContentDiv = js.findElement(LocatorType.ID, "content");

        assertEquals(1, js.findElements(mainContentDiv, LocatorType.LINK_TEXT, "Dropdown").size());
        assertEquals(1, js.findElements(LocatorType.LINK_TEXT, "Dropdown").size());
        assertEquals(3, js.findElements(mainContentDiv, LocatorType.PARTIAL_LINK_TEXT, "File").size());
        assertEquals(3, js.findElements(LocatorType.PARTIAL_LINK_TEXT, "File").size());

        js.click(landingPage.getFormLoginLink());
        loginPage.waitForPageLoading();

        final WebElement form = loginPage.getForm();
        final WebElement contentDiv = loginPage.getContentDiv();
        final WebElement usernameField = loginPage.getUsername();

        // Every test use two test cases, one with context=document (not passed) and other one with context passed
        assertEquals(5, js.findElements(LocatorType.CLASS_NAME, "row").size());
        assertEquals(2, js.findElements(form, LocatorType.CLASS_NAME, "row").size());
        assertEquals(1, js.findElements(LocatorType.NAME, "login").size());
        assertEquals(1, js.findElements(contentDiv, LocatorType.NAME, "login").size());
        assertEquals(12, js.findElements(LocatorType.TAG_NAME, "div").size());
        assertEquals(4, js.findElements(form, LocatorType.TAG_NAME, "div").size());
        assertEquals(usernameField, js.findElements(LocatorType.CSS_SELECTOR, "input[id='username'").getFirst());
        assertEquals(usernameField, js.findElements(form, LocatorType.CSS_SELECTOR, "input[id='username'").getFirst());
        assertEquals(5, js.findElements(LocatorType.CSS_SELECTOR, "div[class='row'").size());
        assertEquals(usernameField, js.findElements(LocatorType.XPATH, "//*[@id='username']").getFirst());
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

        assertNotNull(js.getSize(usernameField));
        assertNotNull(js.getRect(usernameField));

        assertEquals(js.getDomAttribute(usernameField, "name"), "username");
        assertEquals(js.getAttribute(contentDiv, "class"), "large-12 columns");
        assertEquals(js.getText(form), "Username\nPassword\n Login");

        assertEquals(js.getCssValue(usernameField, "color"), "rgba(0, 0, 0, 0.75)");
        assertTrue(js.getCssValue(usernameField, "background").startsWith("rgb(255, 255, 255)"));

        assertNull(js.getShadowRoot(usernameField));
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
        pageLoadWait.until(urlToBe("https://the-internet.herokuapp.com/secure"));
        assertEquals("https://the-internet.herokuapp.com/secure", driver.getCurrentUrl());
    }

    @Test
    public void shadowDom() {
        shadowDomPage.open();

        final WebElement span = shadowDomPage.getSpan();

        final SearchContext shadowRoot = js.getShadowRoot(shadowDomPage.getMyParagraph());
        assertNotNull(shadowRoot);
        assertNull(js.getShadowRoot(span));

        assertEquals("Let's have some different text!", span.getText());
        assertEquals("My default text", shadowRoot.findElement(By.cssSelector("slot")).getText());
    }
}
