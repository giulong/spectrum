package com.giuliolongfils.spectrum.internal;

import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.spectrum.pojos.Configuration;
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
import static com.giuliolongfils.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;

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
                    store.get(EXTENT_TEST, ExtentTest.class).info(message);
                }
            }
            case "DEBUG" -> {
                log.debug(noTagsMessage);

                if (log.isDebugEnabled()) {
                    store.get(EXTENT_TEST, ExtentTest.class).info(message);
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
    public void beforeAnyCall(final Object target, final Method method, final Object[] args) {
        log(events.getBeforeAnyCall(), target, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyCall(final Object target, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyCall(), target, method, Arrays.toString(args), result);
    }

    @Override
    public void onError(final Object target, final Method method, final Object[] args, final InvocationTargetException e) {
        log(events.getOnError(), target, method, Arrays.toString(args), e.getMessage());
        log.error(e.getMessage(), e);
    }

    @Override
    public void beforeAnyWebDriverCall(final WebDriver driver, final Method method, final Object[] args) {
        log(events.getBeforeAnyWebDriverCall(), driver, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyWebDriverCall(final WebDriver driver, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyWebDriverCall(), driver, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeGet(final WebDriver driver, final String url) {
        log(events.getBeforeGet(), driver, url);
    }

    @Override
    public void afterGet(final WebDriver driver, final String url) {
        log(events.getAfterGet(), driver, url);
    }

    @Override
    public void beforeGetCurrentUrl(final WebDriver driver) {
        log(events.getBeforeGetCurrentUrl(), driver);
    }

    @Override
    public void afterGetCurrentUrl(final String result, final WebDriver driver) {
        log(events.getAfterGetCurrentUrl(), result, driver);
    }

    @Override
    public void beforeGetTitle(final WebDriver driver) {
        log(events.getBeforeGetTitle(), driver);
    }

    @Override
    public void afterGetTitle(final WebDriver driver, final String result) {
        log(events.getAfterGetTitle(), driver, result);
    }

    @Override
    public void beforeFindElement(final WebDriver driver, final By locator) {
        log(events.getBeforeFindElement(), driver, locator);
    }

    @Override
    public void afterFindElement(final WebDriver driver, final By locator, final WebElement result) {
        log(events.getAfterFindElement(), driver, locator, result);
    }

    @Override
    public void beforeFindElements(final WebDriver driver, final By locator) {
        log(events.getBeforeFindElements(), driver, locator);
    }

    @Override
    public void afterFindElements(final WebDriver driver, final By locator, final List<WebElement> result) {
        log(events.getAfterFindElements(), driver, locator, result);
    }

    @Override
    public void beforeGetPageSource(final WebDriver driver) {
        log(events.getBeforeGetPageSource(), driver);
    }

    @Override
    public void afterGetPageSource(final WebDriver driver, final String result) {
        log(events.getAfterGetPageSource(), driver, result.replace("<", "&lt;").replace(">", "&gt;"));
    }

    @Override
    public void beforeClose(final WebDriver driver) {
        log(events.getBeforeClose(), driver);
    }

    @Override
    public void afterClose(final WebDriver driver) {
        log(events.getAfterClose(), driver);
    }

    @Override
    public void beforeQuit(final WebDriver driver) {
        log(events.getBeforeQuit(), driver);
    }

    @Override
    public void afterQuit(final WebDriver driver) {
        log(events.getAfterQuit(), driver);
    }

    @Override
    public void beforeGetWindowHandles(final WebDriver driver) {
        log(events.getBeforeGetWindowHandles(), driver);
    }

    @Override
    public void afterGetWindowHandles(final WebDriver driver, final Set<String> result) {
        log(events.getAfterGetWindowHandles(), driver, result);
    }

    @Override
    public void beforeGetWindowHandle(final WebDriver driver) {
        log(events.getBeforeGetWindowHandle(), driver);
    }

    @Override
    public void afterGetWindowHandle(final WebDriver driver, final String result) {
        log(events.getAfterGetWindowHandle(), driver, result);
    }

    @Override
    public void beforeExecuteScript(final WebDriver driver, final String script, final Object[] args) {
        log(events.getBeforeExecuteScript(), driver, script, Arrays.toString(args));
    }

    @Override
    public void afterExecuteScript(final WebDriver driver, final String script, final Object[] args, final Object result) {
        log(events.getAfterExecuteScript(), driver, script, Arrays.toString(args), result);
    }

    @Override
    public void beforeExecuteAsyncScript(final WebDriver driver, final String script, final Object[] args) {
        log(events.getBeforeExecuteAsyncScript(), driver, script, Arrays.toString(args));
    }

    @Override
    public void afterExecuteAsyncScript(final WebDriver driver, final String script, final Object[] args, final Object result) {
        log(events.getAfterExecuteAsyncScript(), driver, script, Arrays.toString(args), result);
    }

    @Override
    public void beforePerform(final WebDriver driver, final Collection<Sequence> actions) {
        log(events.getBeforePerform(), driver, actions);
    }

    @Override
    public void afterPerform(final WebDriver driver, final Collection<Sequence> actions) {
        log(events.getAfterPerform(), driver, actions);
    }

    @Override
    public void beforeResetInputState(final WebDriver driver) {
        log(events.getBeforeResetInputState(), driver);
    }

    @Override
    public void afterResetInputState(final WebDriver driver) {
        log(events.getAfterResetInputState(), driver);
    }

    @Override
    public void beforeAnyWebElementCall(final WebElement element, final Method method, final Object[] args) {
        log(events.getBeforeAnyWebElementCall(), element, method, element, Arrays.toString(args));
    }

    @Override
    public void afterAnyWebElementCall(final WebElement element, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyWebElementCall(), element, method, element, Arrays.toString(args), result);
    }

    @Override
    public void beforeClick(final WebElement element) {
        log(events.getBeforeClick(), element);
    }

    @Override
    public void afterClick(final WebElement element) {
        log(events.getAfterClick(), element);
    }

    @Override
    public void beforeSubmit(final WebElement element) {
        log(events.getBeforeSubmit(), element);
    }

    @Override
    public void afterSubmit(final WebElement element) {
        log(events.getAfterSubmit(), element);
    }

    @Override
    public void beforeSendKeys(final WebElement element, final CharSequence... keysToSend) {
        log(events.getBeforeSendKeys(), element, Arrays.toString(keysToSend));
    }

    @Override
    public void afterSendKeys(final WebElement element, final CharSequence... keysToSend) {
        log(events.getAfterSendKeys(), element, Arrays.toString(keysToSend));
    }

    @Override
    public void beforeClear(final WebElement element) {
        log(events.getBeforeClear(), element);
    }

    @Override
    public void afterClear(final WebElement element) {
        log(events.getAfterClear(), element);
    }

    @Override
    public void beforeGetTagName(final WebElement element) {
        log(events.getBeforeGetTagName(), element);
    }

    @Override
    public void afterGetTagName(final WebElement element, final String result) {
        log(events.getAfterGetTagName(), element, result);
    }

    @Override
    public void beforeGetAttribute(final WebElement element, final String name) {
        log(events.getBeforeGetAttribute(), element, name);
    }

    @Override
    public void afterGetAttribute(final WebElement element, final String name, final String result) {
        log(events.getAfterGetAttribute(), element, name, result);
    }

    @Override
    public void beforeIsSelected(final WebElement element) {
        log(events.getBeforeIsSelected(), element);
    }

    @Override
    public void afterIsSelected(final WebElement element, final boolean result) {
        log(events.getAfterIsSelected(), element, result);
    }

    @Override
    public void beforeIsEnabled(final WebElement element) {
        log(events.getBeforeIsEnabled(), element);
    }

    @Override
    public void afterIsEnabled(final WebElement element, final boolean result) {
        log(events.getAfterIsEnabled(), element, result);
    }

    @Override
    public void beforeGetText(final WebElement element) {
        log(events.getBeforeGetText(), element);
    }

    @Override
    public void afterGetText(final WebElement element, final String result) {
        log(events.getAfterGetText(), element, result);
    }

    @Override
    public void beforeFindElement(final WebElement element, final By locator) {
        log(events.getBeforeFindWebElement(), element, locator);
    }

    @Override
    public void afterFindElement(final WebElement element, final By locator, final WebElement result) {
        log(events.getAfterFindWebElement(), element, locator, result);
    }

    @Override
    public void beforeFindElements(final WebElement element, final By locator) {
        log(events.getBeforeFindWebElements(), element, locator);
    }

    @Override
    public void afterFindElements(final WebElement element, final By locator, final List<WebElement> result) {
        log(events.getAfterFindWebElements(), element, locator, result);
    }

    @Override
    public void beforeIsDisplayed(final WebElement element) {
        log(events.getBeforeIsDisplayed(), element);
    }

    @Override
    public void afterIsDisplayed(final WebElement element, final boolean result) {
        log(events.getAfterIsDisplayed(), element, result);
    }

    @Override
    public void beforeGetLocation(final WebElement element) {
        log(events.getBeforeGetLocation(), element);
    }

    @Override
    public void afterGetLocation(final WebElement element, final Point result) {
        log(events.getAfterGetLocation(), element, result);
    }

    @Override
    public void beforeGetSize(final WebElement element) {
        log(events.getBeforeGetSize(), element);
    }

    @Override
    public void afterGetSize(final WebElement element, final Dimension result) {
        log(events.getAfterGetSize(), element, result);
    }

    @Override
    public void beforeGetCssValue(final WebElement element, final String propertyName) {
        log(events.getBeforeGetCssValue(), element, propertyName);
    }

    @Override
    public void afterGetCssValue(final WebElement element, final String propertyName, final String result) {
        log(events.getAfterGetCssValue(), element, propertyName, result);
    }

    @Override
    public void beforeAnyNavigationCall(final WebDriver.Navigation navigation, final Method method, final Object[] args) {
        log(events.getBeforeAnyNavigationCall(),navigation, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyNavigationCall(final WebDriver.Navigation navigation, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyNavigationCall(), navigation, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeTo(final WebDriver.Navigation navigation, final String url) {
        log(events.getBeforeTo(), navigation, url);
    }

    @Override
    public void afterTo(final WebDriver.Navigation navigation, final String url) {
        log(events.getAfterTo(), navigation, url);
    }

    @Override
    public void beforeTo(final WebDriver.Navigation navigation, final URL url) {
        log(events.getBeforeTo(), navigation, url);
    }

    @Override
    public void afterTo(final WebDriver.Navigation navigation, final URL url) {
        log(events.getAfterTo(), navigation, url);
    }

    @Override
    public void beforeBack(final WebDriver.Navigation navigation) {
        log(events.getBeforeBack(), navigation);
    }

    @Override
    public void afterBack(final WebDriver.Navigation navigation) {
        log(events.getAfterBack(), navigation);
    }

    @Override
    public void beforeForward(final WebDriver.Navigation navigation) {
        log(events.getBeforeForward(), navigation);
    }

    @Override
    public void afterForward(final WebDriver.Navigation navigation) {
        log(events.getAfterForward(), navigation);
    }

    @Override
    public void beforeRefresh(final WebDriver.Navigation navigation) {
        log(events.getBeforeRefresh(), navigation);
    }

    @Override
    public void afterRefresh(final WebDriver.Navigation navigation) {
        log(events.getAfterRefresh(), navigation);
    }

    @Override
    public void beforeAnyAlertCall(final Alert alert, final Method method, final Object[] args) {
        log(events.getBeforeAnyAlertCall(), alert, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyAlertCall(final Alert alert, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyAlertCall(), alert, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeAccept(final Alert alert) {
        log(events.getBeforeAccept(), alert);
    }

    @Override
    public void afterAccept(final Alert alert) {
        log(events.getAfterAccept(), alert);
    }

    @Override
    public void beforeDismiss(final Alert alert) {
        log(events.getBeforeDismiss(), alert);
    }

    @Override
    public void afterDismiss(final Alert alert) {
        log(events.getAfterDismiss(), alert);
    }

    @Override
    public void beforeGetText(final Alert alert) {
        log(events.getBeforeGetText(), alert);
    }

    @Override
    public void afterGetText(final Alert alert, final String result) {
        log(events.getAfterGetText(), alert, result);
    }

    @Override
    public void beforeSendKeys(final Alert alert, final String text) {
        log(events.getBeforeSendKeys(), alert, text);
    }

    @Override
    public void afterSendKeys(final Alert alert, final String text) {
        log(events.getAfterSendKeys(), alert, text);
    }

    @Override
    public void beforeAnyOptionsCall(final WebDriver.Options options, final Method method, final Object[] args) {
        log(events.getBeforeAnyOptionsCall(), options, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyOptionsCall(final WebDriver.Options options, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyOptionsCall(), options, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeAddCookie(final WebDriver.Options options, final Cookie cookie) {
        log(events.getBeforeAddCookie(), options, cookie);
    }

    @Override
    public void afterAddCookie(final WebDriver.Options options, final Cookie cookie) {
        log(events.getAfterAddCookie(), options, cookie);
    }

    @Override
    public void beforeDeleteCookieNamed(final WebDriver.Options options, final String name) {
        log(events.getBeforeDeleteCookieNamed(), options, name);
    }

    @Override
    public void afterDeleteCookieNamed(final WebDriver.Options options, final String name) {
        log(events.getAfterDeleteCookieNamed(), options, name);
    }

    @Override
    public void beforeDeleteCookie(final WebDriver.Options options, final Cookie cookie) {
        log(events.getBeforeDeleteCookie(), options, cookie);
    }

    @Override
    public void afterDeleteCookie(final WebDriver.Options options, final Cookie cookie) {
        log(events.getAfterDeleteCookie(), options, cookie);
    }

    @Override
    public void beforeDeleteAllCookies(final WebDriver.Options options) {
        log(events.getBeforeDeleteAllCookies(), options);
    }

    @Override
    public void afterDeleteAllCookies(final WebDriver.Options options) {
        log(events.getAfterDeleteAllCookies(), options);
    }

    @Override
    public void beforeGetCookies(final WebDriver.Options options) {
        log(events.getBeforeGetCookies(), options);
    }

    @Override
    public void afterGetCookies(final WebDriver.Options options, final Set<Cookie> result) {
        log(events.getAfterGetCookies(), options, result);
    }

    @Override
    public void beforeGetCookieNamed(final WebDriver.Options options, final String name) {
        log(events.getBeforeGetCookieNamed(), options, name);
    }

    @Override
    public void afterGetCookieNamed(final WebDriver.Options options, final String name, final Cookie result) {
        log(events.getAfterGetCookieNamed(), options, name, result);
    }

    @Override
    public void beforeAnyTimeoutsCall(final WebDriver.Timeouts timeouts, final Method method, final Object[] args) {
        log(events.getBeforeAnyTimeoutsCall(), timeouts, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyTimeoutsCall(final WebDriver.Timeouts timeouts, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyTimeoutsCall(), timeouts, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeImplicitlyWait(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getBeforeImplicitlyWait(), timeouts, duration);
    }

    @Override
    public void afterImplicitlyWait(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getAfterImplicitlyWait(), timeouts, duration);
    }

    @Override
    public void beforeSetScriptTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getBeforeSetScriptTimeout(), timeouts, duration);
    }

    @Override
    public void afterSetScriptTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getAfterSetScriptTimeout(), timeouts, duration);
    }

    @Override
    public void beforePageLoadTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getBeforePageLoadTimeout(), timeouts, duration);
    }

    @Override
    public void afterPageLoadTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getAfterPageLoadTimeout(), timeouts, duration);
    }

    @Override
    public void beforeAnyWindowCall(final WebDriver.Window window, final Method method, final Object[] args) {
        log(events.getBeforeAnyWindowCall(), window, method, Arrays.toString(args));
    }

    @Override
    public void afterAnyWindowCall(final WebDriver.Window window, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyWindowCall(), window, method, Arrays.toString(args), result);
    }

    @Override
    public void beforeGetSize(final WebDriver.Window window) {
        log(events.getBeforeGetWindowSize(), window);
    }

    @Override
    public void afterGetSize(final WebDriver.Window window, final Dimension result) {
        log(events.getAfterGetWindowSize(), window, result);
    }

    @Override
    public void beforeSetSize(final WebDriver.Window window, final Dimension size) {
        log(events.getBeforeSetSize(), window, size);
    }

    @Override
    public void afterSetSize(final WebDriver.Window window, final Dimension size) {
        log(events.getAfterSetSize(), window, size);
    }

    @Override
    public void beforeGetPosition(final WebDriver.Window window) {
        log(events.getBeforeGetPosition(), window);
    }

    @Override
    public void afterGetPosition(final WebDriver.Window window, final Point result) {
        log(events.getAfterGetPosition(), window, result);
    }

    @Override
    public void beforeSetPosition(final WebDriver.Window window, final Point position) {
        log(events.getBeforeSetPosition(), window, position);
    }

    @Override
    public void afterSetPosition(final WebDriver.Window window, final Point position) {
        log(events.getAfterSetPosition(), window, position);
    }

    @Override
    public void beforeMaximize(final WebDriver.Window window) {
        log(events.getBeforeMaximize(), window);
    }

    @Override
    public void afterMaximize(final WebDriver.Window window) {
        log(events.getAfterMaximize(), window);
    }

    @Override
    public void beforeFullscreen(final WebDriver.Window window) {
        log(events.getBeforeFullscreen(), window);
    }

    @Override
    public void afterFullscreen(final WebDriver.Window window) {
        log(events.getAfterFullscreen(), window);
    }
}
