package io.github.giulong.spectrum.drivers;

import io.appium.java_client.AppiumDriver;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class AppiumGenericTest {

    @Mock
    private MutableCapabilities mutableCapabilities;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Configuration.Drivers.AppiumGeneric appiumGenericConfiguration;

    @Mock
    private URL url;

    @InjectMocks
    private AppiumGeneric appiumGeneric;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", appiumGeneric, configuration);
        Reflections.setField("capabilities", appiumGeneric, mutableCapabilities);
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of capabilities and set the capabilities from the yaml on it")
    void buildCapabilitiesAbsoluteAppPath() {
        MockedConstruction<MutableCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction(MutableCapabilities.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getAppiumGeneric()).thenReturn(appiumGenericConfiguration);
        when(appiumGenericConfiguration.getCapabilities()).thenReturn(capabilities);

        appiumGeneric.buildCapabilities();

        final Capabilities actual = Reflections.getFieldValue("capabilities", appiumGeneric, Capabilities.class);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildDriverFor should return a new instance of AppiumDriver for the provided url and the instance capabilities")
    void buildDriverFor() {
        MockedConstruction<AppiumDriver> appiumDriverMockedConstruction = mockConstruction(AppiumDriver.class, (mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(mutableCapabilities, context.arguments().get(1));
        });

        assertEquals(appiumGeneric.buildDriverFor(url), appiumDriverMockedConstruction.constructed().getFirst());

        appiumDriverMockedConstruction.close();
    }
}
