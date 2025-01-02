package io.github.giulong.spectrum.utils.js;

import lombok.Builder;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.regex.Pattern;

@Builder
public class JsWebElementProxyBuilder {

    private Js js;
    private Pattern locatorPattern;
    private Map<Method, Method> methods;

    public WebElement buildFor(final Object webElement) {
        return (WebElement) Proxy.newProxyInstance(
                WebElement.class.getClassLoader(),
                new Class<?>[]{WebElement.class},
                JsWebElementInvocationHandler
                        .builder()
                        .js(js)
                        .webElement((WebElement) webElement)
                        .locatorPattern(locatorPattern)
                        .methods(methods)
                        .build());
    }
}
