package io.github.giulong.spectrum.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Map;

import io.appium.java_client.AppiumDriver;
import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.MutableCapabilities;

class AppiumGenericTest {

    @Mock
    private MutableCapabilities mutableCapabilities;

    @MockFinal
    @SuppressWarnings("unused")
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

    @Test
    @DisplayName("buildCapabilities should build a new instance of MutableCapabilities")
    void buildCapabilities() {
        try (MockedConstruction<MutableCapabilities> mockedConstruction = mockConstruction()) {
            assertEquals(appiumGeneric, appiumGeneric.buildCapabilities());

            final MutableCapabilities actual = Reflections.getFieldValue("capabilities", appiumGeneric);
            assertEquals(mockedConstruction.constructed().getFirst(), actual);
        }
    }

    @Test
    @DisplayName("buildDriverFor should return a new instance of AppiumDriver for the provided url and the instance capabilities")
    void buildDriverFor() {
        Reflections.setField("capabilities", appiumGeneric, mutableCapabilities);

        try (MockedConstruction<AppiumDriver> mockedConstruction = mockConstruction((mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(mutableCapabilities, context.arguments().get(1));
        })) {
            assertEquals(appiumGeneric.buildDriverFor(url), mockedConstruction.constructed().getFirst());
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should build a new instance of capabilities and set the capabilities from the yaml on it")
    void mergeCapabilitiesWithAbsoluteAppPath() {
        Reflections.setField("capabilities", appiumGeneric, mutableCapabilities);

        try (MockedConstruction<MutableCapabilities> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(capabilities, context.arguments().getFirst()))) {

            when(drivers.getAppiumGeneric()).thenReturn(appiumGenericConfiguration);
            when(appiumGenericConfiguration.getCapabilities()).thenReturn(capabilities);

            assertEquals(appiumGeneric, appiumGeneric.mergeCapabilitiesWith(drivers));

            verify(mutableCapabilities).merge(mockedConstruction.constructed().getFirst());
        }
    }
}
