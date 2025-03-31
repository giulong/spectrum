package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.enums.LocatorType;
import io.github.giulong.spectrum.it.pages.JsCheckboxPage;
import io.github.giulong.spectrum.it.pages.JsLandingPage;
import io.github.giulong.spectrum.it.pages.JsLoginPage;
import io.github.giulong.spectrum.it.pages.JsShadowDomPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.urlToBe;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@DisplayName("JsWebElement")
class JsWebElementIT extends SpectrumTest<Void> {

    @SuppressWarnings("unused")
    private JsLoginPage jsLoginPage;

    @SuppressWarnings("unused")
    private JsLandingPage jsLandingPage;

    @SuppressWarnings("unused")
    private JsCheckboxPage jsCheckboxPage;

    @SuppressWarnings("unused")
    private JsShadowDomPage jsShadowDomPage;

    @Test
    void checkingJsWebElements() {
        driver.get(configuration.getApplication().getBaseUrl());

        pageLoadWait.until(visibilityOf(jsLandingPage.getTitle()));
        assertEquals("Welcome to the-internet", jsLandingPage.getTitle().getDomProperty("innerText"));

        jsLandingPage.getCheckboxLink().click();
        jsCheckboxPage.waitForPageLoading();

        final WebElement firstCheckbox = jsCheckboxPage.getCheckboxes().getFirst();
        final WebElement secondCheckbox = jsCheckboxPage.getCheckboxes().get(1);

        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        assertTrue(firstCheckbox.isEnabled());
        assertTrue(firstCheckbox.isDisplayed());

        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        // Take a screenshot with a custom message
        screenshotInfo("After checking the first checkbox");
    }

    @Test
    void testFindElementsMethod() {
        driver.get(configuration.getApplication().getBaseUrl());

        pageLoadWait.until(visibilityOf(jsLandingPage.getTitle()));
        final WebElement mainContentDiv = js.findElement(LocatorType.ID, "content");

        assertEquals(1, mainContentDiv.findElements(By.linkText("Dropdown")).size());
        assertEquals(3, mainContentDiv.findElements(By.partialLinkText("File")).size());

        jsLandingPage.getFormLoginLink().click();
        jsLoginPage.waitForPageLoading();

        final WebElement form = jsLoginPage.getForm();
        final WebElement contentDiv = jsLoginPage.getContentDiv();

        assertEquals("form", form.getTagName());

        assertEquals(2, form.findElements(By.className("row")).size());
        assertEquals(1, contentDiv.findElements(By.name("login")).size());
        assertEquals(4, form.findElements(By.tagName("div")).size());

        assertEquals("", form.findElements(By.cssSelector("input[id='username'")).getFirst().getText());
    }

    @Test
    void testInputFieldActions() {
        jsLoginPage.open();

        pageLoadWait.until(visibilityOf(jsLoginPage.getUsername()));
        final WebElement usernameField = jsLoginPage.getUsername();
        final WebElement passwordField = jsLoginPage.getPassword();
        final WebElement form = jsLoginPage.getForm();

        assertTrue(Objects.requireNonNull(usernameField.getDomProperty("value")).isEmpty());
        usernameField.sendKeys("tomsmith");
        assertEquals("tomsmith", form.findElements(By.tagName("input")).getFirst().getDomProperty("value"));
        assertEquals("tomsmith", form.findElement(By.tagName("input")).getDomProperty("value"));
        assertEquals("login", form.getDomAttribute("id"));
        assertEquals("login", form.getAttribute("id"));

        assertNotNull(usernameField.getSize());
        assertNotNull(usernameField.getLocation());
        assertNotNull(usernameField.getLocation());
        assertNotNull(usernameField.getRect().getDimension());

        usernameField.clear();
        assertTrue(Objects.requireNonNull(usernameField.getDomProperty("value")).isEmpty());
        usernameField.sendKeys("tomsmith");
        passwordField.sendKeys("SuperSecretPassword!");

        form.submit();
        pageLoadWait.until(urlToBe("https://the-internet.herokuapp.com/secure"));
        assertEquals("https://the-internet.herokuapp.com/secure", driver.getCurrentUrl());
    }

    @Test
    void shadowDom() {
        jsShadowDomPage.open();

        final WebElement span = jsShadowDomPage.getSpan();
        pageLoadWait.until(visibilityOf(jsShadowDomPage.getSpan()));

        final SearchContext shadowRoot = jsShadowDomPage.getMyParagraph().getShadowRoot();
        assertNotNull(shadowRoot);

        assertEquals("Let's have some different text!", span.getText());
        assertEquals("My default text", shadowRoot.findElement(By.cssSelector("slot")).getText());
    }
}
