package io.github.giulong.spectrum.internals.web_driver_listeners;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mockito.Mockito.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

class AutoWaitWebDriverListenerTest {

    private MockedStatic<ExpectedConditions> expectedConditionsMockedStatic;

    @Mock
    private Actions actions;

    @Mock
    private ExpectedCondition<Boolean> booleanExpectedCondition;

    @Mock
    private ExpectedCondition<Boolean> andExpectedCondition;

    @Mock
    private ExpectedCondition<WebDriver> webDriverExpectedCondition;

    @Mock
    private ExpectedCondition<WebElement> webElementExpectedCondition;

    @Mock
    private WebElement webElement;

    @Mock
    private Pattern locatorPattern;

    @Mock
    private Matcher matcher;

    @Mock
    private WebDriverWait webDriverWait;

    @InjectMocks
    private AutoWaitWebDriverListener autoWaitWebDriverListener = new AutoWaitWebDriverListener(AutoWaitWebDriverListener.builder());

    @BeforeEach
    void beforeEach() {
        expectedConditionsMockedStatic = mockStatic(ExpectedConditions.class);
    }

    @AfterEach
    void afterEach() {
        expectedConditionsMockedStatic.close();
    }

    private void stubs() {
        // extractSelectorFrom
        final String fullWebElement = "fullWebElement";
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(webElement.toString()).thenReturn(fullWebElement);
        when(matcher.find()).thenReturn(true).thenReturn(false);
        when(matcher.group(1)).thenReturn(fullWebElement);

        when(actions.scrollToElement(webElement)).thenReturn(actions);
    }

    private void verifications() {
        verify(actions).perform();
        verify(webDriverWait).until(andExpectedCondition);
    }

    @Test
    @DisplayName("autoWait should use the webDriverWait to wait until all the provided conditions are satisfied")
    void autoWaitFor() {
        stubs();

        when(and(booleanExpectedCondition, webDriverExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.autoWaitFor(webElement, booleanExpectedCondition, webDriverExpectedCondition);

        verifications();
    }

    @Test
    @DisplayName("beforeClick should wait for the element to be clickable")
    void beforeClick() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeClick(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeSubmit should wait for the element to be clickable")
    void beforeSubmit() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeSubmit(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeSendKeys should wait for the element to be clickable")
    void beforeSendKeys() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeSendKeys(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeClear should wait for the element to be clickable")
    void beforeClear() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeClear(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeGetTagName should wait for the element to be clickable")
    void beforeGetTagName() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeGetTagName(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeGetAttribute should wait for the element to be clickable")
    void beforeGetAttribute() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeGetAttribute(webElement, "name");

        verifications();
    }

    @Test
    @DisplayName("beforeIsSelected should wait for the element to be clickable")
    void beforeIsSelected() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeIsSelected(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeIsEnabled should wait for the element to be visible")
    void beforeIsEnabled() {
        stubs();

        when(visibilityOf(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeIsEnabled(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeGetText should wait for the element to be clickable")
    void beforeGetText() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeGetText(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeGetLocation should wait for the element to be clickable")
    void beforeGetLocation() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeGetLocation(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeGetSize should wait for the element to be clickable")
    void beforeGetSize() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeGetSize(webElement);

        verifications();
    }

    @Test
    @DisplayName("beforeGetCssValue should wait for the element to be clickable")
    void beforeGetCssValue() {
        stubs();

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.beforeGetCssValue(webElement, "name");

        verifications();
    }

    @DisplayName("autoWaitFor should avoid scrolling to the provided web element is not interactable")
    @ParameterizedTest(name = "with exception {0}")
    @ValueSource(classes = {ElementNotInteractableException.class, MoveTargetOutOfBoundsException.class})
    void autoWaitForHidden(final Class<? extends RuntimeException> exception) {
        stubs();

        when(actions.scrollToElement(webElement)).thenThrow(exception);

        when(elementToBeClickable(webElement)).thenReturn(webElementExpectedCondition);
        when(and(webElementExpectedCondition)).thenReturn(andExpectedCondition);

        autoWaitWebDriverListener.autoWaitFor(webElement, webElementExpectedCondition);

        verify(webDriverWait).until(andExpectedCondition);
        verify(actions, never()).perform();
    }
}
