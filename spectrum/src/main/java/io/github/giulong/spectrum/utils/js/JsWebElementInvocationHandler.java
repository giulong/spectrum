package io.github.giulong.spectrum.utils.js;

import io.github.giulong.spectrum.enums.LocatorType;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void sendKeys(@NotNull final CharSequence... keysToSend) {
        js.sendKeys(webElement, keysToSend);
    }

    @Override
    public void clear() {
        js.clear(webElement);
    }

    @NotNull
    @Override
    public String getTagName() {
        return js.getTagName(webElement);
    }

    @Override
    public String getDomProperty(@NotNull final String name) {
        return js.getDomProperty(webElement, name);
    }

    @Override
    public String getDomAttribute(@NotNull final String name) {
        return js.getDomAttribute(webElement, name);
    }

    @Override
    public String getAttribute(@NotNull final String name) {
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

    @NotNull
    @Override
    public String getText() {
        return js.getText(webElement);
    }

    @NotNull
    @Override
    public List<WebElement> findElements(@NotNull final By by) {
        return js.findElements(webElement, LocatorType.from(by), extractLocatorValueFrom(by));
    }

    @NotNull
    @Override
    public WebElement findElement(@NotNull final By by) {
        return js.findElement(webElement, LocatorType.from(by), extractLocatorValueFrom(by));
    }

    @NotNull
    @Override
    public SearchContext getShadowRoot() {
        return js.getShadowRoot(webElement);
    }

    @Override
    public boolean isDisplayed() {
        return js.isDisplayed(webElement);
    }

    @NotNull
    @Override
    public Point getLocation() {
        return js.getLocation(webElement);
    }

    @NotNull
    @Override
    public Dimension getSize() {
        return js.getSize(webElement);
    }

    @NotNull
    @Override
    public Rectangle getRect() {
        return js.getRect(webElement);
    }

    @NotNull
    @Override
    public String getCssValue(@NotNull final String propertyName) {
        return js.getCssValue(webElement, propertyName);
    }

    @NotNull
    @Override
    public <X> X getScreenshotAs(@NotNull final OutputType<X> target) throws WebDriverException {
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
