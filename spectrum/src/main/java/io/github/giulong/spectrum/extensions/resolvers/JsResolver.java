package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Js;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class JsResolver extends TypeBasedParameterResolver<Js> {

    public static final String JS = "js";

    @Override
    public Js resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        return context.getRoot().getStore(GLOBAL).getOrComputeIfAbsent(JS, e -> {
            log.debug("Resolving {}", JS);

            return Js.builder()
                    .driver((JavascriptExecutor) context.getStore(GLOBAL).get(DRIVER, WebDriver.class))
                    .build();
        }, Js.class);
    }
}
