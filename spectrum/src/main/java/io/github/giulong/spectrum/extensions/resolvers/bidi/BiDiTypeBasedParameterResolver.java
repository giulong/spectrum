package io.github.giulong.spectrum.extensions.resolvers.bidi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.HasBiDi;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public abstract class BiDiTypeBasedParameterResolver<T> implements ParameterResolver {

    abstract String getKey();

    abstract Class<T> getType();

    abstract T resolveParameterFor(WebDriver driver);

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext context) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(getType());
    }

    @Override
    public final T resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final WebDriver driver = store.get(DRIVER, WebDriver.class);
        final String key = getKey();

        if (!isBiDiEnabledFor(driver)) {
            log.debug("BiDi disabled. Avoid resolving {}", key);
            return null;
        }

        log.debug("Resolving {}", key);
        final T t = resolveParameterFor(driver);

        store.put(key, t);
        return t;
    }

    boolean isBiDiEnabledFor(final WebDriver driver) {
        return driver instanceof HasBiDi && ((HasBiDi) driver).maybeGetBiDi().isPresent();
    }
}
