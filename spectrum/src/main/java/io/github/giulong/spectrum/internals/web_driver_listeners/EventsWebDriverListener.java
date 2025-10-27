package io.github.giulong.spectrum.internals.web_driver_listeners;

import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static io.github.giulong.spectrum.enums.Frame.AUTO_BEFORE;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Configuration.Drivers.Events;
import io.github.giulong.spectrum.utils.web_driver_events.WebDriverEvent;
import io.github.giulong.spectrum.utils.web_driver_events.WebDriverEventConsumer;

import lombok.Generated;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Sequence;
import org.slf4j.event.Level;

@Slf4j
@SuperBuilder
public class EventsWebDriverListener extends SpectrumWebDriverListener {

    private static final Pattern SECURED_PATTERN = Pattern.compile("@Secured@(?<key>.*)@Secured@");

    private Events events;
    private List<WebDriverEventConsumer> consumers;

    List<String> parse(final Object[] args) {
        return Arrays
                .stream(args)
                .map(arg -> arg instanceof WebElement ? extractSelectorFrom((WebElement) arg) : String.valueOf(arg))
                .toList();
    }

    @SneakyThrows
    void listenTo(final Frame frame, final Configuration.Drivers.Event event, final Object... args) {
        final Level level = event.getLevel();

        if (level == null || !log.isEnabledForLevel(level)) {
            return;
        }

        final long wait = event.getWait();
        if (wait > 0) {
            log.debug("Waiting {} ms before event processing", wait);
            Thread.sleep(wait);
        }

        final WebDriverEvent webDriverEvent = WebDriverEvent
                .builder()
                .frame(frame)
                .level(level)
                .args(Arrays.asList(args))
                .message(String.format(event.getMessage(), parse(args).toArray()))
                .build();

        consumers
                .stream()
                .filter(WebDriverEventConsumer::isEnabled)
                .forEach(consumer -> consumer.accept(webDriverEvent));
    }

