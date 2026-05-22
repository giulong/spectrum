package io.github.giulong.spectrum.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.Map;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.service.DriverService;

class FirefoxTest {

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Configuration.Drivers.Firefox firefoxConfig;

    @Mock
    private Configuration.Drivers.Firefox.Service service;

    @Mock
    private File profileRoot;

    @InjectMocks
    private Firefox firefox;

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of GeckoDriverService.Builder()")
    void getDriverServiceBuilder() {
        final String allowHosts = "allowHosts";

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getService()).thenReturn(service);
        when(service.getAllowHosts()).thenReturn(allowHosts);
        when(service.getLogLevel()).thenReturn(FirefoxDriverLogLevel.TRACE);
        when(service.isTruncatedLogs()).thenReturn(true);
        when(service.getProfileRoot()).thenReturn(profileRoot);

        MockedConstruction<GeckoDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction((mock, context) -> {
            when(mock.withAllowHosts(allowHosts)).thenReturn(mock);
            when(mock.withLogLevel(FirefoxDriverLogLevel.TRACE)).thenReturn(mock);
            when(mock.withTruncatedLogs(true)).thenReturn(mock);
            when(mock.withProfileRoot(profileRoot)).thenReturn(mock);
        });

        final DriverService.Builder<GeckoDriverService, GeckoDriverService.Builder> driverServiceBuilder = firefox.getDriverServiceBuilder();
        assertEquals(chromeDriverServiceMockedConstruction.constructed().getFirst(), driverServiceBuilder);

        chromeDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build an instance of Firefox")
    void buildCapabilities() {
        try (MockedConstruction<FirefoxOptions> mockedConstruction = mockConstruction()) {
            assertEquals(firefox, firefox.buildCapabilities());

            final FirefoxOptions actual = Reflections.getFieldValue("capabilities", firefox);
            assertEquals(mockedConstruction.constructed().getFirst(), actual);
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should merge an instance of Firefox based on the provided configuration")
    void mergeCapabilitiesWith() {
        final List<String> arguments = List.of("args");
        final String binary = "binary";

        when(drivers.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getBinary()).thenReturn(binary);
        when(firefoxConfig.getArgs()).thenReturn(arguments);
        when(firefoxConfig.getPreferences()).thenReturn(Map.of("preference", "value1"));
        when(firefoxConfig.getCapabilities()).thenReturn(Map.of("capability", "value2"));

        // activateBiDi
        when(drivers.isBiDi()).thenReturn(false);
        when(firefoxConfig.isBiDi()).thenReturn(true);

        try (MockedConstruction<FirefoxOptions> firefoxOptionsMockedConstruction = mockConstruction((mock, context) -> {
            when(mock.addArguments(arguments)).thenReturn(mock);
            when(mock.setBinary(binary)).thenReturn(mock);
        })) {

            assertEquals(firefox, firefox.mergeCapabilitiesWith(drivers));

            final FirefoxOptions firefoxOptions = firefoxOptionsMockedConstruction.constructed().getFirst();
            verify(firefoxOptions).addPreference("preference", "value1");
            verify(firefoxOptions).setBinary(binary);
            verify(firefoxOptions).setCapability("capability", (Object) "value2");
            verify(firefoxOptions).setCapability("webSocketUrl", true);

            assertEquals(firefoxOptions, Reflections.getFieldValue("capabilities", firefox));
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should merge an instance of Firefox based on the provided configuration with no binary")
    void mergeCapabilitiesWithNoBinary() {
        final List<String> arguments = List.of("args");
        final String binary = "binary";

        when(drivers.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getBinary()).thenReturn(null);
        when(firefoxConfig.getArgs()).thenReturn(arguments);
        when(firefoxConfig.getPreferences()).thenReturn(Map.of("preference", "value1"));
        when(firefoxConfig.getCapabilities()).thenReturn(Map.of("capability", "value2"));

        // activateBiDi
        when(drivers.isBiDi()).thenReturn(false);
        when(firefoxConfig.isBiDi()).thenReturn(true);

        try (MockedConstruction<FirefoxOptions> firefoxOptionsMockedConstruction = mockConstruction((mock, context) -> {
            when(mock.addArguments(arguments)).thenReturn(mock);
            when(mock.setBinary(binary)).thenReturn(mock);
        })) {
            assertEquals(firefox, firefox.mergeCapabilitiesWith(drivers));

            final FirefoxOptions firefoxOptions = firefoxOptionsMockedConstruction.constructed().getFirst();
            verify(firefoxOptions).addPreference("preference", "value1");
            verify(firefoxOptions, never()).setBinary(anyString());
            verify(firefoxOptions).setCapability("capability", (Object) "value2");
            verify(firefoxOptions).setCapability("webSocketUrl", true);

            assertEquals(firefoxOptions, Reflections.getFieldValue("capabilities", firefox));
        }
    }
}
