package io.github.giulong.spectrum.drivers;

import io.appium.java_client.service.local.AppiumServiceBuilder;
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

import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;

import static io.github.giulong.spectrum.drivers.Appium.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    public void beforeEach() {
        Reflections.setField("configuration", appium, configuration);
    }

    @Test
    @DisplayName("getDriverServiceBuilder should return an instance of AppiumDriverServiceBuilder")
    public void getDriverServiceBuilder() {
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
    @DisplayName("adjustCapabilitiesFrom should set the app path absolute if it's relative")
    public void adjustCapabilitiesFrom() {
        final Path path = Path.of("relative", "path");
        final String appPath = path.toString();
        final String appAbsolutePath = path.toAbsolutePath().toString();

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        assertEquals(capabilities, appium.adjustCapabilitiesFrom(capabilities));

        verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);
    }

    @Test
    @DisplayName("adjustCapabilitiesFrom should just return the provided capabilities if the app path absolute is already absolute")
    public void adjustCapabilitiesFromAbsolute() {
        final String appPath = Path.of("absolute", "path").toAbsolutePath().toString();

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        assertEquals(capabilities, appium.adjustCapabilitiesFrom(capabilities));

        verify(capabilities, never()).put(anyString(), anyString());
    }
}