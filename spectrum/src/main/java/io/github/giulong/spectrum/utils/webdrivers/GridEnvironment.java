package io.github.giulong.spectrum.utils.webdrivers;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.browsers.Browser;
import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.utils.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class GridEnvironment extends Environment {

    @JsonSchemaTypes(String.class)
    @JsonPropertyDescription("Url of the selenium grid")
    protected URL url;

    @JsonPropertyDescription("Capabilities dedicated to executions on the grid")
    protected final Map<String, String> capabilities = new HashMap<>();

    @JsonPropertyDescription("Whether to search for files to upload on the client machine or not")
    protected boolean localFileDetector;

    @Override
    public WebDriver setupFrom(final Configuration configuration, final Browser<?, ?, ?> browser) {
        log.info("Running on grid at {}", url);

        final RemoteWebDriver webDriver = (RemoteWebDriver) RemoteWebDriver
                .builder()
                .oneOf(browser.mergeGridCapabilitiesFrom(capabilities))
                .address(url)
                .build();

        if (localFileDetector) {
            webDriver.setFileDetector(new LocalFileDetector());
        }

        return webDriver;
    }

    @Override
    public void shutdown() {
        log.debug("Nothing to shutdown in a grid environment");
    }
}
