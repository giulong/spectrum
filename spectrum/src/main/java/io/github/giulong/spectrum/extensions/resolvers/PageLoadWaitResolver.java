package io.github.giulong.spectrum.extensions.resolvers;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import io.github.giulong.spectrum.types.PageLoadWait;
import io.github.giulong.spectrum.utils.Configuration;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

@Slf4j
public class PageLoadWaitResolver extends TypeBasedParameterResolver<PageLoadWait> {

    public static final String PAGE_LOAD_WAIT = "pageLoadWait";

    @Override
    public PageLoadWait resolveParameter(final ParameterContext parameterContext, final ExtensionContext context) {
        log.debug("Resolving {}", PAGE_LOAD_WAIT);

        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final WebDriver driver = store.get(DRIVER, WebDriver.class);
        final Configuration.Drivers.Waits waits = rootStore.get(ConfigurationResolver.CONFIGURATION, Configuration.class).getDrivers().getWaits();
        final PageLoadWait webDriverWait = new PageLoadWait(driver, waits.getPageLoadTimeout());

        store.put(PAGE_LOAD_WAIT, webDriverWait);
        return webDriverWait;
    }
}
