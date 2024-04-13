package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Js;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class JsResolver extends TypeBasedParameterResolver<Js> {

    public static final String JS = "js";

    @Override
    public Js resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", JS);

        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final Js js = Js.builder()
                .driver(store.get(DRIVER, WebDriver.class))
                .build();

        store.put(JS, js);
        return js;
    }
}
