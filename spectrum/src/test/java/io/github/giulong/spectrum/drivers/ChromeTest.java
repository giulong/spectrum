package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriverLogLevel;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.service.DriverService;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

class ChromeTest {

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
    private Configuration configuration;

    @Mock
    private Configuration.Drivers.Logs logs;

    @Mock
    private Configuration.Drivers.Chrome.Service service;

    @InjectMocks
    private Chrome chrome;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", chrome, configuration);
    }

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of ChromeDriverService.Builder()")
    void getDriverServiceBuilder() {
        final String allowedListIps = "allowedListIps";

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getService()).thenReturn(service);
        when(service.isBuildCheckDisabled()).thenReturn(true);
        when(service.isAppendLog()).thenReturn(true);
        when(service.isReadableTimestamp()).thenReturn(true);
        when(service.getLogLevel()).thenReturn(ChromiumDriverLogLevel.ALL);
        when(service.isSilent()).thenReturn(true);
        when(service.isVerbose()).thenReturn(true);
        when(service.getAllowedListIps()).thenReturn(allowedListIps);

        MockedConstruction<ChromeDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction(ChromeDriverService.Builder.class, (mock, context) -> {
            when(mock.withBuildCheckDisabled(true)).thenReturn(mock);
            when(mock.withAppendLog(true)).thenReturn(mock);
            when(mock.withReadableTimestamp(true)).thenReturn(mock);
            when(mock.withLogLevel(ChromiumDriverLogLevel.ALL)).thenReturn(mock);
            when(mock.withSilent(true)).thenReturn(mock);
            when(mock.withVerbose(true)).thenReturn(mock);
            when(mock.withAllowedListIps(allowedListIps)).thenReturn(mock);
        });

        final DriverService.Builder<ChromeDriverService, ChromeDriverService.Builder> driverServiceBuilder = chrome.getDriverServiceBuilder();
        assertEquals(chromeDriverServiceMockedConstruction.constructed().getFirst(), driverServiceBuilder);

        chromeDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build an instance of Chrome based on the provided configuration")
    void buildCapabilities() {
        final List<String> arguments = List.of("args");

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getChrome()).thenReturn(chromeConfig);
        when(driversConfig.getLogs()).thenReturn(logs);
        when(chromeConfig.getArgs()).thenReturn(arguments);
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getCapabilities()).thenReturn(Map.of("capability", "value1"));
        when(chromeConfig.getExperimentalOptions()).thenReturn(Map.of("experimental", "value2"));

        // activateBiDi
        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.isBiDi()).thenReturn(false);
        lenient().when(chromeConfig.isBiDi()).thenReturn(true);

        MockedConstruction<ChromeOptions> chromeOptionsMockedConstruction = mockConstruction(ChromeOptions.class,
                (mock, context) -> when(mock.addArguments(arguments)).thenReturn(mock));
        MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction = mockConstruction(LoggingPreferences.class);

        chrome.buildCapabilities();
        final ChromeOptions chromeOptions = chromeOptionsMockedConstruction.constructed().getFirst();
        verify(chromeOptions).addArguments(arguments);

        final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().getFirst();
        verify(loggingPreferences).enable(BROWSER, browserLevel);
        verify(loggingPreferences).enable(DRIVER, driverLevel);
        verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
        verify(chromeOptions).setCapability(LOGGING_PREFS, loggingPreferences);

        verify(chromeOptions).setCapability("capability", (Object) "value1");
        verify(chromeOptions).setExperimentalOption("experimental", "value2");
        verify(chromeOptions).setCapability("webSocketUrl", true);

        assertEquals(chromeOptions, Reflections.getFieldValue("capabilities", chrome));

        chromeOptionsMockedConstruction.close();
        loggingPreferencesMockedConstruction.close();
    }
}
