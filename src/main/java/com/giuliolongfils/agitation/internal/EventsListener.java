package com.giuliolongfils.agitation.internal;

import com.aventstack.extentreports.ExtentTest;
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

    protected void log(final Configuration.Event event, final Object... args) {
        final String message = String.format(event.getMessage(), args);
        final String noTagsMessage = message.replaceAll("<.*?>", "");

        switch (event.getLevel().levelStr) {
            case "OFF" -> {
            }
            case "TRACE" -> {
                log.trace(noTagsMessage);

                if (log.isTraceEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).debug(message);
                }
            }
            case "DEBUG" -> {
                log.debug(noTagsMessage);

                if (log.isDebugEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).debug(message);
                }
            }
            case "INFO" -> {
                log.info(noTagsMessage);

                if (log.isInfoEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).info(message);
                }
            }
            case "WARN" -> {
                log.warn(noTagsMessage);

                if (log.isWarnEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).warning(createLabel(message, YELLOW));
                }
            }
            default -> log.warn("Message '{}' won't be logged. Wrong log level set in configuration.yaml. Choose one among OFF, TRACE, DEBUG, INFO, WARN", message);
        }
    }

    @Override
    public void beforeAnyCall(Object target, Method method, Object[] args) {
        log(events.getBeforeAnyCall(), target, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyCall(Object target, Method method, Object[] args, Object result) {
        log(events.getAfterAnyCall(), target, method, Arrays.toString(args), result);
    }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        log(events.getOnError(), target, method, Arrays.toString(args), e.getMessage());
        log.error(e.getMessage(), e);
    }

    @Override
    public void beforeAnyWebDriverCall(WebDriver driver, Method method, Object[] args) {
        log(events.getBeforeAnyWebDriverCall(), driver, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyWebDriverCall(WebDriver driver, Method method, Object[] args, Object result) {
        log(events.getAfterAnyWebDriverCall(), driver, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeGet(WebDriver driver, String url) {
        log(events.getBeforeGet(), driver, url);
    }

    @Override
    public void afterGet(WebDriver driver, String url) {
        log(events.getAfterGet(), driver, url);
    }

    @Override
    public void beforeGetCurrentUrl(WebDriver driver) {
        log(events.getBeforeGetCurrentUrl(), driver);
    }

    @Override
    public void afterGetCurrentUrl(String result, WebDriver driver) {
        log(events.getAfterGetCurrentUrl(), result, driver);
    }

    @Override
    public void beforeGetTitle(WebDriver driver) {
        log(events.getBeforeGetTitle(), driver);
    }

    @Override
    public void afterGetTitle(WebDriver driver, String result) {
        log(events.getAfterGetTitle(), driver, result);
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        log(events.getBeforeFindElement(), driver, locator);
    }

    @Override
    public void afterFindElement(WebDriver driver, By locator, WebElement result) {
        log(events.getAfterFindElement(), driver, locator, result);
    }

    @Override
    public void beforeFindElements(WebDriver driver, By locator) {
        log(events.getBeforeFindElements(), driver, locator);
    }

    @Override
    public void afterFindElements(WebDriver driver, By locator, List<WebElement> result) {
        log(events.getAfterFindElements(), driver, locator, result);
    }

    @Override
    public void beforeGetPageSource(WebDriver driver) {
        log(events.getBeforeGetPageSource(), driver);
    }

    @Override
    public void afterGetPageSource(WebDriver driver, String result) {
        log(events.getAfterGetPageSource(), driver, result.replace("<", "&lt;").replace(">", "&gt;"));
    }

    @Override
    public void beforeClose(WebDriver driver) {
        log(events.getBeforeClose(), driver);
    }

    @Override
    public void afterClose(WebDriver driver) {
        log(events.getAfterClose(), driver);
    }

    @Override
    public void beforeQuit(WebDriver driver) {
        log(events.getBeforeQuit(), driver);
    }

    @Override
    public void afterQuit(WebDriver driver) {
        log(events.getAfterQuit(), driver);
    }

    @Override
    public void beforeGetWindowHandles(WebDriver driver) {
        log(events.getBeforeGetWindowHandles(), driver);
    }

    @Override
    public void afterGetWindowHandles(WebDriver driver, Set<String> result) {
        log(events.getAfterGetWindowHandles(), driver, result);
    }

    @Override
    public void beforeGetWindowHandle(WebDriver driver) {
        log(events.getBeforeGetWindowHandle(), driver);
    }

    @Override
    public void afterGetWindowHandle(WebDriver driver, String result) {
        log(events.getAfterGetWindowHandle(), driver, result);
    }

    @Override
    public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
        log(events.getBeforeExecuteScript(), driver, script, Arrays.toString(args));
    }

    @Override
    public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) {
        log(events.getAfterExecuteScript(), driver, script, Arrays.toString(args), result);
    }

    @Override
    public void beforeExecuteAsyncScript(WebDriver driver, String script, Object[] args) {
        log(events.getBeforeExecuteAsyncScript(), driver, script, Arrays.toString(args));
    }

    @Override
    public void afterExecuteAsyncScript(WebDriver driver, String script, Object[] args, Object result) {
        log(events.getAfterExecuteAsyncScript(), driver, script, Arrays.toString(args), result);
    }

    @Override
    public void beforePerform(WebDriver driver, Collection<Sequence> actions) {
        log(events.getBeforePerform(), driver, actions);
    }

    @Override
    public void afterPerform(WebDriver driver, Collection<Sequence> actions) {
        log(events.getAfterPerform(), driver, actions);
    }

    @Override
    public void beforeResetInputState(WebDriver driver) {
        log(events.getBeforeResetInputState(), driver);
    }

    @Override
    public void afterResetInputState(WebDriver driver) {
        log(events.getAfterResetInputState(), driver);
    }

    @Override
    public void beforeAnyWebElementCall(WebElement element, Method method, Object[] args) {
        log(events.getBeforeAnyWebElementCall(), element, method, element, Arrays.toString(args));
    }

    @Override
    public void afterAnyWebElementCall(WebElement element, Method method, Object[] args, Object result) {
        log(events.getAfterAnyWebElementCall(), element, method, element, Arrays.toString(args), result);
    }

    @Override
    public void beforeClick(WebElement element) {
        log(events.getBeforeClick(), element);
    }

    @Override
    public void afterClick(WebElement element) {
        log(events.getAfterClick(), element);
    }

    @Override
    public void beforeSubmit(WebElement element) {
        log(events.getBeforeSubmit(), element);
    }

    @Override
    public void afterSubmit(WebElement element) {
        log(events.getAfterSubmit(), element);
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        log(events.getBeforeSendKeys(), element, Arrays.toString(keysToSend));
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        log(events.getAfterSendKeys(), element, Arrays.toString(keysToSend));
    }

    @Override
    public void beforeClear(WebElement element) {
        log(events.getBeforeClear(), element);
    }

    @Override
    public void afterClear(WebElement element) {
        log(events.getAfterClear(), element);
    }

    @Override
    public void beforeGetTagName(WebElement element) {
        log(events.getBeforeGetTagName(), element);
    }

    @Override
    public void afterGetTagName(WebElement element, String result) {
        log(events.getAfterGetTagName(), element, result);
    }

    @Override
    public void beforeGetAttribute(WebElement element, String name) {
        log(events.getBeforeGetAttribute(), element, name);
    }

    @Override
    public void afterGetAttribute(WebElement element, String name, String result) {
        log(events.getAfterGetAttribute(), element, name, result);
    }

    @Override
    public void beforeIsSelected(WebElement element) {
        log(events.getBeforeIsSelected(), element);
    }

    @Override
    public void afterIsSelected(WebElement element, boolean result) {
        log(events.getAfterIsSelected(), element, result);
    }

    @Override
    public void beforeIsEnabled(WebElement element) {
        log(events.getBeforeIsEnabled(), element);
    }

    @Override
    public void afterIsEnabled(WebElement element, boolean result) {
        log(events.getAfterIsEnabled(), element, result);
    }

    @Override
    public void beforeGetText(WebElement element) {
        log(events.getBeforeGetText(), element);
    }

    @Override
    public void afterGetText(WebElement element, String result) {
        log(events.getAfterGetText(), element, result);
    }

    @Override
    public void beforeFindElement(WebElement element, By locator) {
        log(events.getBeforeFindWebElement(), element, locator);
    }

    @Override
    public void afterFindElement(WebElement element, By locator, WebElement result) {
        log(events.getAfterFindWebElement(), element, locator, result);
    }

    @Override
    public void beforeFindElements(WebElement element, By locator) {
        log(events.getBeforeFindWebElements(), element, locator);
    }

    @Override
    public void afterFindElements(WebElement element, By locator, List<WebElement> result) {
        log(events.getAfterFindWebElements(), element, locator, result);
    }

    @Override
    public void beforeIsDisplayed(WebElement element) {
        log(events.getBeforeIsDisplayed(), element);
    }

    @Override
    public void afterIsDisplayed(WebElement element, boolean result) {
        log(events.getAfterIsDisplayed(), element, result);
    }

    @Override
    public void beforeGetLocation(WebElement element) {
        log(events.getBeforeGetLocation(), element);
    }

    @Override
    public void afterGetLocation(WebElement element, Point result) {
        log(events.getAfterGetLocation(), element, result);
    }

    @Override
    public void beforeGetSize(WebElement element) {
        log(events.getBeforeGetSize(), element);
    }

    @Override
    public void afterGetSize(WebElement element, Dimension result) {
        log(events.getAfterGetSize(), element, result);
    }

    @Override
    public void beforeGetCssValue(WebElement element, String propertyName) {
        log(events.getBeforeGetCssValue(), element, propertyName);
    }

    @Override
    public void afterGetCssValue(WebElement element, String propertyName, String result) {
        log(events.getAfterGetCssValue(), element, propertyName, result);
    }

    @Override
    public void beforeAnyNavigationCall(WebDriver.Navigation navigation, Method method, Object[] args) {
        log(events.getBeforeAnyNavigationCall(),navigation, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyNavigationCall(WebDriver.Navigation navigation, Method method, Object[] args, Object result) {
        log(events.getAfterAnyNavigationCall(), navigation, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, String url) {
        log(events.getBeforeTo(), navigation, url);
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
        log(events.getAfterTo(), navigation, url);
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, URL url) {
        log(events.getBeforeTo(), navigation, url);
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, URL url) {
        log(events.getAfterTo(), navigation, url);
    }

    @Override
    public void beforeBack(WebDriver.Navigation navigation) {
        log(events.getBeforeBack(), navigation);
    }

    @Override
    public void afterBack(WebDriver.Navigation navigation) {
        log(events.getAfterBack(), navigation);
    }

    @Override
    public void beforeForward(WebDriver.Navigation navigation) {
        log(events.getBeforeForward(), navigation);
    }

    @Override
    public void afterForward(WebDriver.Navigation navigation) {
        log(events.getAfterForward(), navigation);
    }

    @Override
    public void beforeRefresh(WebDriver.Navigation navigation) {
        log(events.getBeforeRefresh(), navigation);
    }

    @Override
    public void afterRefresh(WebDriver.Navigation navigation) {
        log(events.getAfterRefresh(), navigation);
    }

    @Override
    public void beforeAnyAlertCall(Alert alert, Method method, Object[] args) {
        log(events.getBeforeAnyAlertCall(), alert, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyAlertCall(Alert alert, Method method, Object[] args, Object result) {
        log(events.getAfterAnyAlertCall(), alert, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeAccept(Alert alert) {
        log(events.getBeforeAccept(), alert);
    }

    @Override
    public void afterAccept(Alert alert) {
        log(events.getAfterAccept(), alert);
    }

    @Override
    public void beforeDismiss(Alert alert) {
        log(events.getBeforeDismiss(), alert);
    }

    @Override
    public void afterDismiss(Alert alert) {
        log(events.getAfterDismiss(), alert);
    }

    @Override
    public void beforeGetText(Alert alert) {
        log(events.getBeforeGetText(), alert);
    }

    @Override
    public void afterGetText(Alert alert, String result) {
        log(events.getAfterGetText(), alert, result);
    }

    @Override
    public void beforeSendKeys(Alert alert, String text) {
        log(events.getBeforeSendKeys(), alert, text);
    }

    @Override
    public void afterSendKeys(Alert alert, String text) {
        log(events.getAfterSendKeys(), alert, text);
    }

    @Override
    public void beforeAnyOptionsCall(WebDriver.Options options, Method method, Object[] args) {
        log(events.getBeforeAnyOptionsCall(), options, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyOptionsCall(WebDriver.Options options, Method method, Object[] args, Object result) {
        log(events.getAfterAnyOptionsCall(), options, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeAddCookie(WebDriver.Options options, Cookie cookie) {
        log(events.getBeforeAddCookie(), options, cookie);
    }

    @Override
    public void afterAddCookie(WebDriver.Options options, Cookie cookie) {
        log(events.getAfterAddCookie(), options, cookie);
    }

    @Override
    public void beforeDeleteCookieNamed(WebDriver.Options options, String name) {
        log(events.getBeforeDeleteCookieNamed(), options, name);
    }

    @Override
    public void afterDeleteCookieNamed(WebDriver.Options options, String name) {
        log(events.getAfterDeleteCookieNamed(), options, name);
    }

    @Override
    public void beforeDeleteCookie(WebDriver.Options options, Cookie cookie) {
        log(events.getBeforeDeleteCookie(), options, cookie);
    }

    @Override
    public void afterDeleteCookie(WebDriver.Options options, Cookie cookie) {
        log(events.getAfterDeleteCookie(), options, cookie);
    }

    @Override
    public void beforeDeleteAllCookies(WebDriver.Options options) {
        log(events.getBeforeDeleteAllCookies(), options);
    }

    @Override
    public void afterDeleteAllCookies(WebDriver.Options options) {
        log(events.getAfterDeleteAllCookies(), options);
    }

    @Override
    public void beforeGetCookies(WebDriver.Options options) {
        log(events.getBeforeGetCookies(), options);
    }

    @Override
    public void afterGetCookies(WebDriver.Options options, Set<Cookie> result) {
        log(events.getAfterGetCookies(), options, result);
    }

    @Override
    public void beforeGetCookieNamed(WebDriver.Options options, String name) {
        log(events.getBeforeGetCookieNamed(), options, name);
    }

    @Override
    public void afterGetCookieNamed(WebDriver.Options options, String name, Cookie result) {
        log(events.getAfterGetCookieNamed(), options, name, result);
    }

    @Override
    public void beforeAnyTimeoutsCall(WebDriver.Timeouts timeouts, Method method, Object[] args) {
        log(events.getBeforeAnyTimeoutsCall(), timeouts, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyTimeoutsCall(WebDriver.Timeouts timeouts, Method method, Object[] args, Object result) {
        log(events.getAfterAnyTimeoutsCall(), timeouts, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeImplicitlyWait(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getBeforeImplicitlyWait(), timeouts, duration);
    }

    @Override
    public void afterImplicitlyWait(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getAfterImplicitlyWait(), timeouts, duration);
    }

    @Override
    public void beforeSetScriptTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getBeforeSetScriptTimeout(), timeouts, duration);
    }

    @Override
    public void afterSetScriptTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getAfterSetScriptTimeout(), timeouts, duration);
    }

    @Override
    public void beforePageLoadTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getBeforePageLoadTimeout(), timeouts, duration);
    }

    @Override
    public void afterPageLoadTimeout(WebDriver.Timeouts timeouts, Duration duration) {
        log(events.getAfterPageLoadTimeout(), timeouts, duration);
    }

    @Override
    public void beforeAnyWindowCall(WebDriver.Window window, Method method, Object[] args) {
        log(events.getBeforeAnyWindowCall(), window, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyWindowCall(WebDriver.Window window, Method method, Object[] args, Object result) {
        log(events.getAfterAnyWindowCall(), window, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeGetSize(WebDriver.Window window) {
        log(events.getBeforeGetWindowSize(), window);
    }

    @Override
    public void afterGetSize(WebDriver.Window window, Dimension result) {
        log(events.getAfterGetWindowSize(), window, result);
    }

    @Override
    public void beforeSetSize(WebDriver.Window window, Dimension size) {
        log(events.getBeforeSetSize(), window, size);
    }

    @Override
    public void afterSetSize(WebDriver.Window window, Dimension size) {
        log(events.getAfterSetSize(), window, size);
    }

    @Override
    public void beforeGetPosition(WebDriver.Window window) {
        log(events.getBeforeGetPosition(), window);
    }

    @Override
    public void afterGetPosition(WebDriver.Window window, Point result) {
        log(events.getAfterGetPosition(), window, result);
    }

    @Override
    public void beforeSetPosition(WebDriver.Window window, Point position) {
        log(events.getBeforeSetPosition(), window, position);
    }

    @Override
    public void afterSetPosition(WebDriver.Window window, Point position) {
        log(events.getAfterSetPosition(), window, position);
    }

    @Override
    public void beforeMaximize(WebDriver.Window window) {
        log(events.getBeforeMaximize(), window);
    }

    @Override
    public void afterMaximize(WebDriver.Window window) {
        log(events.getAfterMaximize(), window);
    }

    @Override
    public void beforeFullscreen(WebDriver.Window window) {
        log(events.getBeforeFullscreen(), window);
    }

    @Override
    public void afterFullscreen(WebDriver.Window window) {
        log(events.getAfterFullscreen(), window);
    }
}
