package io.github.giulong.spectrum.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.time.Duration;
import java.util.Map;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.WebDriver;

class WindowsTest {

    @Mock
    private WebDriver windowsWebDriver;

    @Mock
    private WindowsOptions windowsOptions;

    @Mock
    private WebDriver.Options options;

    @Mock
    private WebDriver.Timeouts timeouts;

    @Mock
    private Configuration.Drivers.Waits waits;

    @Mock
    private Duration duration;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Configuration.Drivers.Windows windowsConfiguration;

    @Mock
    private URL url;

    @InjectMocks
    private Windows windows;

    @Test
    @DisplayName("buildCapabilities should build a new instance of WindowsOptions")
    void buildCapabilities() {
        try (MockedConstruction<WindowsOptions> mockedConstruction = mockConstruction()) {
            assertEquals(windows, windows.buildCapabilities());

            final WindowsOptions actual = Reflections.getFieldValue("capabilities", windows);
            assertEquals(mockedConstruction.constructed().getFirst(), actual);
        }
    }

    @Test
    @DisplayName("buildDriverFor should return a new instance of WindowsDriver for the provided url and the instance capabilities")
    void buildDriverFor() {
        Reflections.setField("capabilities", windows, windowsOptions);

        try (MockedConstruction<WindowsDriver> windowsDriverMockedConstruction = mockConstruction((mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(windowsOptions, context.arguments().get(1));
        })) {
            assertEquals(windows.buildDriverFor(url), windowsDriverMockedConstruction.constructed().getFirst());
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should merge a new instance of windowsOptions and set the capabilities from the yaml on it")
    void mergeCapabilitiesWith() {
        Reflections.setField("capabilities", windows, windowsOptions);

        try (MockedConstruction<WindowsOptions> desiredCapabilitiesMockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(capabilities, context.arguments().getFirst()))) {

            when(drivers.getWindows()).thenReturn(windowsConfiguration);
            when(windowsConfiguration.getCapabilities()).thenReturn(capabilities);

            assertEquals(windows, windows.mergeCapabilitiesWith(drivers));

            verify(windowsOptions).merge(desiredCapabilitiesMockedConstruction.constructed().getFirst());
        }
    }

    @Test
    @DisplayName("configureWaitsOf should configure just the implicitWait, since the others are not implemented")
    void configureWaitsOf() {
        when(waits.getImplicit()).thenReturn(duration);

        when(windowsWebDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);

        windows.configureWaitsOf(windowsWebDriver, waits);

        verify(timeouts).implicitlyWait(duration);
    }
}
