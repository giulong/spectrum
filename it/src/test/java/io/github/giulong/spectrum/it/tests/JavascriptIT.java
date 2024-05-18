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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Javascript")
public class JavascriptIT extends SpectrumTest<Void> {

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

        // Asserting that custom js methods return same value as selenium WebElement methods
        assertEquals(js.getSize(usernameField), usernameField.getSize());
        assertEquals(js.getRect(usernameField), usernameField.getRect());

        assertEquals(js.getDomAttribute(usernameField, "name"), "username");
        assertEquals(js.getAttribute(contentDiv, "class"), "large-12 columns");
        assertEquals(js.getText(form), "Username\nPassword\n Login");

        assertEquals(js.getCssValue(usernameField, "color"), "rgba(0, 0, 0, 0.75)");
        assertEquals(js.getCssValue(usernameField, "background"), "rgb(255, 255, 255) none repeat scroll 0% 0% / auto padding-box border-box");

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