package io.github.giulong.spectrum.utils.environments;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GridEnvironmentTest {

    private MockedStatic<RemoteWebDriver> remoteWebDriverMockedStatic;

    @Mock
    private RemoteWebDriverBuilder webDriverBuilder;

    @Mock
    private Driver<ChromeOptions, ?, ?> driver;

    @Mock
    private RemoteWebDriver webDriver;

    @Mock
    private ChromeOptions chromeOptions;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Environments environments;

    @Mock
    private Configuration.Environments.Grid grid;

    @InjectMocks
    private GridEnvironment gridEnvironment;

    @BeforeEach
    void beforeEach() {
        remoteWebDriverMockedStatic = mockStatic(RemoteWebDriver.class);

        Reflections.setField("configuration", gridEnvironment, configuration);
    }

    @AfterEach
    void afterEach() {
        remoteWebDriverMockedStatic.close();
    }

    private void commonStubs() throws MalformedURLException {
        final URL url = URI.create("http://url").toURL();

        when(configuration.getEnvironments()).thenReturn(environments);
        when(environments.getGrid()).thenReturn(grid);
        when(grid.getUrl()).thenReturn(url);
        when(grid.getCapabilities()).thenReturn(capabilities);

        when(webDriverBuilder.build()).thenReturn(webDriver);
        when(driver.mergeGridCapabilitiesFrom(capabilities)).thenReturn(chromeOptions);
        when(RemoteWebDriver.builder()).thenReturn(webDriverBuilder);
        when(webDriverBuilder.address(url)).thenReturn(webDriverBuilder);
        when(webDriverBuilder.oneOf(chromeOptions)).thenReturn(webDriverBuilder);
    }

    @Test
    @DisplayName("setupFrom should configure a remote webDriver with localFileDetector and return an instance of WebDriver")
    void setupFrom() throws MalformedURLException {
        commonStubs();
        when(grid.isLocalFileDetector()).thenReturn(true);

        assertEquals(webDriver, gridEnvironment.setupFor(driver));

        verify(driver).mergeGridCapabilitiesFrom(capabilities);
        verify(webDriver).setFileDetector(any(LocalFileDetector.class));
    }

    @Test
    @DisplayName("setFileDetectorFor should set the localFileDetector on the provided webDriver")
    void setFileDetectorForLocal() {
        when(grid.isLocalFileDetector()).thenReturn(true);

        assertEquals(webDriver, gridEnvironment.setFileDetectorFor(webDriver, grid));

        verify(webDriver).setFileDetector(any(LocalFileDetector.class));
    }

    @Test
    @DisplayName("setFileDetectorFor should do nothing if set the localFileDetector is false")
    void setFileDetectorFor() {
        assertEquals(webDriver, gridEnvironment.setFileDetectorFor(webDriver, grid));

        verifyNoInteractions(webDriver);
    }

    @Test
    @DisplayName("shutdown should do nothing for a remoteWebDriver")
    void shutdown() {
        gridEnvironment.shutdown();
    }
}
