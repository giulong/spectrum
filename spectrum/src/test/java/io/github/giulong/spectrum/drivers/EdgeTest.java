package io.github.giulong.spectrum.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.chrome.ChromeOptions.LOGGING_PREFS;
import static org.openqa.selenium.logging.LogType.*;

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
import org.openqa.selenium.chromium.ChromiumDriverLogLevel;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.service.DriverService;

class EdgeTest {

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Configuration.Drivers.Edge edgeConfig;

    @Mock
    private Level browserLevel;

    @Mock
    private Level driverLevel;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Level performanceLevel;

    @Mock
    private Configuration.Drivers.Logs logs;

    @Mock
    private Configuration.Drivers.Chrome.Service service;

    @Mock
    private EdgeOptions capabilities;

    @InjectMocks
    private Edge edge;

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of EdgeDriverService.Builder()")
    void getDriverServiceBuilder() {
        final String allowedListIps = "allowedListIps";

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getEdge()).thenReturn(edgeConfig);
        when(edgeConfig.getService()).thenReturn(service);
        when(service.isBuildCheckDisabled()).thenReturn(true);
        when(service.isAppendLog()).thenReturn(true);
        when(service.isReadableTimestamp()).thenReturn(true);
        when(service.getLogLevel()).thenReturn(ChromiumDriverLogLevel.ALL);
        when(service.isSilent()).thenReturn(true);
        when(service.isVerbose()).thenReturn(true);
        when(service.getAllowedListIps()).thenReturn(allowedListIps);

        MockedConstruction<EdgeDriverService.Builder> edgeDriverServiceMockedConstruction = mockConstruction((mock, context) -> {
            when(mock.withBuildCheckDisabled(true)).thenReturn(mock);
            when(mock.withAppendLog(true)).thenReturn(mock);
            when(mock.withReadableTimestamp(true)).thenReturn(mock);
            when(mock.withLoglevel(ChromiumDriverLogLevel.ALL)).thenReturn(mock);
            when(mock.withSilent(true)).thenReturn(mock);
            when(mock.withVerbose(true)).thenReturn(mock);
            when(mock.withAllowedListIps(allowedListIps)).thenReturn(mock);
        });

        final DriverService.Builder<EdgeDriverService, EdgeDriverService.Builder> driverServiceBuilder = edge.getDriverServiceBuilder();
        assertEquals(edgeDriverServiceMockedConstruction.constructed().getFirst(), driverServiceBuilder);

        edgeDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build an instance of EdgeOptions")
    void buildCapabilities() {
        try (MockedConstruction<EdgeOptions> edgeOptionsMockedConstruction = mockConstruction()) {
            assertEquals(edge, edge.buildCapabilities());

            final EdgeOptions edgeOptions = edgeOptionsMockedConstruction.constructed().getFirst();

            assertEquals(edgeOptions, Reflections.getFieldValue("capabilities", edge));
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should merge the provided configuration")
    void mergeCapabilitiesWith() {
        final List<String> arguments = List.of("args");

        Reflections.setField("capabilities", edge, capabilities);

        when(drivers.getEdge()).thenReturn(edgeConfig);
        when(drivers.getLogs()).thenReturn(logs);
        when(logs.getBrowser()).thenReturn(browserLevel);
        when(logs.getDriver()).thenReturn(driverLevel);
        when(logs.getPerformance()).thenReturn(performanceLevel);
        when(edgeConfig.getArgs()).thenReturn(arguments);
        when(edgeConfig.getCapabilities()).thenReturn(Map.of("capability", "value1"));
        when(edgeConfig.getExperimentalOptions()).thenReturn(Map.of("experimental", "value2"));

        // activateBiDi
        when(drivers.isBiDi()).thenReturn(false);
        when(edgeConfig.isBiDi()).thenReturn(true);

        try (MockedConstruction<LoggingPreferences> loggingPreferencesMockedConstruction = mockConstruction()) {

            assertEquals(edge, edge.mergeCapabilitiesWith(drivers));

            final LoggingPreferences loggingPreferences = loggingPreferencesMockedConstruction.constructed().getFirst();

            verify(loggingPreferences).enable(BROWSER, browserLevel);
            verify(loggingPreferences).enable(DRIVER, driverLevel);
            verify(loggingPreferences).enable(PERFORMANCE, performanceLevel);
            verify(capabilities).setCapability(LOGGING_PREFS, loggingPreferences);

            verify(capabilities).setCapability("capability", (Object) "value1");
            verify(capabilities).setExperimentalOption("experimental", "value2");
            verify(capabilities).setCapability("webSocketUrl", true);

            assertEquals(capabilities, Reflections.getFieldValue("capabilities", edge));
        }
    }
}