    @Override
    @Generated
    public void beforeAnyCall(final Object target, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyCall(), target, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyCall(final Object target, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyCall(), target, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void onError(final Object target, final Method method, final Object[] args, final InvocationTargetException e) {
        listenTo(AUTO_AFTER, events.getOnError(), target, method, Arrays.toString(args), e.getMessage());
    }

    @Override
    @Generated
    public void beforeAnyWebDriverCall(final WebDriver webDriver, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyWebDriverCall(), webDriver, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyWebDriverCall(final WebDriver webDriver, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyWebDriverCall(), webDriver, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeGet(final WebDriver webDriver, final String url) {
        listenTo(AUTO_BEFORE, events.getBeforeGet(), webDriver, url);
    }

    @Override
    @Generated
    public void afterGet(final WebDriver webDriver, final String url) {
        listenTo(AUTO_AFTER, events.getAfterGet(), webDriver, url);
    }

    @Override
    @Generated
    public void beforeGetCurrentUrl(final WebDriver webDriver) {
        listenTo(AUTO_BEFORE, events.getBeforeGetCurrentUrl(), webDriver);
    }

    @Override
    @Generated
    public void afterGetCurrentUrl(final WebDriver webDriver, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetCurrentUrl(), result, webDriver);
    }

    @Override
    @Generated
    public void beforeGetTitle(final WebDriver webDriver) {
        listenTo(AUTO_BEFORE, events.getBeforeGetTitle(), webDriver);
    }

    @Override
    @Generated
    public void afterGetTitle(final WebDriver webDriver, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetTitle(), webDriver, result);
    }

    @Override
    @Generated
    public void beforeFindElement(final WebDriver webDriver, final By locator) {
        listenTo(AUTO_BEFORE, events.getBeforeFindElement(), webDriver, locator);
    }

    @Override
    @Generated
    public void afterFindElement(final WebDriver webDriver, final By locator, final WebElement result) {
        listenTo(AUTO_AFTER, events.getAfterFindElement(), webDriver, locator, result);
    }

    @Override
    @Generated
    public void beforeFindElements(final WebDriver webDriver, final By locator) {
        listenTo(AUTO_BEFORE, events.getBeforeFindElements(), webDriver, locator);
    }

    @Override
    @Generated
    public void afterFindElements(final WebDriver webDriver, final By locator, final List<WebElement> result) {
        listenTo(AUTO_AFTER, events.getAfterFindElements(), webDriver, locator, result);
    }

    @Override
    @Generated
    public void beforeGetPageSource(final WebDriver webDriver) {
        listenTo(AUTO_BEFORE, events.getBeforeGetPageSource(), webDriver);
    }

    @Override
    @Generated
    public void afterGetPageSource(final WebDriver webDriver, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetPageSource(), webDriver, result.replace("<", "&lt;").replace(">", "&gt;"));
    }

    @Override
    @Generated
    public void beforeClose(final WebDriver webDriver) {
        listenTo(AUTO_BEFORE, events.getBeforeClose(), webDriver);
    }

    @Override
    @Generated
    public void afterClose(final WebDriver webDriver) {
        listenTo(AUTO_AFTER, events.getAfterClose(), webDriver);
    }

    @Override
    @Generated
    public void beforeQuit(final WebDriver webDriver) {
        listenTo(AUTO_BEFORE, events.getBeforeQuit(), webDriver);
    }

    @Override
    @Generated
    public void afterQuit(final WebDriver webDriver) {
        listenTo(AUTO_AFTER, events.getAfterQuit(), webDriver);
    }

    @Override
    @Generated
    public void beforeGetWindowHandles(final WebDriver webDriver) {
        listenTo(AUTO_BEFORE, events.getBeforeGetWindowHandles(), webDriver);
    }

    @Override
    @Generated
    public void afterGetWindowHandles(final WebDriver webDriver, final Set<String> result) {
        listenTo(AUTO_AFTER, events.getAfterGetWindowHandles(), webDriver, result);
    }

    @Override
    @Generated
    public void beforeGetWindowHandle(final WebDriver webDriver) {
        listenTo(AUTO_BEFORE, events.getBeforeGetWindowHandle(), webDriver);
    }

    @Override
    @Generated
    public void afterGetWindowHandle(final WebDriver webDriver, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetWindowHandle(), webDriver, result);
    }

