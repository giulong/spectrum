package io.github.giulong.spectrum.utils.environments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

@Slf4j
@Getter
public class GridEnvironment extends Environment {

    @JsonIgnore
    protected final Configuration configuration = Configuration.getInstance();

    @Override
    public WebDriver setupFor(final Driver<?, ?, ?> driver) {
        final Configuration.Environments.Grid grid = configuration.getEnvironments().getGrid();
        final URL url = grid.getUrl();

        log.info("Running on grid at {}", url);

        final RemoteWebDriver webDriver = (RemoteWebDriver) RemoteWebDriver
                .builder()
                .oneOf(driver.mergeGridCapabilitiesFrom(grid.getCapabilities()))
                .address(url)
                .build();

        return setFileDetectorFor(webDriver, grid);
    }

    @Override
    public void shutdown() {
        log.debug("Nothing to shutdown in a grid environment");
    }

    protected RemoteWebDriver setFileDetectorFor(final RemoteWebDriver webDriver, final Configuration.Environments.Grid grid) {
        if (grid.isLocalFileDetector()) {
            webDriver.setFileDetector(new LocalFileDetector());
        }

        return webDriver;
    }
}
