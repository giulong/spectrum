package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.service.DriverService;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FirefoxTest {

    @Mock
    private FirefoxOptions firefoxOptions;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers driversConfig;

    @Mock
    private Configuration.Drivers.Firefox firefoxConfig;

    @Mock
    private Configuration.Drivers.Firefox.Service service;

    @Mock
    private File profileRoot;

    @InjectMocks
    private Firefox firefox;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", firefox, configuration);
        Reflections.setField("capabilities", firefox, firefoxOptions);
    }

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of GeckoDriverService.Builder()")
    void getDriverServiceBuilder() {
        final String allowHosts = "allowHosts";

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getService()).thenReturn(service);
        when(service.getAllowHosts()).thenReturn(allowHosts);
        when(service.getLogLevel()).thenReturn(FirefoxDriverLogLevel.TRACE);
        when(service.isTruncatedLogs()).thenReturn(true);
        when(service.getProfileRoot()).thenReturn(profileRoot);

        MockedConstruction<GeckoDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction(GeckoDriverService.Builder.class, (mock, context) -> {
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
    @DisplayName("buildCapabilitiesFrom should build an instance of Firefox based on the provided configuration")
    void buildCapabilitiesFrom() {
        final List<String> arguments = List.of("args");
        final String binary = "binary";

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getBinary()).thenReturn(binary);
        when(firefoxConfig.getArgs()).thenReturn(arguments);
        when(firefoxConfig.getPreferences()).thenReturn(Map.of("preference", "value1"));
        when(firefoxConfig.getCapabilities()).thenReturn(Map.of("capability", "value2"));

        MockedConstruction<FirefoxOptions> firefoxOptionsMockedConstruction = mockConstruction(FirefoxOptions.class, (mock, context) -> {
            when(mock.addArguments(arguments)).thenReturn(mock);
            when(mock.setBinary(binary)).thenReturn(mock);
        });

        firefox.buildCapabilities();

        final FirefoxOptions localFirefoxOptions = firefoxOptionsMockedConstruction.constructed().getFirst();
        verify(localFirefoxOptions).addPreference("preference", "value1");
        verify(localFirefoxOptions).setBinary(binary);
        verify(localFirefoxOptions).setCapability("capability", (Object) "value2");

        assertEquals(localFirefoxOptions, Reflections.getFieldValue("capabilities", firefox));

        firefoxOptionsMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Firefox based on the provided configuration")
    void buildCapabilitiesFromNoBinary() {
        final List<String> arguments = List.of("args");

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getArgs()).thenReturn(arguments);
        when(firefoxConfig.getPreferences()).thenReturn(Map.of("preference", "value1"));
        when(firefoxConfig.getCapabilities()).thenReturn(Map.of("capability", "value2"));

        MockedConstruction<FirefoxOptions> firefoxOptionsMockedConstruction = mockConstruction(FirefoxOptions.class, (mock, context) -> {
            when(mock.addArguments(arguments)).thenReturn(mock);
        });

        firefox.buildCapabilities();

        final FirefoxOptions localFirefoxOptions = firefoxOptionsMockedConstruction.constructed().getFirst();
        verify(localFirefoxOptions).addPreference("preference", "value1");
        verify(localFirefoxOptions, never()).setBinary(anyString());
        verify(localFirefoxOptions).setCapability("capability", (Object) "value2");

        assertEquals(localFirefoxOptions, Reflections.getFieldValue("capabilities", firefox));

        firefoxOptionsMockedConstruction.close();
    }
}