    @Override
    @Generated
    public void beforeExecuteScript(final WebDriver webDriver, final String script, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeExecuteScript(), webDriver, script, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterExecuteScript(final WebDriver webDriver, final String script, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterExecuteScript(), webDriver, script, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeExecuteAsyncScript(final WebDriver webDriver, final String script, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeExecuteAsyncScript(), webDriver, script, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterExecuteAsyncScript(final WebDriver webDriver, final String script, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterExecuteAsyncScript(), webDriver, script, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforePerform(final WebDriver webDriver, final Collection<Sequence> actions) {
        listenTo(AUTO_BEFORE, events.getBeforePerform(), webDriver, actions);
    }

    @Override
    @Generated
    public void afterPerform(final WebDriver webDriver, final Collection<Sequence> actions) {
        listenTo(AUTO_AFTER, events.getAfterPerform(), webDriver, actions);
    }

    @Override
    @Generated
    public void beforeResetInputState(final WebDriver webDriver) {
        listenTo(AUTO_BEFORE, events.getBeforeResetInputState(), webDriver);
    }

    @Override
    @Generated
    public void afterResetInputState(final WebDriver webDriver) {
        listenTo(AUTO_AFTER, events.getAfterResetInputState(), webDriver);
    }

    @Override
    @Generated
    public void beforeAnyWebElementCall(final WebElement element, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyWebElementCall(), element, method, element, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyWebElementCall(final WebElement element, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyWebElementCall(), element, method, element, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeClick(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeClick(), element);
    }

    @Override
    @Generated
    public void afterClick(final WebElement element) {
        listenTo(AUTO_AFTER, events.getAfterClick(), element);
    }

    @Override
    @Generated
    public void beforeSubmit(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeSubmit(), element);
    }

    @Override
    @Generated
    public void afterSubmit(final WebElement element) {
        listenTo(AUTO_AFTER, events.getAfterSubmit(), element);
    }

    @Override
    public void beforeSendKeys(final WebElement element, final CharSequence... keysToSend) {
        if (isSecured(keysToSend)) {
            log.debug("Masking keys to send to @Secured webElement");
            listenTo(AUTO_BEFORE, events.getBeforeSendKeys(), element, "[***]");

            return;
        }

        listenTo(AUTO_BEFORE, events.getBeforeSendKeys(), element, Arrays.toString(keysToSend));
    }

    @Override
    public void afterSendKeys(final WebElement element, final CharSequence... keysToSend) {
        if (isSecured(keysToSend)) {
            log.debug("Masking keys sent to @Secured webElement");
            listenTo(AUTO_AFTER, events.getAfterSendKeys(), element, "[***]");

            return;
        }

        listenTo(AUTO_AFTER, events.getAfterSendKeys(), element, Arrays.toString(keysToSend));
    }

    @Override
    @Generated
    public void beforeClear(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeClear(), element);
    }

    @Override
    @Generated
    public void afterClear(final WebElement element) {
        listenTo(AUTO_AFTER, events.getAfterClear(), element);
    }

    @Override
    @Generated
    public void beforeGetTagName(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeGetTagName(), element);
    }

    @Override
    @Generated
    public void afterGetTagName(final WebElement element, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetTagName(), element, result);
    }

    @Override
    @Generated
    public void beforeGetAttribute(final WebElement element, final String name) {
        listenTo(AUTO_BEFORE, events.getBeforeGetAttribute(), element, name);
    }

    @Override
    @Generated
    public void afterGetAttribute(final WebElement element, final String name, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetAttribute(), element, name, result);
    }

