package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.ImplicitWait;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ImplicitWaitResolver extends TypeBasedParameterResolver<ImplicitWait> {

    public static final String IMPLICIT_WAIT = "implicitWait";

    @Override
    public ImplicitWait resolveParameter(final ParameterContext parameterContext, final ExtensionContext context) {
        log.debug("Resolving {}", IMPLICIT_WAIT);

        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final WebDriver driver = store.get(DRIVER, WebDriver.class);
        final Configuration.Drivers.Waits waits = rootStore.get(ConfigurationResolver.CONFIGURATION, Configuration.class).getDrivers().getWaits();
        final ImplicitWait webDriverWait = new ImplicitWait(driver, waits.getImplicit());

        store.put(IMPLICIT_WAIT, webDriverWait);
        return webDriverWait;
    }
}
