package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.DownloadWait;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class DownloadWaitResolver extends TypeBasedParameterResolver<DownloadWait> {

    public static final String DOWNLOAD_WAIT = "downloadWait";

    @Override
    public DownloadWait resolveParameter(final ParameterContext parameterContext, final ExtensionContext context) {
        log.debug("Resolving {}", DOWNLOAD_WAIT);

        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final WebDriver driver = store.get(DRIVER, WebDriver.class);
        final Configuration.Drivers.Waits waits = rootStore.get(CONFIGURATION, Configuration.class).getDrivers().getWaits();
        final DownloadWait webDriverWait = new DownloadWait(driver, waits.getDownloadTimeout());

        store.put(DOWNLOAD_WAIT, webDriverWait);
        return webDriverWait;
    }
}
