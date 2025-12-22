package io.github.giulong.spectrum.utils.js;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.giulong.spectrum.enums.LocatorType;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.openqa.selenium.*;

@Slf4j
@Builder
public class JsWebElementInvocationHandler implements WebElement, InvocationHandler {

    private Js js;
    private WebElement webElement;
    private Pattern locatorPattern;
    private Map<Method, Method> methods;

    @Override
    public void click() {
        js.click(webElement);
    }

    @Override
    public void submit() {
        js.submit(webElement);
    }

    @Override
    public void sendKeys(@NonNull final CharSequence... keysToSend) {
        js.sendKeys(webElement, keysToSend);
    }

    @Override
    public void clear() {
        js.clear(webElement);
    }

    @NonNull
    @Override
    public String getTagName() {
        return js.getTagName(webElement);
    }

    @Override
    public String getDomProperty(@NonNull final String name) {
        return js.getDomProperty(webElement, name);
    }

    @Override
    public String getDomAttribute(@NonNull final String name) {
        return js.getDomAttribute(webElement, name);
    }

    @Override
    public String getAttribute(@NonNull final String name) {
        return js.getAttribute(webElement, name);
    }

    @Override
    public String getAriaRole() {
        throw new UnsupportedOperationException("getAriaRole");
    }

    @Override
    public String getAccessibleName() {
        throw new UnsupportedOperationException("getAccessibleName");
    }

    @Override
    public boolean isSelected() {
        return js.isSelected(webElement);
    }

    @Override
    public boolean isEnabled() {
        return js.isEnabled(webElement);
    }

    @NonNull
    @Override
    public String getText() {
        return js.getText(webElement);
    }

    @NonNull
    @Override
    public List<WebElement> findElements(@NonNull final By by) {
        return js.findElements(webElement, LocatorType.from(by), extractLocatorValueFrom(by));
    }

    @NonNull
    @Override
    public WebElement findElement(@NonNull final By by) {
        return js.findElement(webElement, LocatorType.from(by), extractLocatorValueFrom(by));
    }

    @NonNull
    @Override
    public SearchContext getShadowRoot() {
        return js.getShadowRoot(webElement);
    }

    @Override
    public boolean isDisplayed() {
        return js.isDisplayed(webElement);
    }

    @NonNull
    @Override
    public Point getLocation() {
        return js.getLocation(webElement);
    }

    @NonNull
    @Override
    public Dimension getSize() {
        return js.getSize(webElement);
    }

    @NonNull
    @Override
    public Rectangle getRect() {
        return js.getRect(webElement);
    }

    @NonNull
    @Override
    public String getCssValue(@NonNull final String propertyName) {
        return js.getCssValue(webElement, propertyName);
    }

    @NonNull
    @Override
    public <X> X getScreenshotAs(@NonNull final OutputType<X> target) throws WebDriverException {
        throw new UnsupportedOperationException("getScreenshotAs");
    }

    @SneakyThrows
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        final String fullWebElement = webElement.toString();
        final Matcher matcher = locatorPattern.matcher(fullWebElement);
        log.debug("Intercepting method {} on webElement [{}]", method.getName(), matcher.find() ? matcher.group(1) : fullWebElement);

        return methods.getOrDefault(method, method).invoke(this, args);
    }

    String extractLocatorValueFrom(final By by) {
        return by.toString().split(": ")[1];
    }
}
