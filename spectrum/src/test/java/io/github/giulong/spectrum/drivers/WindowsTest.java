package io.github.giulong.spectrum.drivers;

import io.appium.java_client.windows.WindowsDriver;
import io.appium.java_client.windows.options.WindowsOptions;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

import java.net.URL;
import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Windows")
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

    @Mock
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

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("configuration", windows, configuration);
        Reflections.setField("capabilities", windows, windowsOptions);
    }

    @Test
    @DisplayName("configureWaitsOf should configure just the implicitWait, since the others are not implemented")
    public void configureWaitsOf() {
        when(waits.getImplicit()).thenReturn(duration);

        when(windowsWebDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);

        windows.configureWaitsOf(windowsWebDriver, waits);

        verify(timeouts).implicitlyWait(duration);
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of windowsOptions and set the capabilities from the yaml on it")
    public void buildCapabilitiesAbsoluteAppPath() {
        MockedConstruction<WindowsOptions> desiredCapabilitiesMockedConstruction = mockConstruction(WindowsOptions.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getWindows()).thenReturn(windowsConfiguration);
        when(windowsConfiguration.getCapabilities()).thenReturn(capabilities);

        windows.buildCapabilities();

        final WindowsOptions actual = (WindowsOptions) Reflections.getFieldValue("capabilities", windows);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildDriverFor should return a new instance of WindowsDriver for the provided url and the instance capabilities")
    public void buildDriverFor() {
        MockedConstruction<WindowsDriver> windowsDriverMockedConstruction = mockConstruction(WindowsDriver.class, (mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(windowsOptions, context.arguments().get(1));
        });

        assertEquals(windows.buildDriverFor(url), windowsDriverMockedConstruction.constructed().getFirst());

        windowsDriverMockedConstruction.close();
    }
}
