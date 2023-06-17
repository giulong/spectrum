package com.github.giulong.spectrum.internals;

import com.aventstack.extentreports.ExtentTest;
import com.github.giulong.spectrum.pojos.Configuration;
import lombok.Builder;
import lombok.Generated;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.aventstack.extentreports.markuputils.ExtentColor.YELLOW;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;

@Slf4j
@Builder
@Getter
public class EventsListener implements WebDriverListener {

    private static final Pattern LOCATOR_PATTERN = Pattern.compile("\\s->\\s(?<locator>[\\w:\\s\\-.#]+)");

    private ExtensionContext.Store store;
    private Configuration.Events events;

    protected String extractSelectorFrom(final WebElement webElement) {
        final String fullWebElement = webElement.toString();
        final Matcher matcher = LOCATOR_PATTERN.matcher(fullWebElement);

        final List<String> locators = new ArrayList<>();
        while (matcher.find()) {
            locators.add(matcher.group("locator"));
        }

        return String.join(" -> ", locators);
    }

    protected List<String> parse(final Object[] args) {
        return Arrays.stream(args)
                .map(arg -> (arg instanceof WebElement)
                        ? extractSelectorFrom((WebElement) arg)
                        : String.valueOf(arg))
                .toList();
    }

    protected void log(final Configuration.Event event, final Object... args) {
        switch (event.getLevel().levelStr) {
            case "OFF" -> {
            }
            case "TRACE" -> {
                if (log.isTraceEnabled()) {
                    final String message = String.format(event.getMessage(), parse(args).toArray());
                    final String noTagsMessage = message.replaceAll("<.*?>", "");

                    log.trace(noTagsMessage);
                    store.get(EXTENT_TEST, ExtentTest.class).info(message);
                }
            }
            case "DEBUG" -> {
                if (log.isDebugEnabled()) {
                    final String message = String.format(event.getMessage(), parse(args).toArray());
                    final String noTagsMessage = message.replaceAll("<.*?>", "");

                    log.debug(noTagsMessage);
                    store.get(EXTENT_TEST, ExtentTest.class).info(message);
                }
            }
            case "INFO" -> {
                if (log.isInfoEnabled()) {
                    final String message = String.format(event.getMessage(), parse(args).toArray());
                    final String noTagsMessage = message.replaceAll("<.*?>", "");

                    log.info(noTagsMessage);
                    store.get(EXTENT_TEST, ExtentTest.class).info(message);
                }
            }
            case "WARN" -> {
                if (log.isWarnEnabled()) {
                    final String message = String.format(event.getMessage(), parse(args).toArray());
                    final String noTagsMessage = message.replaceAll("<.*?>", "");

                    log.warn(noTagsMessage);
                    store.get(EXTENT_TEST, ExtentTest.class).warning(createLabel(message, YELLOW));
                }
            }
            default -> {
                final String message = String.format(event.getMessage(), parse(args).toArray());
                log.warn("Message '{}' won't be logged. Wrong log level set in configuration.yaml. Choose one among OFF, TRACE, DEBUG, INFO, WARN", message);
            }
        }
    }

