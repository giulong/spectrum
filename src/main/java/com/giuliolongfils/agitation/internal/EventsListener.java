package com.giuliolongfils.agitation.internal;

import ch.qos.logback.classic.Level;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.giuliolongfils.agitation.pojos.Configuration;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.aventstack.extentreports.markuputils.ExtentColor.YELLOW;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.giuliolongfils.agitation.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;

@Slf4j
@Builder
@Getter
public class EventsListener implements WebDriverListener {

    private ExtensionContext.Store store;
    private Configuration.Events events;

    protected void log(final Level level, final String msg) {
        switch (level.levelStr) {
            case "OFF" -> {
            }
            case "TRACE" -> {
                log.trace(msg);

                if (log.isTraceEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).debug(msg);
                }
            }
            case "DEBUG" -> {
                log.debug(msg);

                if (log.isDebugEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).debug(msg);
                }
            }
            case "INFO" -> {
                log.info(msg);

                if (log.isInfoEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).info(msg);
                }
            }
            case "WARN" -> {
                log.warn(msg);

                if (log.isWarnEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).warning(createLabel(msg, YELLOW));
                }
            }
            default -> log.warn("Message '{}' won't be logged. Wrong log level set in configuration.yaml. Choose one among OFF, DEBUG, INFO, WARN", msg);
        }
    }

    protected String buildUrlTagFor(final String url) {
        return String.format("<a href=\"%s\" target=\"_blank\">%s</a>", url, url);
    }

    @Override
    public void beforeAnyCall(Object target, Method method, Object[] args) {
        log(events.getBeforeAnyCall(), String.format("About to call method %s on target %s with args %s", method, target, Arrays.toString(args)));
    }

    @Override
    public void afterAnyCall(Object target, Method method, Object[] args, Object result) {
        log(events.getAfterAnyCall(), String.format("Method %s called on target %s with args %s returned %s", method, target, Arrays.toString(args), result));
    }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        log(events.getOnError(), String.format("Error when calling method %s on target %s with args %s", method, target, Arrays.toString(args)));
        log.error(e.getMessage(), e);
    }

    @Override
    public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) {
        log(events.getBeforeAnyWebDriverCall(), String.format("About to call method %s with args %s", method, Arrays.toString(args)));
    }

    @Override
    public void afterAnyWebDriverCall(WebDriver driver, Method method, Object[] args, Object result) {
        log(events.getAfterAnyWebDriverCall(), String.format("Method %s called with args %s returned %s", method, Arrays.toString(args), result));
    }

    @Override
    public void beforeGet(WebDriver driver, String url) {
        log(events.getBeforeGet(), String.format("About to get %s", buildUrlTagFor(url)));
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
        log(events.getAfterGet(), String.format("Landed on %s", url));
    }

    @Override
    public void beforeGetCurrentUrl(WebDriver driver) {
        log(events.getBeforeGetCurrentUrl(), String.format("About to get current url: %s", driver.getCurrentUrl()));
    }

    @Override
    public void afterGetCurrentUrl(String result, WebDriver driver) {
        log(events.getAfterGetCurrentUrl(), String.format("Landed on current url: %s", driver.getCurrentUrl()));
    }

    @Override
    public void beforeGetTitle(WebDriver driver) {
        log(events.getBeforeGetTitle(), String.format("About to get title %s", driver.getTitle()));
    }

    @Override
    public void afterGetTitle(WebDriver driver, String result) {
        log(events.getAfterGetTitle(), String.format("Done get title %s", result));
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        log(events.getBeforeFindElement(), String.format("About to find element %s", locator));
    }

    @Override
    public void afterFindElement(WebDriver driver, By locator, WebElement result) {
        log(events.getAfterFindElement(), String.format("Found element with locator %s : %s", locator, result));
    }

    @Override
    public void beforeFindElements(WebDriver driver, By locator) {
        log(events.getBeforeFindElements(), String.format("About to find elements %s", locator));
    }

    @Override
    public void afterFindElements(WebDriver driver, By locator, List<WebElement> result) {
        log(events.getAfterFindElements(), String.format("Found elements with locator %s : %s", locator, result));
    }

    @Override
    public void beforeGetPageSource(WebDriver driver) {
        log(events.getBeforeGetPageSource(), "About to get page source");
    }

    @Override
    public void afterGetPageSource(WebDriver driver, String result) {
        log(events.getAfterGetPageSource(), String.format("Got page source:\n%s", MarkupHelper.createCodeBlock(result).getMarkup()));
    }

    @Override
    public void beforeClose(WebDriver driver) {
        log(events.getBeforeClose(), "About to close");
    }

    @Override
    public void afterClose(WebDriver driver) {
        log(events.getAfterClose(), "Done closing");
    }

    @Override
    public void beforeQuit(WebDriver driver) {
        log(events.getBeforeQuit(), "About to quit");
    }

    @Override
    public void afterQuit(WebDriver driver) {
        log(events.getAfterQuit(), "Done quitting");
    }

    @Override
    public void beforeGetWindowHandles(WebDriver driver) {
        log(events.getBeforeGetWindowHandles(), "About to get window handles");
    }

    @Override
    public void afterGetWindowHandles(WebDriver driver, Set<String> result) {
        log(events.getAfterGetWindowHandles(), String.format("Got window handles %s", result));
    }

    @Override
    public void beforeGetWindowHandle(WebDriver driver) {
        log(events.getBeforeGetWindowHandle(), "About to get window handle");
    }

    @Override
    public void afterGetWindowHandle(WebDriver driver, String result) {
        log(events.getAfterGetWindowHandle(), String.format("Got window handle %s", result));
    }

    @Override
    public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
        log(events.getBeforeExecuteScript(), String.format("About to execute script %s with args %s", script, Arrays.toString(args)));
    }

    @Override
    public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) {
        log(events.getAfterExecuteScript(), String.format("Script %s with args %s returned %s", script, Arrays.toString(args), result));
    }

    @Override
    public void beforeExecuteAsyncScript(WebDriver driver, String script, Object[] args) {
        log(events.getBeforeExecuteAsyncScript(), String.format("About to execute async script %s with args %s", script, Arrays.toString(args)));
    }

    @Override
    public void afterExecuteAsyncScript(WebDriver driver, String script, Object[] args, Object result) {
        log(events.getAfterExecuteAsyncScript(), String.format("Async script %s with args %s returned %s", script, Arrays.toString(args), result));
    }

    @Override
    public void beforePerform(WebDriver driver, Collection<Sequence> actions) {
        log(events.getBeforePerform(), String.format("About to perform actions %s", actions));
    }

    @Override
    public void afterPerform(WebDriver driver, Collection<Sequence> actions) {
        log(events.getAfterPerform(), String.format("Performed actions %s", actions));
    }

    @Override
    public void beforeResetInputState(WebDriver driver) {
        log(events.getBeforeResetInputState(), "About to reset input state");
    }

    @Override
    public void afterResetInputState(WebDriver driver) {
        log(events.getAfterResetInputState(), "Done reset input state");
    }

    @Override
    public void beforeAnyWebElementCall(WebElement element, Method method, Object[] args) {
        log(events.getBeforeAnyWebElementCall(), String.format("About to call method %s in element %s with args %s", method, element, Arrays.toString(args)));
    }

    @Override
    public void afterAnyWebElementCall(WebElement element, Method method, Object[] args, Object result) {
        log(events.getAfterAnyWebElementCall(), String.format("Method %s called in element %s with args %s returned %s", method, element, Arrays.toString(args), result));
    }

    @Override
    public void beforeClick(WebElement element) {
        log(events.getBeforeClick(), String.format("About to click on %s", element));
    }

    @Override
    public void afterClick(WebElement element) {
        log(events.getAfterClick(), String.format("Clicked on %s", element));
    }

    @Override
    public void beforeSubmit(WebElement element) {
        log(events.getBeforeSubmit(), String.format("About to submit element %s", element));
    }

    @Override
    public void afterSubmit(WebElement element) {
        log(events.getAfterSubmit(), String.format("Element %s submitted", element));
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        log(events.getBeforeSendKeys(), String.format("About to send keys %s to %s", Arrays.toString(keysToSend), element));
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        log(events.getAfterSendKeys(), String.format("Sent keys %s to %s", Arrays.toString(keysToSend), element));
    }

    @Override
    public void beforeClear(WebElement element) {
        log(events.getBeforeClear(), String.format("About to clear %s", element));
    }

    @Override
    public void afterClear(WebElement element) {
        log(events.getAfterClear(), String.format("Element %s cleared", element));
    }

    @Override
    public void beforeGetTagName(WebElement element) {
        log(events.getBeforeGetTagName(), String.format("About to get tag name of %s", element));
    }

    @Override
    public void afterGetTagName(WebElement element, String result) {
        log(events.getAfterGetTagName(), String.format("Tag name of %s is %s", element, result));
    }

    @Override
    public void beforeGetAttribute(WebElement element, String name) {
        log(events.getBeforeGetAttribute(), String.format("About to get attribute %s on element %s", name, element));
    }

    @Override
    public void afterGetAttribute(WebElement element, String name, String result) {
        log(events.getAfterGetAttribute(), String.format("Attribute %s of element %s has value %s", name, element, result));
    }

    @Override
    public void beforeIsSelected(WebElement element) {
        log(events.getBeforeIsSelected(), String.format("About to check if element %s is selected", element));
    }

    @Override
    public void afterIsSelected(WebElement element, boolean result) {
        log(events.getAfterIsSelected(), String.format("Element %s selected? %s", element, result));
    }

    @Override
    public void beforeIsEnabled(WebElement element) {
        log(events.getBeforeIsEnabled(), String.format("About to check if element %s is enabled", element));
    }

    @Override
    public void afterIsEnabled(WebElement element, boolean result) {
        log(events.getAfterIsEnabled(), String.format("Element %s enabled? %s", element, result));
    }

    @Override
    public void beforeGetText(WebElement element) {
        log(events.getBeforeGetText(), String.format("About to get text of element %s", element));
    }

    @Override
    public void afterGetText(WebElement element, String result) {
        log(events.getAfterGetText(), String.format("Element %s has text %s", element, result));
    }

    @Override
    public void beforeFindElement(WebElement element, By locator) {
        log(events.getBeforeFindElement(), String.format("About to find element %s with locator %s", element, locator));
    }

    @Override
    public void afterFindElement(WebElement element, By locator, WebElement result) {
        log(events.getAfterFindElement(), String.format("Element %s with locator %s is %s", element, locator, result));
    }

    @Override
    public void beforeFindElements(WebElement element, By locator) {
        log(events.getBeforeFindElements(), String.format("About to find elements %s with locator %s", element, locator));
    }

    @Override
    public void afterFindElements(WebElement element, By locator, List<WebElement> result) {
        log(events.getAfterFindElements(), String.format("Elements %s with locator %s are %s", element, locator, result));
    }

    @Override
    public void beforeIsDisplayed(WebElement element) {
        log(events.getBeforeIsDisplayed(), String.format("About to check if element %s is displayed", element));
    }

    @Override
    public void afterIsDisplayed(WebElement element, boolean result) {
        log(events.getAfterIsDisplayed(), String.format("Element %s displayed? %s", element, result));
    }

    @Override
    public void beforeGetLocation(WebElement element) {
        log(events.getBeforeGetLocation(), String.format("About to get location of element %s", element));
    }

    @Override
    public void afterGetLocation(WebElement element, Point result) {
        log(events.getAfterGetLocation(), String.format("Element %s has location %s", element, result));
    }

    @Override
    public void beforeGetSize(WebElement element) {
        log(events.getBeforeGetSize(), String.format("About to get size of element %s", element));
    }

    @Override
    public void afterGetSize(WebElement element, Dimension result) {
        log(events.getAfterGetSize(), String.format("Element %s has size %s", element, result));
    }

    @Override
    public void beforeGetCssValue(WebElement element, String propertyName) {
        log(events.getBeforeGetCssValue(), String.format("About to get css value of property %s of element %s", propertyName, element));
    }

    @Override
    public void afterGetCssValue(WebElement element, String propertyName, String result) {
        log(events.getAfterGetCssValue(), String.format("Css value of property %s of element %s is %s", propertyName, element, result));
    }

    @Override
    public void beforeAnyNavigationCall(WebDriver.Navigation navigation, Method method, Object[] args) {
        log(events.getBeforeAnyNavigationCall(), String.format("About to navigate %s with method %s with args %s", navigation, method, Arrays.toString(args)));
    }

    @Override
    public void afterAnyNavigationCall(WebDriver.Navigation navigation, Method method, Object[] args, Object result) {
        log(events.getAfterAnyNavigationCall(), String.format("Navigation %s with method %s with args %s returned %s", navigation, method, Arrays.toString(args), result));
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, String url) {
        log(events.getBeforeTo(), String.format("About to navigate to %s", url));
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
        log(events.getAfterTo(), String.format("Navigated to %s", url));
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, URL url) {
        log(events.getBeforeTo(), String.format("About to navigate to %s", url));
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, URL url) {
        log(events.getAfterTo(), String.format("Navigated to %s", url));
    }

    @Override
    public void beforeBack(WebDriver.Navigation navigation) {
        log(events.getBeforeBack(), "About to navigate back");
    }

    @Override
    public void afterBack(WebDriver.Navigation navigation) {
        log(events.getAfterBack(), "Navigated back");
    }

    @Override
    public void beforeForward(WebDriver.Navigation navigation) {
        log(events.getBeforeForward(), "About to navigate forward");
    }

    @Override
    public void afterForward(WebDriver.Navigation navigation) {
        log(events.getAfterForward(), "Navigated forward");
    }

    @Override
    public void beforeRefresh(WebDriver.Navigation navigation) {
        log(events.getBeforeRefresh(), "About to refresh page");
    }

    @Override
    public void afterRefresh(WebDriver.Navigation navigation) {
        log(events.getAfterRefresh(), "Page refreshed");
    }

    @Override
    public void beforeAnyAlertCall(Alert alert, Method method, Object[] args) {
        log(events.getBeforeAnyAlertCall(), String.format("About to alert %s with method %s with args %s", alert, method, Arrays.toString(args)));
    }

    @Override
    public void afterAnyAlertCall(Alert alert, Method method, Object[] args, Object result) {
        log(events.getAfterAnyAlertCall(), String.format("Alerted %s with method %s with args %s returned %s", alert, method, Arrays.toString(args), result));
    }

    @Override
    public void beforeAccept(Alert alert) {
        log(events.getBeforeAccept(), String.format("About to accept alert %s", alert));
    }

    @Override
    public void afterAccept(Alert alert) {
        log(events.getAfterAccept(), String.format("Alert %s accepted", alert));
    }

    @Override
    public void beforeDismiss(Alert alert) {
        log(events.getBeforeDismiss(), String.format("About to dismiss alert %s", alert));
    }

    @Override
    public void afterDismiss(Alert alert) {
        log(events.getAfterDismiss(), String.format("Alert %s dismissed", alert));
    }

    @Override
    public void beforeGetText(Alert alert) {
        log(events.getBeforeGetText(), String.format("About to get alert %s text", alert));
    }

    @Override
    public void afterGetText(Alert alert, String result) {
        log(events.getAfterGetText(), String.format("Got text %s from alert %s", result, alert));
    }

    @Override
    public void beforeSendKeys(Alert alert, String text) {
        log(events.getBeforeSendKeys(), String.format("About to send keys %s to alert %s", text, alert));
    }

    @Override
    public void afterSendKeys(Alert alert, String text) {
        log(events.getAfterSendKeys(), String.format("Sent keys %s to alert %s", text, alert));
    }

    @Override
    public void beforeAnyOptionsCall(WebDriver.Options options, Method method, Object[] args) {
        log(events.getBeforeAnyOptionsCall(), String.format("About to call method %s with args %s", method, Arrays.toString(args)));
    }

    @Override
    public void afterAnyOptionsCall(WebDriver.Options options, Method method, Object[] args, Object result) {
        log(events.getAfterAnyOptionsCall(), String.format("Method %s called with args %s returned %s", method, Arrays.toString(args), result));
    }

    @Override
    public void beforeAddCookie(WebDriver.Options options, Cookie cookie) {
        log(events.getBeforeAddCookie(), String.format("About to add cookie %s", cookie));
    }

    @Override
    public void afterAddCookie(WebDriver.Options options, Cookie cookie) {
        log(events.getAfterAddCookie(), String.format("Cookie %s added", cookie));
    }

    @Override
    public void beforeDeleteCookieNamed(WebDriver.Options options, String name) {
        log(events.getBeforeDeleteCookieNamed(), String.format("About to delete cookie %s", name));
    }

    @Override
    public void afterDeleteCookieNamed(WebDriver.Options options, String name) {
        log(events.getAfterDeleteCookieNamed(), String.format("Cookie %s deleted", name));
    }

    @Override
    public void beforeDeleteCookie(WebDriver.Options options, Cookie cookie) {
        log(events.getBeforeDeleteCookie(), String.format("About to delete cookie %s", cookie));
    }

    @Override
    public void afterDeleteCookie(WebDriver.Options options, Cookie cookie) {
        log(events.getAfterDeleteCookie(), String.format("Cookie %s deleted", cookie));
    }

    @Override
    public void beforeDeleteAllCookies(WebDriver.Options options) {
        log(events.getBeforeDeleteAllCookies(), "About to delete all cookies");
    }

    @Override
    public void afterDeleteAllCookies(WebDriver.Options options) {
        log(events.getAfterDeleteAllCookies(), "Cookies deleted");
    }

    @Override
    public void beforeGetCookies(WebDriver.Options options) {
        log(events.getBeforeGetCookies(), "About to get cookies");
    }

    @Override
    public void afterGetCookies(WebDriver.Options options, Set<Cookie> result) {
        log(events.getAfterGetCookies(), String.format("Got cookies: %s", result));
    }

    @Override
    public void beforeGetCookieNamed(WebDriver.Options options, String name) {
        log(events.getBeforeGetCookieNamed(), String.format("About to get cookie %s", name));
    }

    @Override
    public void afterGetCookieNamed(WebDriver.Options options, String name, Cookie result) {
        log(events.getAfterGetCookieNamed(), String.format("Got cookie %s: %s", name, result));
    }

    @Override
    public void beforeAnyTimeoutsCall(WebDriver.Timeouts timeouts, Method method, Object[] args) {
        log(events.getBeforeAnyTimeoutsCall(), String.format("About to call timeout method %s with args %s", method, Arrays.toString(args)));
    }

    @Override
    public void afterAnyTimeoutsCall(WebDriver.Timeouts timeouts, Method method, Object[] args, Object result) {
        log(events.getAfterAnyTimeoutsCall(), String.format("Method %s called with args %s returned %s", method, Arrays.toString(args), result));
    }

    @Override
    public void beforeImplicitlyWait(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getBeforeImplicitlyWait(), String.format("About to wait implicitly for duration of %s", duration));
    }

    @Override
    public void afterImplicitlyWait(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getAfterImplicitlyWait(), String.format("Implicitly waited for duration of %s", duration));
    }

    @Override
    public void beforeSetScriptTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getBeforeSetScriptTimeout(), String.format("About to set script timeout of duration %s", duration));
    }

    @Override
    public void afterSetScriptTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getAfterSetScriptTimeout(), String.format("Set script timeout of duration %s", duration));
    }

    @Override
    public void beforePageLoadTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getBeforePageLoadTimeout(), String.format("About to reach the page load timeout of %s", duration));
    }

    @Override
    public void afterPageLoadTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getAfterPageLoadTimeout(), String.format("Got page load timeout of %s", duration));
    }

    @Override
    public void beforeAnyWindowCall(WebDriver.Window window, Method method, Object[] args) {
        log(events.getBeforeAnyWindowCall(), String.format("About to call window method %s with args %s", method, Arrays.toString(args)));
    }

    @Override
    public void afterAnyWindowCall(WebDriver.Window window, Method method, Object[] args, Object result) {
        log(events.getAfterAnyWindowCall(), String.format("Method %s called with args %s returned %s", method, Arrays.toString(args), result));
    }

    @Override
    public void beforeGetSize(WebDriver.Window window) {
        log(events.getBeforeGetSize(), "About to get window size");
    }

    @Override
    public void afterGetSize(WebDriver.Window window, Dimension result) {
        log(events.getAfterGetSize(), String.format("Window size is %s", result));
    }

    @Override
    public void beforeSetSize(WebDriver.Window window, Dimension size) {
        log(events.getBeforeSetSize(), String.format("About to set window size to %s", size));
    }

    @Override
    public void afterSetSize(WebDriver.Window window, Dimension size) {
        log(events.getAfterSetSize(), String.format("Set window size to %s", size));
    }

    @Override
    public void beforeGetPosition(WebDriver.Window window) {
        log(events.getBeforeGetPosition(), "About to get window position");
    }

    @Override
    public void afterGetPosition(WebDriver.Window window, Point result) {
        log(events.getAfterGetPosition(), String.format("Window position is %s", result));
    }

    @Override
    public void beforeSetPosition(WebDriver.Window window, Point position) {
        log(events.getBeforeSetPosition(), String.format("About to set window position to %s", position));
    }

    @Override
    public void afterSetPosition(WebDriver.Window window, Point position) {
        log(events.getAfterSetPosition(), String.format("Window position set to %s", position));
    }

    @Override
    public void beforeMaximize(WebDriver.Window window) {
        log(events.getBeforeMaximize(), "About to maximize window");
    }

    @Override
    public void afterMaximize(WebDriver.Window window) {
        log(events.getAfterMaximize(), "Window maximized");
    }

    @Override
    public void beforeFullscreen(WebDriver.Window window) {
        log(events.getBeforeFullscreen(), "About to set browser full screen");
    }

    @Override
    public void afterFullscreen(WebDriver.Window window) {
        log(events.getAfterFullscreen(), "Browser set full screen");
    }
}
