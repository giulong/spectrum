package io.github.giulong.spectrum.extensions.resolvers.bidi;

import io.github.giulong.spectrum.utils.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.openqa.selenium.WebDriver;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public abstract class BiDiTypeBasedParameterResolver<T> implements ParameterResolver {

    private final Configuration configuration = Configuration.getInstance();

    abstract String getKey();

    abstract Class<T> getType();

    abstract T resolveParameterFor(WebDriver driver);

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, @NonNull final ExtensionContext context) {
        return parameterContext.getParameter().getType().equals(getType());
    }

    @Override
    public final T resolveParameter(@NonNull final ParameterContext parameterContext, final ExtensionContext context) {
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final WebDriver driver = store.get(DRIVER, WebDriver.class);
        final String key = getKey();

        if (!configuration.getDrivers().isBiDi()) {
            log.debug("BiDi disabled. Avoid resolving {}", key);
            return null;
        }

        log.debug("Resolving {}", key);
        final T t = resolveParameterFor(driver);

        store.put(key, t);
        return t;
    }
}