    @Override
    @Generated
    public void beforeIsSelected(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeIsSelected(), element);
    }

    @Override
    @Generated
    public void afterIsSelected(final WebElement element, final boolean result) {
        listenTo(AUTO_AFTER, events.getAfterIsSelected(), element, result);
    }

    @Override
    @Generated
    public void beforeIsEnabled(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeIsEnabled(), element);
    }

    @Override
    @Generated
    public void afterIsEnabled(final WebElement element, final boolean result) {
        listenTo(AUTO_AFTER, events.getAfterIsEnabled(), element, result);
    }

    @Override
    @Generated
    public void beforeGetText(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeGetText(), element);
    }

    @Override
    @Generated
    public void afterGetText(final WebElement element, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetText(), element, result);
    }

    @Override
    @Generated
    public void beforeFindElement(final WebElement element, final By locator) {
        listenTo(AUTO_BEFORE, events.getBeforeFindWebElement(), element, locator);
    }

    @Override
    @Generated
    public void afterFindElement(final WebElement element, final By locator, final WebElement result) {
        listenTo(AUTO_AFTER, events.getAfterFindWebElement(), element, locator, result);
    }

    @Override
    @Generated
    public void beforeFindElements(final WebElement element, final By locator) {
        listenTo(AUTO_BEFORE, events.getBeforeFindWebElements(), element, locator);
    }

    @Override
    @Generated
    public void afterFindElements(final WebElement element, final By locator, final List<WebElement> result) {
        listenTo(AUTO_AFTER, events.getAfterFindWebElements(), element, locator, result);
    }

    @Override
    @Generated
    public void beforeIsDisplayed(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeIsDisplayed(), element);
    }

    @Override
    @Generated
    public void afterIsDisplayed(final WebElement element, final boolean result) {
        listenTo(AUTO_AFTER, events.getAfterIsDisplayed(), element, result);
    }

    @Override
    @Generated
    public void beforeGetLocation(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeGetLocation(), element);
    }

    @Override
    @Generated
    public void afterGetLocation(final WebElement element, final Point result) {
        listenTo(AUTO_AFTER, events.getAfterGetLocation(), element, result);
    }

    @Override
    @Generated
    public void beforeGetSize(final WebElement element) {
        listenTo(AUTO_BEFORE, events.getBeforeGetSize(), element);
    }

    @Override
    @Generated
    public void afterGetSize(final WebElement element, final Dimension result) {
        listenTo(AUTO_AFTER, events.getAfterGetSize(), element, result);
    }

    @Override
    @Generated
    public void beforeGetCssValue(final WebElement element, final String propertyName) {
        listenTo(AUTO_BEFORE, events.getBeforeGetCssValue(), element, propertyName);
    }

    @Override
    @Generated
    public void afterGetCssValue(final WebElement element, final String propertyName, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetCssValue(), element, propertyName, result);
    }

    @Override
    @Generated
    public void beforeAnyNavigationCall(final WebDriver.Navigation navigation, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyNavigationCall(), navigation, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyNavigationCall(final WebDriver.Navigation navigation, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyNavigationCall(), navigation, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeTo(final WebDriver.Navigation navigation, final String url) {
        listenTo(AUTO_BEFORE, events.getBeforeTo(), navigation, url);
    }

    @Override
    @Generated
    public void afterTo(final WebDriver.Navigation navigation, final String url) {
        listenTo(AUTO_AFTER, events.getAfterTo(), navigation, url);
    }

    @Override
    @Generated
    public void beforeTo(final WebDriver.Navigation navigation, final URL url) {
        listenTo(AUTO_BEFORE, events.getBeforeTo(), navigation, url);
    }

    @Override
    @Generated
    public void afterTo(final WebDriver.Navigation navigation, final URL url) {
        listenTo(AUTO_AFTER, events.getAfterTo(), navigation, url);
    }

    @Override
    @Generated
    public void beforeBack(final WebDriver.Navigation navigation) {
        listenTo(AUTO_BEFORE, events.getBeforeBack(), navigation);
    }

    @Override
    @Generated
    public void afterBack(final WebDriver.Navigation navigation) {
        listenTo(AUTO_AFTER, events.getAfterBack(), navigation);
    }

    @Override
    @Generated
    public void beforeForward(final WebDriver.Navigation navigation) {
        listenTo(AUTO_BEFORE, events.getBeforeForward(), navigation);
    }

    @Override
    @Generated
    public void afterForward(final WebDriver.Navigation navigation) {
        listenTo(AUTO_AFTER, events.getAfterForward(), navigation);
    }

    @Override
    @Generated
    public void beforeRefresh(final WebDriver.Navigation navigation) {
        listenTo(AUTO_BEFORE, events.getBeforeRefresh(), navigation);
    }

    @Override
    @Generated
    public void afterRefresh(final WebDriver.Navigation navigation) {
        listenTo(AUTO_AFTER, events.getAfterRefresh(), navigation);
    }

    @Override
    @Generated
    public void beforeAnyAlertCall(final Alert alert, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyAlertCall(), alert, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyAlertCall(final Alert alert, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyAlertCall(), alert, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeAccept(final Alert alert) {
        listenTo(AUTO_BEFORE, events.getBeforeAccept(), alert);
    }

    @Override
    @Generated
    public void afterAccept(final Alert alert) {
        listenTo(AUTO_AFTER, events.getAfterAccept(), alert);
    }

    @Override
    @Generated
    public void beforeDismiss(final Alert alert) {
        listenTo(AUTO_BEFORE, events.getBeforeDismiss(), alert);
    }

    @Override
    @Generated
    public void afterDismiss(final Alert alert) {
        listenTo(AUTO_AFTER, events.getAfterDismiss(), alert);
    }

    @Override
    @Generated
    public void beforeGetText(final Alert alert) {
        listenTo(AUTO_BEFORE, events.getBeforeGetText(), alert);
    }

    @Override
    @Generated
    public void afterGetText(final Alert alert, final String result) {
        listenTo(AUTO_AFTER, events.getAfterGetText(), alert, result);
    }

    @Override
    @Generated
    public void beforeSendKeys(final Alert alert, final String text) {
        listenTo(AUTO_BEFORE, events.getBeforeSendKeys(), alert, text);
    }

    @Override
    @Generated
    public void afterSendKeys(final Alert alert, final String text) {
        listenTo(AUTO_AFTER, events.getAfterSendKeys(), alert, text);
    }

    @Override
    @Generated
    public void beforeAnyOptionsCall(final WebDriver.Options options, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyOptionsCall(), options, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyOptionsCall(final WebDriver.Options options, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyOptionsCall(), options, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeAddCookie(final WebDriver.Options options, final Cookie cookie) {
        listenTo(AUTO_BEFORE, events.getBeforeAddCookie(), options, cookie);
    }

    @Override
    @Generated
    public void afterAddCookie(final WebDriver.Options options, final Cookie cookie) {
        listenTo(AUTO_AFTER, events.getAfterAddCookie(), options, cookie);
    }

    @Override
    @Generated
    public void beforeDeleteCookieNamed(final WebDriver.Options options, final String name) {
        listenTo(AUTO_BEFORE, events.getBeforeDeleteCookieNamed(), options, name);
    }

    @Override
    @Generated
    public void afterDeleteCookieNamed(final WebDriver.Options options, final String name) {
        listenTo(AUTO_AFTER, events.getAfterDeleteCookieNamed(), options, name);
    }

    @Override
    @Generated
    public void beforeDeleteCookie(final WebDriver.Options options, final Cookie cookie) {
        listenTo(AUTO_BEFORE, events.getBeforeDeleteCookie(), options, cookie);
    }

    @Override
    @Generated
    public void afterDeleteCookie(final WebDriver.Options options, final Cookie cookie) {
        listenTo(AUTO_AFTER, events.getAfterDeleteCookie(), options, cookie);
    }

    @Override
    @Generated
    public void beforeDeleteAllCookies(final WebDriver.Options options) {
        listenTo(AUTO_BEFORE, events.getBeforeDeleteAllCookies(), options);
    }

    @Override
    @Generated
    public void afterDeleteAllCookies(final WebDriver.Options options) {
        listenTo(AUTO_AFTER, events.getAfterDeleteAllCookies(), options);
    }

    @Override
    @Generated
    public void beforeGetCookies(final WebDriver.Options options) {
        listenTo(AUTO_BEFORE, events.getBeforeGetCookies(), options);
    }

    @Override
    @Generated
    public void afterGetCookies(final WebDriver.Options options, final Set<Cookie> result) {
        listenTo(AUTO_AFTER, events.getAfterGetCookies(), options, result);
    }

    @Override
    @Generated
    public void beforeGetCookieNamed(final WebDriver.Options options, final String name) {
        listenTo(AUTO_BEFORE, events.getBeforeGetCookieNamed(), options, name);
    }

    @Override
    @Generated
    public void afterGetCookieNamed(final WebDriver.Options options, final String name, final Cookie result) {
        listenTo(AUTO_AFTER, events.getAfterGetCookieNamed(), options, name, result);
    }

    @Override
    @Generated
    public void beforeAnyTimeoutsCall(final WebDriver.Timeouts timeouts, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyTimeoutsCall(), timeouts, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyTimeoutsCall(final WebDriver.Timeouts timeouts, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyTimeoutsCall(), timeouts, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeImplicitlyWait(final WebDriver.Timeouts timeouts, final Duration duration) {
        listenTo(AUTO_BEFORE, events.getBeforeImplicitlyWait(), timeouts, duration);
    }

    @Override
    @Generated
    public void afterImplicitlyWait(final WebDriver.Timeouts timeouts, final Duration duration) {
        listenTo(AUTO_AFTER, events.getAfterImplicitlyWait(), timeouts, duration);
    }

    @Override
    @Generated
    public void beforeScriptTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        listenTo(AUTO_BEFORE, events.getBeforeScriptTimeout(), timeouts, duration);
    }

    @Override
    @Generated
    public void afterScriptTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        listenTo(AUTO_AFTER, events.getAfterScriptTimeout(), timeouts, duration);
    }

    @Override
    @Generated
    public void beforePageLoadTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        listenTo(AUTO_BEFORE, events.getBeforePageLoadTimeout(), timeouts, duration);
    }

    @Override
    @Generated
    public void afterPageLoadTimeout(final WebDriver.Timeouts timeouts, final Duration duration) {
        listenTo(AUTO_AFTER, events.getAfterPageLoadTimeout(), timeouts, duration);
    }

    @Override
    @Generated
    public void beforeAnyWindowCall(final WebDriver.Window window, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyWindowCall(), window, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyWindowCall(final WebDriver.Window window, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyWindowCall(), window, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeGetSize(final WebDriver.Window window) {
        listenTo(AUTO_BEFORE, events.getBeforeGetWindowSize(), window);
    }

    @Override
    @Generated
    public void afterGetSize(final WebDriver.Window window, final Dimension result) {
        listenTo(AUTO_AFTER, events.getAfterGetWindowSize(), window, result);
    }

    @Override
    @Generated
    public void beforeSetSize(final WebDriver.Window window, final Dimension size) {
        listenTo(AUTO_BEFORE, events.getBeforeSetSize(), window, size);
    }

    @Override
    @Generated
    public void afterSetSize(final WebDriver.Window window, final Dimension size) {
        listenTo(AUTO_AFTER, events.getAfterSetSize(), window, size);
    }

    @Override
    @Generated
    public void beforeGetPosition(final WebDriver.Window window) {
        listenTo(AUTO_BEFORE, events.getBeforeGetPosition(), window);
    }

    @Override
    @Generated
    public void afterGetPosition(final WebDriver.Window window, final Point result) {
        listenTo(AUTO_AFTER, events.getAfterGetPosition(), window, result);
    }

    @Override
    @Generated
    public void beforeSetPosition(final WebDriver.Window window, final Point position) {
        listenTo(AUTO_BEFORE, events.getBeforeSetPosition(), window, position);
    }

    @Override
    @Generated
    public void afterSetPosition(final WebDriver.Window window, final Point position) {
        listenTo(AUTO_AFTER, events.getAfterSetPosition(), window, position);
    }

    @Override
    @Generated
    public void beforeMaximize(final WebDriver.Window window) {
        listenTo(AUTO_BEFORE, events.getBeforeMaximize(), window);
    }

    @Override
    @Generated
    public void afterMaximize(final WebDriver.Window window) {
        listenTo(AUTO_AFTER, events.getAfterMaximize(), window);
    }

    @Override
    @Generated
    public void beforeFullscreen(final WebDriver.Window window) {
        listenTo(AUTO_BEFORE, events.getBeforeFullscreen(), window);
    }

    @Override
    @Generated
    public void afterFullscreen(final WebDriver.Window window) {
        listenTo(AUTO_AFTER, events.getAfterFullscreen(), window);
    }

    @Override
    @Generated
    public void beforeAnyTargetLocatorCall(final WebDriver.TargetLocator targetLocator, final Method method, final Object[] args) {
        listenTo(AUTO_BEFORE, events.getBeforeAnyTargetLocatorCall(), targetLocator, method, Arrays.toString(args));
    }

    @Override
    @Generated
    public void afterAnyTargetLocatorCall(final WebDriver.TargetLocator targetLocator, final Method method, final Object[] args, final Object result) {
        listenTo(AUTO_AFTER, events.getAfterAnyTargetLocatorCall(), targetLocator, method, Arrays.toString(args), result);
    }

    @Override
    @Generated
    public void beforeFrame(final WebDriver.TargetLocator targetLocator, final int index) {
        listenTo(AUTO_BEFORE, events.getBeforeFrame(), targetLocator, index);
    }

    @Override
    @Generated
    public void afterFrame(final WebDriver.TargetLocator targetLocator, final int index, final WebDriver driver) {
        listenTo(AUTO_AFTER, events.getAfterFrame(), targetLocator, index, driver);
    }

    @Override
    @Generated
    public void beforeFrame(final WebDriver.TargetLocator targetLocator, final String nameOrId) {
        listenTo(AUTO_BEFORE, events.getBeforeFrame(), targetLocator, nameOrId);
    }

    @Override
    @Generated
    public void afterFrame(final WebDriver.TargetLocator targetLocator, final String nameOrId, final WebDriver driver) {
        listenTo(AUTO_AFTER, events.getAfterFrame(), targetLocator, nameOrId, driver);
    }

    @Override
    @Generated
    public void beforeFrame(final WebDriver.TargetLocator targetLocator, final WebElement frameElement) {
        listenTo(AUTO_BEFORE, events.getBeforeFrame(), targetLocator, frameElement);
    }

    @Override
    @Generated
    public void afterFrame(final WebDriver.TargetLocator targetLocator, final WebElement frameElement, final WebDriver driver) {
        listenTo(AUTO_AFTER, events.getAfterFrame(), targetLocator, frameElement, driver);
    }

    @Override
    @Generated
    public void beforeParentFrame(final WebDriver.TargetLocator targetLocator) {
        listenTo(AUTO_BEFORE, events.getBeforeParentFrame(), targetLocator);
    }

    @Override
    @Generated
    public void afterParentFrame(final WebDriver.TargetLocator targetLocator, final WebDriver driver) {
        listenTo(AUTO_AFTER, events.getAfterParentFrame(), targetLocator, driver);
    }

    @Override
    @Generated
    public void beforeWindow(final WebDriver.TargetLocator targetLocator, final String nameOrHandle) {
        listenTo(AUTO_BEFORE, events.getBeforeWindow(), targetLocator, nameOrHandle);
    }

    @Override
    @Generated
    public void afterWindow(final WebDriver.TargetLocator targetLocator, final String nameOrHandle, final WebDriver driver) {
        listenTo(AUTO_AFTER, events.getAfterWindow(), targetLocator, nameOrHandle, driver);
    }

    @Override
    @Generated
    public void beforeNewWindow(final WebDriver.TargetLocator targetLocator, final WindowType typeHint) {
        listenTo(AUTO_BEFORE, events.getBeforeNewWindow(), targetLocator, typeHint);
    }

    @Override
    @Generated
    public void afterNewWindow(final WebDriver.TargetLocator targetLocator, final WindowType typeHint, final WebDriver driver) {
        listenTo(AUTO_AFTER, events.getAfterNewWindow(), targetLocator, typeHint, driver);
    }

    @Override
    @Generated
    public void beforeDefaultContent(final WebDriver.TargetLocator targetLocator) {
        listenTo(AUTO_BEFORE, events.getBeforeDefaultContent(), targetLocator);
    }

    @Override
    @Generated
    public void afterDefaultContent(final WebDriver.TargetLocator targetLocator, final WebDriver driver) {
        listenTo(AUTO_AFTER, events.getAfterDefaultContent(), targetLocator, driver);
    }

    @Override
    @Generated
    public void beforeActiveElement(final WebDriver.TargetLocator targetLocator) {
        listenTo(AUTO_BEFORE, events.getBeforeActiveElement(), targetLocator);
    }

    @Override
    @Generated
    public void afterActiveElement(final WebDriver.TargetLocator targetLocator, final WebDriver driver) {
        listenTo(AUTO_AFTER, events.getAfterActiveElement(), targetLocator, driver);
    }

    @Override
    @Generated
    public void beforeAlert(final WebDriver.TargetLocator targetLocator) {
        listenTo(AUTO_BEFORE, events.getBeforeAlert(), targetLocator);
    }

    @Override
    @Generated
    public void afterAlert(final WebDriver.TargetLocator targetLocator, final Alert alert) {
        listenTo(AUTO_AFTER, events.getAfterAlert(), targetLocator, alert);
    }

    boolean isSecured(final CharSequence... keysToSend) {
        final Matcher matcher = SECURED_PATTERN.matcher(keysToSend[0]);

        if (matcher.find()) {
            keysToSend[0] = matcher.group("key");
            return true;
        }

        return false;
    }
}
