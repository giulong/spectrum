package io.github.giulong.spectrum.utils;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
    }

    @Override
    public void sendKeys(final CharSequence... keysToSend) {
    }

    @Override
    public void clear() {
    }

    @Override
    public String getTagName() {
        return "";
    }

    @Override
    public String getDomProperty(String name) {
        throw new UnsupportedOperationException("getDomProperty");
    }

    @Override
    public String getDomAttribute(String name) {
        throw new UnsupportedOperationException("getDomAttribute");
    }

    @Override
    public String getAttribute(final String name) {
        return "";
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
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public List<WebElement> findElements(final By by) {
        return List.of();
    }

    @Override
    public WebElement findElement(final By by) {
        return null;
    }

    @Override
    public SearchContext getShadowRoot() {
        throw new UnsupportedOperationException("getShadowRoot");
    }

    @Override
    public boolean isDisplayed() {
        return false;
    }

    @Override
    public Point getLocation() {
        return null;
    }

    @Override
    public Dimension getSize() {
        return null;
    }

    @Override
    public Rectangle getRect() {
        return null;
    }

    @Override
    public String getCssValue(final String propertyName) {
        return "";
    }

    @Override
    public <X> X getScreenshotAs(final OutputType<X> target) throws WebDriverException {
        return null;
    }

    @SneakyThrows
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        final String fullWebElement = webElement.toString();
        final Matcher matcher = locatorPattern.matcher(fullWebElement);
        log.debug("Intercepting method {} on webElement [{}]", method.getName(), matcher.find() ? matcher.group(1) : fullWebElement);

        return methods.get(method).invoke(this, args);
    }
}
