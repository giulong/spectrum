package io.github.giulong.spectrum.extensions.resolvers;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import io.github.giulong.spectrum.utils.js.Js;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

@Slf4j
public class JsResolver extends TypeBasedParameterResolver<Js> {

    public static final String JS = "js";

    @Override
    public Js resolveParameter(@NonNull final ParameterContext parameterContext, final ExtensionContext context) {
        log.debug("Resolving {}", JS);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Js js = Js.builder()
                .driver((JavascriptExecutor) store.get(DRIVER, WebDriver.class))
                .build();

        store.put(JS, js);
        return js;
    }
}