    @Override
    @Generated
    public void beforeAnyCall(final Object target, final Method method, final Object[] args) {
        log(events.getBeforeAnyCall(), target, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyCall(final Object target, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyCall(), target, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void onError(final Object target, final Method method, final Object[] args, final InvocationTargetException e) {
        log(events.getOnError(), target, method, Arrays.toString(args), e.getMessage());
        log.error(e.getMessage(), e);
    }

    @Override
    @Generated
    public void beforeAnyWebDriverCall(final WebDriver driver, final Method method, final Object[] args) {
        log(events.getBeforeAnyWebDriverCall(), driver, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyWebDriverCall(final WebDriver driver, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyWebDriverCall(), driver, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeGet(final WebDriver driver, final String url) {
        log(events.getBeforeGet(), driver, url);
    }

    @Override
    @Generated
    public void afterGet(final WebDriver driver, final String url) {
        log(events.getAfterGet(), driver, url);
    }

    @Override
    @Generated
    public void beforeGetCurrentUrl(final WebDriver driver) {
        log(events.getBeforeGetCurrentUrl(), driver);
    }

    @Override
    @Generated
    public void afterGetCurrentUrl(final String result, final WebDriver driver) {
        log(events.getAfterGetCurrentUrl(), result, driver);
    }

    @Override
    @Generated
    public void beforeGetTitle(final WebDriver driver) {
        log(events.getBeforeGetTitle(), driver);
    }

    @Override
    @Generated
    public void afterGetTitle(final WebDriver driver, final String result) {
        log(events.getAfterGetTitle(), driver, result);
    }

    @Override
    @Generated
    public void beforeFindElement(final WebDriver driver, final By locator) {
        log(events.getBeforeFindElement(), driver, locator);
    }

    @Override
    @Generated
    public void afterFindElement(final WebDriver driver, final By locator, final WebElement result) {
        log(events.getAfterFindElement(), driver, locator, result);
    }

    @Override
    @Generated
    public void beforeFindElements(final WebDriver driver, final By locator) {
        log(events.getBeforeFindElements(), driver, locator);
    }

    @Override
    @Generated
    public void afterFindElements(final WebDriver driver, final By locator, final List<WebElement> result) {
        log(events.getAfterFindElements(), driver, locator, result);
    }

    @Override
    @Generated
    public void beforeGetPageSource(final WebDriver driver) {
        log(events.getBeforeGetPageSource(), driver);
    }

    @Override
    @Generated
    public void afterGetPageSource(final WebDriver driver, final String result) {
        log(events.getAfterGetPageSource(), driver, result.replace("<", "&lt;").replace(">", "&gt;"));
    }

    @Override
    @Generated
    public void beforeClose(final WebDriver driver) {
        log(events.getBeforeClose(), driver);
    }

    @Override
    @Generated
    public void afterClose(final WebDriver driver) {
        log(events.getAfterClose(), driver);
    }

    @Override
    @Generated
    public void beforeQuit(final WebDriver driver) {
        log(events.getBeforeQuit(), driver);
    }

    @Override
    @Generated
    public void afterQuit(final WebDriver driver) {
        log(events.getAfterQuit(), driver);
    }

    @Override
    @Generated
    public void beforeGetWindowHandles(final WebDriver driver) {
        log(events.getBeforeGetWindowHandles(), driver);
    }

    @Override
    @Generated
    public void afterGetWindowHandles(final WebDriver driver, final Set<String> result) {
        log(events.getAfterGetWindowHandles(), driver, result);
    }

    @Override
    @Generated
    public void beforeGetWindowHandle(final WebDriver driver) {
        log(events.getBeforeGetWindowHandle(), driver);
    }

    @Override
    @Generated
    public void afterGetWindowHandle(final WebDriver driver, final String result) {
        log(events.getAfterGetWindowHandle(), driver, result);
    }

    @Override
    @Generated
    public void beforeExecuteScript(final WebDriver driver, final String script, final Object[] args) {
        log(events.getBeforeExecuteScript(), driver, script, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterExecuteScript(final WebDriver driver, final String script, final Object[] args, final Object result) {
        log(events.getAfterExecuteScript(), driver, script, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeExecuteAsyncScript(final WebDriver driver, final String script, final Object[] args) {
        log(events.getBeforeExecuteAsyncScript(), driver, script, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterExecuteAsyncScript(final WebDriver driver, final String script, final Object[] args, final Object result) {
        log(events.getAfterExecuteAsyncScript(), driver, script, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforePerform(final WebDriver driver, final Collection<Sequence> actions) {
        log(events.getBeforePerform(), driver, actions);
    }

    @Override
    @Generated
    public void afterPerform(final WebDriver driver, final Collection<Sequence> actions) {
        log(events.getAfterPerform(), driver, actions);
    }

    @Override
    @Generated
    public void beforeResetInputState(final WebDriver driver) {
        log(events.getBeforeResetInputState(), driver);
    }

    @Override
    @Generated
    public void afterResetInputState(final WebDriver driver) {
        log(events.getAfterResetInputState(), driver);
    }

    @Override
    @Generated
    public void beforeAnyWebElementCall(final WebElement element, final Method method, final Object[] args) {
        log(events.getBeforeAnyWebElementCall(), element, method, element, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyWebElementCall(final WebElement element, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyWebElementCall(), element, method, element, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeClick(final WebElement element) {
        log(events.getBeforeClick(), element);
    }

    @Override
    @Generated
    public void afterClick(final WebElement element) {
        log(events.getAfterClick(), element);
    }

    @Override
    @Generated
    public void beforeSubmit(final WebElement element) {
        log(events.getBeforeSubmit(), element);
    }

    @Override
    @Generated
    public void afterSubmit(final WebElement element) {
        log(events.getAfterSubmit(), element);
    }

    @Override
    @Generated
    public void beforeSendKeys(final WebElement element, final CharSequence... keysToSend) {
        log(events.getBeforeSendKeys(), element, Arrays.toString(keysToSend));
    }

    @Override
    @Generated
    public void afterSendKeys(final WebElement element, final CharSequence... keysToSend) {
        log(events.getAfterSendKeys(), element, Arrays.toString(keysToSend));
    }

    @Override
    @Generated
    public void beforeClear(final WebElement element) {
        log(events.getBeforeClear(), element);
    }

    @Override
    @Generated
    public void afterClear(final WebElement element) {
        log(events.getAfterClear(), element);
    }

    @Override
    @Generated
    public void beforeGetTagName(final WebElement element) {
        log(events.getBeforeGetTagName(), element);
    }

    @Override
    @Generated
    public void afterGetTagName(final WebElement element, final String result) {
        log(events.getAfterGetTagName(), element, result);
    }

    @Override
    @Generated
    public void beforeGetAttribute(final WebElement element, final String name) {
        log(events.getBeforeGetAttribute(), element, name);
    }

    @Override
    @Generated
    public void afterGetAttribute(final WebElement element, final String name, final String result) {
        log(events.getAfterGetAttribute(), element, name, result);
    }

    @Override
    @Generated
    public void beforeIsSelected(final WebElement element) {
        log(events.getBeforeIsSelected(), element);
    }

    @Override
    @Generated
    public void afterIsSelected(final WebElement element, final boolean result) {
        log(events.getAfterIsSelected(), element, result);
    }

    @Override
    @Generated
    public void beforeIsEnabled(final WebElement element) {
        log(events.getBeforeIsEnabled(), element);
    }

    @Override
    @Generated
    public void afterIsEnabled(final WebElement element, final boolean result) {
        log(events.getAfterIsEnabled(), element, result);
    }

    @Override
    @Generated
    public void beforeGetText(final WebElement element) {
        log(events.getBeforeGetText(), element);
    }

    @Override
    @Generated
    public void afterGetText(final WebElement element, final String result) {
        log(events.getAfterGetText(), element, result);
    }

    @Override
    @Generated
    public void beforeFindElement(final WebElement element, final By locator) {
        log(events.getBeforeFindWebElement(), element, locator);
    }

    @Override
    @Generated
    public void afterFindElement(final WebElement element, final By locator, final WebElement result) {
        log(events.getAfterFindWebElement(), element, locator, result);
    }

    @Override
    @Generated
    public void beforeFindElements(final WebElement element, final By locator) {
        log(events.getBeforeFindWebElements(), element, locator);
    }

    @Override
    @Generated
    public void afterFindElements(final WebElement element, final By locator, final List<WebElement> result) {
        log(events.getAfterFindWebElements(), element, locator, result);
    }

    @Override
    @Generated
    public void beforeIsDisplayed(final WebElement element) {
        log(events.getBeforeIsDisplayed(), element);
    }

    @Override
    @Generated
    public void afterIsDisplayed(final WebElement element, final boolean result) {
        log(events.getAfterIsDisplayed(), element, result);
    }

    @Override
    @Generated
    public void beforeGetLocation(final WebElement element) {
        log(events.getBeforeGetLocation(), element);
    }

    @Override
    @Generated
    public void afterGetLocation(final WebElement element, final Point result) {
        log(events.getAfterGetLocation(), element, result);
    }

    @Override
    @Generated
    public void beforeGetSize(final WebElement element) {
        log(events.getBeforeGetSize(), element);
    }

    @Override
    @Generated
    public void afterGetSize(final WebElement element, final Dimension result) {
        log(events.getAfterGetSize(), element, result);
    }

    @Override
    @Generated
    public void beforeGetCssValue(final WebElement element, final String propertyName) {
        log(events.getBeforeGetCssValue(), element, propertyName);
    }

    @Override
    @Generated
    public void afterGetCssValue(final WebElement element, final String propertyName, final String result) {
        log(events.getAfterGetCssValue(), element, propertyName, result);
    }

    @Override
    @Generated
    public void beforeAnyNavigationCall(final WebDriver.Navigation navigation, final Method method, final Object[] args) {
        log(events.getBeforeAnyNavigationCall(), navigation, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyNavigationCall(final WebDriver.Navigation navigation, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyNavigationCall(), navigation, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeTo(final WebDriver.Navigation navigation, final String url) {
        log(events.getBeforeTo(), navigation, url);
    }

    @Override
    @Generated
    public void afterTo(final WebDriver.Navigation navigation, final String url) {
        log(events.getAfterTo(), navigation, url);
    }

    @Override
    @Generated
    public void beforeTo(final WebDriver.Navigation navigation, final URL url) {
        log(events.getBeforeTo(), navigation, url);
    }

    @Override
    @Generated
    public void afterTo(final WebDriver.Navigation navigation, final URL url) {
        log(events.getAfterTo(), navigation, url);
    }

    @Override
    @Generated
    public void beforeBack(final WebDriver.Navigation navigation) {
        log(events.getBeforeBack(), navigation);
    }

    @Override
    @Generated
    public void afterBack(final WebDriver.Navigation navigation) {
        log(events.getAfterBack(), navigation);
    }

    @Override
    @Generated
    public void beforeForward(final WebDriver.Navigation navigation) {
        log(events.getBeforeForward(), navigation);
    }

    @Override
    @Generated
    public void afterForward(final WebDriver.Navigation navigation) {
        log(events.getAfterForward(), navigation);
    }

    @Override
    @Generated
    public void beforeRefresh(final WebDriver.Navigation navigation) {
        log(events.getBeforeRefresh(), navigation);
    }

    @Override
    @Generated
    public void afterRefresh(final WebDriver.Navigation navigation) {
        log(events.getAfterRefresh(), navigation);
    }

    @Override
    @Generated
    public void beforeAnyAlertCall(final Alert alert, final Method method, final Object[] args) {
        log(events.getBeforeAnyAlertCall(), alert, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyAlertCall(final Alert alert, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyAlertCall(), alert, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeAccept(final Alert alert) {
        log(events.getBeforeAccept(), alert);
    }

    @Override
    @Generated
    public void afterAccept(final Alert alert) {
        log(events.getAfterAccept(), alert);
    }

    @Override
    @Generated
    public void beforeDismiss(final Alert alert) {
        log(events.getBeforeDismiss(), alert);
    }

    @Override
    @Generated
    public void afterDismiss(final Alert alert) {
        log(events.getAfterDismiss(), alert);
    }

    @Override
    @Generated
    public void beforeGetText(final Alert alert) {
        log(events.getBeforeGetText(), alert);
    }

    @Override
    @Generated
    public void afterGetText(final Alert alert, final String result) {
        log(events.getAfterGetText(), alert, result);
    }

    @Override
    @Generated
    public void beforeSendKeys(final Alert alert, final String text) {
        log(events.getBeforeSendKeys(), alert, text);
    }

    @Override
    @Generated
    public void afterSendKeys(final Alert alert, final String text) {
        log(events.getAfterSendKeys(), alert, text);
    }

    @Override
    @Generated
    public void beforeAnyOptionsCall(final WebDriver.Options options, final Method method, final Object[] args) {
        log(events.getBeforeAnyOptionsCall(), options, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyOptionsCall(final WebDriver.Options options, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyOptionsCall(), options, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeAddCookie(final WebDriver.Options options, final Cookie cookie) {
        log(events.getBeforeAddCookie(), options, cookie);
    }

    @Override
    @Generated
    public void afterAddCookie(final WebDriver.Options options, final Cookie cookie) {
        log(events.getAfterAddCookie(), options, cookie);
    }

    @Override
    @Generated
    public void beforeDeleteCookieNamed(final WebDriver.Options options, final String name) {
        log(events.getBeforeDeleteCookieNamed(), options, name);
    }

    @Override
    @Generated
    public void afterDeleteCookieNamed(final WebDriver.Options options, final String name) {
        log(events.getAfterDeleteCookieNamed(), options, name);
    }

    @Override
    @Generated
    public void beforeDeleteCookie(final WebDriver.Options options, final Cookie cookie) {
        log(events.getBeforeDeleteCookie(), options, cookie);
    }

    @Override
    @Generated
    public void afterDeleteCookie(final WebDriver.Options options, final Cookie cookie) {
        log(events.getAfterDeleteCookie(), options, cookie);
    }

    @Override
    @Generated
    public void beforeDeleteAllCookies(final WebDriver.Options options) {
        log(events.getBeforeDeleteAllCookies(), options);
    }

    @Override
    @Generated
    public void afterDeleteAllCookies(final WebDriver.Options options) {
        log(events.getAfterDeleteAllCookies(), options);
    }

    @Override
    @Generated
    public void beforeGetCookies(final WebDriver.Options options) {
        log(events.getBeforeGetCookies(), options);
    }

    @Override
    @Generated
    public void afterGetCookies(final WebDriver.Options options, final Set<Cookie> result) {
        log(events.getAfterGetCookies(), options, result);
    }

    @Override
    @Generated
    public void beforeGetCookieNamed(final WebDriver.Options options, final String name) {
        log(events.getBeforeGetCookieNamed(), options, name);
    }

    @Override
    @Generated
    public void afterGetCookieNamed(final WebDriver.Options options, final String name, final Cookie result) {
        log(events.getAfterGetCookieNamed(), options, name, result);
    }

    @Override
    @Generated
    public void beforeAnyTimeoutsCall(final WebDriver.Timeouts timeouts, final Method method, final Object[] args) {
        log(events.getBeforeAnyTimeoutsCall(), timeouts, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyTimeoutsCall(final WebDriver.Timeouts timeouts, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyTimeoutsCall(), timeouts, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeImplicitlyWait(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getBeforeImplicitlyWait(), timeouts, duration);
    }

    @Override
    @Generated
    public void afterImplicitlyWait(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getAfterImplicitlyWait(), timeouts, duration);
    }

    @Override
    @Generated
    public void beforeSetScriptTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getBeforeSetScriptTimeout(), timeouts, duration);
    }

    @Override
    @Generated
    public void afterSetScriptTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getAfterSetScriptTimeout(), timeouts, duration);
    }

    @Override
    @Generated
    public void beforePageLoadTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getBeforePageLoadTimeout(), timeouts, duration);
    }

    @Override
    @Generated
    public void afterPageLoadTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        log(events.getAfterPageLoadTimeout(), timeouts, duration);
    }

    @Override
    @Generated
    public void beforeAnyWindowCall(final WebDriver.Window window, final Method method, final Object[] args) {
        log(events.getBeforeAnyWindowCall(), window, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyWindowCall(final WebDriver.Window window, final Method method, final Object[] args, final Object result) {
        log(events.getAfterAnyWindowCall(), window, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeGetSize(final WebDriver.Window window) {
        log(events.getBeforeGetWindowSize(), window);
    }

    @Override
    @Generated
    public void afterGetSize(final WebDriver.Window window, final Dimension result) {
        log(events.getAfterGetWindowSize(), window, result);
    }

    @Override
    @Generated
    public void beforeSetSize(final WebDriver.Window window, final Dimension size) {
        log(events.getBeforeSetSize(), window, size);
    }

    @Override
    @Generated
    public void afterSetSize(final WebDriver.Window window, final Dimension size) {
        log(events.getAfterSetSize(), window, size);
    }

    @Override
    @Generated
    public void beforeGetPosition(final WebDriver.Window window) {
        log(events.getBeforeGetPosition(), window);
    }

    @Override
    @Generated
    public void afterGetPosition(final WebDriver.Window window, final Point result) {
        log(events.getAfterGetPosition(), window, result);
    }

    @Override
    @Generated
    public void beforeSetPosition(final WebDriver.Window window, final Point position) {
        log(events.getBeforeSetPosition(), window, position);
    }

    @Override
    @Generated
    public void afterSetPosition(final WebDriver.Window window, final Point position) {
        log(events.getAfterSetPosition(), window, position);
    }

    @Override
    @Generated
    public void beforeMaximize(final WebDriver.Window window) {
        log(events.getBeforeMaximize(), window);
    }

    @Override
    @Generated
    public void afterMaximize(final WebDriver.Window window) {
        log(events.getAfterMaximize(), window);
    }

    @Override
    @Generated
    public void beforeFullscreen(final WebDriver.Window window) {
        log(events.getBeforeFullscreen(), window);
    }

    @Override
    @Generated
    public void afterFullscreen(final WebDriver.Window window) {
        log(events.getAfterFullscreen(), window);
    }
}
