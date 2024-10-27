package io.github.giulong.spectrum.drivers;

import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import static io.github.giulong.spectrum.drivers.Appium.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AppiumTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Environments environments;

    @Mock
    private Configuration.Environments.Appium appiumConfiguration;

    @Mock
    private Configuration.Environments.Appium.Service service;

    @Mock
    private Duration timeout;

    @Mock
    private Map<String, Object> capabilities;

    @InjectMocks
    private UiAutomator2 appium;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", appium, configuration);
    }

    @Test
    @DisplayName("getDriverServiceBuilder should return an instance of AppiumDriverServiceBuilder")
    void getDriverServiceBuilder() {
        final String ipAddress = "ipAddress";
        final int port = 123;

        when(configuration.getEnvironments()).thenReturn(environments);
        when(environments.getAppium()).thenReturn(appiumConfiguration);
        when(appiumConfiguration.getService()).thenReturn(service);
        when(service.getIpAddress()).thenReturn(ipAddress);
        when(service.getPort()).thenReturn(port);
        when(service.getTimeout()).thenReturn(timeout);

        MockedConstruction<AppiumServiceBuilder> appiumServiceBuilderMockedConstruction = mockConstruction(AppiumServiceBuilder.class, (mock, context) -> {
            when(mock.withIPAddress(ipAddress)).thenReturn(mock);
            when(mock.usingPort(port)).thenReturn(mock);
            when(mock.withTimeout(timeout)).thenReturn(mock);
        });

        final AppiumServiceBuilder actual = (AppiumServiceBuilder) appium.getDriverServiceBuilder();
        assertEquals(appiumServiceBuilderMockedConstruction.constructed().getFirst(), actual);

        appiumServiceBuilderMockedConstruction.close();
    }

    @Test
    @DisplayName("adjustCapabilitiesFrom should do nothing if the app capability is not set")
    void adjustCapabilitiesFromNoApp() {
        when(capabilities.get(APP_CAPABILITY)).thenReturn(null);

        assertEquals(capabilities, appium.adjustCapabilitiesFrom(capabilities));

        verifyNoMoreInteractions(capabilities);
    }

    @Test
    @DisplayName("adjustCapabilitiesFrom should set the app path absolute if it's relative")
    void adjustCapabilitiesFrom() {
        final Path path = Path.of("relative", "path");
        final String appPath = path.toString();
        final String appAbsolutePath = path.toAbsolutePath().toString();

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        assertEquals(capabilities, appium.adjustCapabilitiesFrom(capabilities));

        verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);
    }

    @Test
    @DisplayName("adjustCapabilitiesFrom should just return the provided capabilities if the app path absolute is already absolute")
    void adjustCapabilitiesFromAbsolute() {
        final String appPath = Path.of("absolute", "path").toAbsolutePath().toString();

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        assertEquals(capabilities, appium.adjustCapabilitiesFrom(capabilities));

        verify(capabilities, never()).put(anyString(), anyString());
    }
}
