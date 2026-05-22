package io.github.giulong.spectrum.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.BROWSER;
import static org.openqa.selenium.logging.LogType.DRIVER;
import static org.openqa.selenium.logging.LogType.PERFORMANCE;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

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

class ChromeTest {

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Configuration.Drivers.Chrome chromeConfig;

    @Mock
    private Level browserLevel;

    @Mock
    private Level driverLevel;

    @Mock
    private Level performanceLevel;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Drivers.Logs logs;

    @Mock
    private Configuration.Drivers.Chrome.Service service;

    @Mock
    private ChromeOptions capabilities;

    @InjectMocks
    private Chrome chrome;

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of ChromeDriverService.Builder()")
    void getDriverServiceBuilder() {
        final String allowedListIps = "allowedListIps";

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getChrome()).thenReturn(chromeConfig);
        when(chromeConfig.getService()).thenReturn(service);
        when(service.isBuildCheckDisabled()).thenReturn(true);
        when(service.isAppendLog()).thenReturn(true);
        when(service.isReadableTimestamp()).thenReturn(true);
        when(service.getLogLevel()).thenReturn(ChromiumDriverLogLevel.ALL);
        when(service.isSilent()).thenReturn(true);
        when(service.isVerbose()).thenReturn(true);
        when(service.getAllowedListIps()).thenReturn(allowedListIps);

        MockedConstruction<ChromeDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction((mock, context) -> {
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
    @DisplayName("buildCapabilities should build an instance of ChromeOptions")
    void buildCapabilities() {
        try (MockedConstruction<ChromeOptions> edgeOptionsMockedConstruction = mockConstruction()) {
            assertEquals(chrome, chrome.buildCapabilities());

            final ChromeOptions chromeOptions = edgeOptionsMockedConstruction.constructed().getFirst();

            assertEquals(chromeOptions, Reflections.getFieldValue("capabilities", chrome));
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should merge the provided configuration")
    void mergeCapabilitiesWith() {
        final List<String> arguments = List.of("args");

        Reflections.setField("capabilities", chrome, capabilities);

        when(drivers.getChrome()).thenReturn(chromeConfig);
        when(drivers.getLogs()).thenReturn(logs);
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);
        when(chromeConfig.getArgs()).thenReturn(arguments);
        when(chromeConfig.getCapabilities()).thenReturn(Map.of("capability", "value1"));
        when(chromeConfig.getExperimentalOptions()).thenReturn(Map.of("experimental", "value2"));

        // activateBiDi
        when(drivers.isBiDi()).thenReturn(false);
        when(chromeConfig.isBiDi()).thenReturn(true);

        try (MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction = mockConstruction()) {

            assertEquals(chrome, chrome.mergeCapabilitiesWith(drivers));

            final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().getFirst();

            verify(loggingPreferences).enable(BROWSER, browserLevel);
            verify(loggingPreferences).enable(DRIVER, driverLevel);
            verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
            verify(capabilities).setCapability(LOGGING_PREFS, loggingPreferences);

            verify(capabilities).setCapability("capability", (Object) "value1");
            verify(capabilities).setExperimentalOption("experimental", "value2");
            verify(capabilities).setCapability("webSocketUrl", true);

            assertEquals(capabilities, Reflections.getFieldValue("capabilities", chrome));
        }
    }
}
