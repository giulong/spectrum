package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.js.Js;
import io.github.giulong.spectrum.utils.js.JsWebElementInvocationHandler;
import io.github.giulong.spectrum.utils.js.JsWebElementProxyBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.JsResolver.JS;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class JsWebElementProxyBuilderResolver extends TypeBasedParameterResolver<JsWebElementProxyBuilder> {

    public static final String JS_WEB_ELEMENT_PROXY_BUILDER = "jsWebElementProxyBuilder";

    @Override
    public JsWebElementProxyBuilder resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final ExtensionContext.Store store = context.getStore(GLOBAL);

        log.debug("Resolving {}", JS_WEB_ELEMENT_PROXY_BUILDER);

        final Configuration configuration = rootStore.get(CONFIGURATION, Configuration.class);
        final List<Method> remainingJsMethods = new ArrayList<>(Arrays.asList(JsWebElementInvocationHandler.class.getDeclaredMethods()));
        final List<Method> remainingWebElementMethods = new ArrayList<>(Arrays.asList(WebElement.class.getDeclaredMethods()));
        final Map<Method, Method> methods = new HashMap<>(remainingWebElementMethods.size());

        do {
            final Method webElementMethod = remainingWebElementMethods.getFirst();
            final Method jsMethod = remainingJsMethods
                    .stream()
                    .filter(method -> methodsEqual(webElementMethod, method))
                    .findFirst()
                    .orElseThrow();

            methods.put(webElementMethod, jsMethod);
            remainingJsMethods.remove(jsMethod);
            remainingWebElementMethods.remove(webElementMethod);
        } while (!remainingWebElementMethods.isEmpty());

        final JsWebElementProxyBuilder jsWebElementProxyBuilder = JsWebElementProxyBuilder
                .builder()
                .js(store.get(JS, Js.class))
                .locatorPattern(Pattern.compile(configuration.getExtent().getLocatorRegex()))
                .methods(methods)
                .build();

        store.put(JS_WEB_ELEMENT_PROXY_BUILDER, jsWebElementProxyBuilder);
        return jsWebElementProxyBuilder;
    }

    boolean methodsEqual(final Method m1, final Method m2) {
        final boolean result = m1.getName().equals(m2.getName()) &&
                m1.getReturnType().equals(m2.getReturnType()) &&
                Arrays.equals(m1.getParameterTypes(), m2.getParameterTypes());

        log.trace("Checking if {} binds to {}: {}", m1.getName(), m2.getName(), result);
        return result;
    }
}
