package io.github.giulong.spectrum.utils;

import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
@Builder
public class JsWebElementListInvocationHandler implements InvocationHandler {

    private JsWebElementProxyBuilder jsWebElementProxyBuilder;
    private List<WebElement> webElements;

    @SneakyThrows
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        final Object result = method.invoke(webElements, args);

        if (result instanceof WebElement) {
            log.debug("Creating on-demand proxy for webElement");
            return jsWebElementProxyBuilder.buildFor(result);
        }

        return result;
    }
}
