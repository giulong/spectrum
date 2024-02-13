package io.github.giulong.spectrum.utils.environments;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("GridEnvironment")
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

    @InjectMocks
    private GridEnvironment gridEnvironment;

    @BeforeEach
    public void beforeEach() {
        remoteWebDriverMockedStatic = mockStatic(RemoteWebDriver.class);
    }

    @AfterEach
    public void afterEach() {
        remoteWebDriverMockedStatic.close();
    }

    private void commonStubs() throws MalformedURLException {
        final URL url = URI.create("http://url").toURL();

        Reflections.setField("url", gridEnvironment, url);
        Reflections.setField("capabilities", gridEnvironment, capabilities);

        when(webDriverBuilder.build()).thenReturn(webDriver);
        when(driver.mergeGridCapabilitiesFrom(capabilities)).thenReturn(chromeOptions);
        when(RemoteWebDriver.builder()).thenReturn(webDriverBuilder);
        when(webDriverBuilder.address(url)).thenReturn(webDriverBuilder);
        when(webDriverBuilder.oneOf(chromeOptions)).thenReturn(webDriverBuilder);
    }

    @Test
    @DisplayName("setupFrom should configure a remote webDriver and return an instance of WebDriver")
    public void setupFrom() throws MalformedURLException {
        commonStubs();

        assertEquals(webDriver, gridEnvironment.setupFor(driver));

        verify(driver).mergeGridCapabilitiesFrom(capabilities);
    }

    @Test
    @DisplayName("setupFrom should configure a remote webDriver with localFileDetector and return an instance of WebDriver")
    public void setupFromLocalFileDetector() throws MalformedURLException {
        commonStubs();
        gridEnvironment.localFileDetector = true;

        assertEquals(webDriver, gridEnvironment.setupFor(driver));

        verify(driver).mergeGridCapabilitiesFrom(capabilities);
        verify(webDriver).setFileDetector(any(LocalFileDetector.class));
    }

    @Test
    @DisplayName("shutdown should do nothing for a remoteWebDriver")
    public void shutdown() {
        gridEnvironment.shutdown();
    }
}
