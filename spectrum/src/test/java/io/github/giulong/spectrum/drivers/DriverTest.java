package io.github.giulong.spectrum.drivers;

import static io.github.giulong.spectrum.drivers.Driver.WEB_DRIVER_THREAD_LOCAL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.environments.Environment;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ThreadGuard;

class DriverTest {

    private MockedStatic<ThreadGuard> threadGuardMockedStatic;

    private MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Drivers driversConfig;

    @Mock
    private Configuration.Drivers.Chrome chromeConfig;

    @Mock
    private Level browserLevel;

    @Mock
    private Level driverLevel;

    @Mock
    private Level performanceLevel;

    @Mock
    private Configuration.Drivers.Logs logs;

    @Mock
    private WebDriver webDriver;

    @Mock
    private WebDriver protectedWebDriver;

    @Mock
    private Configuration.Drivers.Waits waits;

    @Mock
    private WebDriver.Options options;

    @Mock
    private WebDriver.Timeouts timeouts;

    @Mock
    private Duration implicitDuration;

    @Mock
    private Duration pageLoadDuration;

    @Mock
    private Duration scriptDuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Environment environment;

    @Mock
    private ChromeOptions driverOptions;

    @Mock
    private Map<String, Object> gridCapabilities;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

    @InjectMocks
    private Chrome driver;

    @BeforeEach
    void beforeEach() {
        WEB_DRIVER_THREAD_LOCAL.remove();

        threadGuardMockedStatic = mockStatic();
        loggingPreferencesMockedConstruction = mockConstruction();
    }

    @AfterEach
    void afterEach() {
        threadGuardMockedStatic.close();
        loggingPreferencesMockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities and return the capabilities")
    void mergeGridCapabilitiesFrom() {
        Reflections.setField("capabilities", driver, driverOptions);
        when(driverOptions.merge(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(driverOptions);

        MockedConstruction<DesiredCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction((mock, context) -> {
            assertEquals(gridCapabilities, context.arguments().getFirst());
        });

        final ChromeOptions actual = driver.mergeGridCapabilitiesFrom(gridCapabilities);
        verify(driverOptions).merge(desiredCapabilitiesMockedConstruction.constructed().getFirst());
        assertEquals(actual, driverOptions);

        desiredCapabilitiesMockedConstruction.close();
        Reflections.setField("capabilities", driver, null);
    }

    @Test
    @DisplayName("build should return the instance of the requested driver")
    void build() {
        // buildCapabilitiesFrom stubs
        final List<String> arguments = List.of("args");
        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getArgs()).thenReturn(arguments);
        when(driversConfig.getLogs()).thenReturn(logs);
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getCapabilities()).thenReturn(Map.of());
        when(chromeConfig.getExperimentalOptions()).thenReturn(Map.of());

        MockedConstruction<ChromeOptions> chromeOptionsMockedConstruction = mockConstruction(
                (mock, context) -> when(mock.addArguments(arguments)).thenReturn(mock));

        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getEnvironment()).thenReturn(environment);
        when(webDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);
        when(driversConfig.getWaits()).thenReturn(waits);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);
        when(timeouts.scriptTimeout(scriptDuration)).thenReturn(timeouts);
        when(environment.setupFor(driver)).thenReturn(webDriver);

        when(ThreadGuard.protect(webDriver)).thenReturn(protectedWebDriver);

        final WebDriver actual = driver.build();
        final WebDriver threadLocalWebDriver = WEB_DRIVER_THREAD_LOCAL.get();

        assertEquals(protectedWebDriver, threadLocalWebDriver);
        assertEquals(protectedWebDriver, actual);

        chromeOptionsMockedConstruction.close();
    }

    @Test
    @DisplayName("configureWaitsOf should configure all the waits by default")
    void configureWaitsOf() {
        when(waits.getImplicit()).thenReturn(implicitDuration);
        when(waits.getPageLoadTimeout()).thenReturn(pageLoadDuration);
        when(waits.getScriptTimeout()).thenReturn(scriptDuration);

        when(webDriver.manage()).thenReturn(options);
        when(options.timeouts()).thenReturn(timeouts);
        when(timeouts.implicitlyWait(implicitDuration)).thenReturn(timeouts);
        when(timeouts.pageLoadTimeout(pageLoadDuration)).thenReturn(timeouts);

        driver.configureWaitsOf(webDriver, waits);

        verify(timeouts).scriptTimeout(scriptDuration);
    }

    @Test
    @DisplayName("shutdown should quit the webDriver")
    void shutdown() {
        WEB_DRIVER_THREAD_LOCAL.set(webDriver);

        driver.shutdown();

        verify(webDriver).quit();
    }
}
