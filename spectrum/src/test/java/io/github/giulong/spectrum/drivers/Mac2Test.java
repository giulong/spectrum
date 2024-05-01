package io.github.giulong.spectrum.drivers;

import io.appium.java_client.mac.Mac2Driver;
import io.appium.java_client.mac.options.Mac2Options;
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

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Mac2Test {

    @Mock
    private Mac2Options mac2Options;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Configuration.Drivers.Mac2 mac2Configuration;

    @Mock
    private URL url;

    @InjectMocks
    private Mac2 mac2;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("configuration", mac2, configuration);
        Reflections.setField("capabilities", mac2, mac2Options);
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of mac2Options and set the capabilities from the yaml on it")
    public void buildCapabilitiesAbsoluteAppPath() {
        MockedConstruction<Mac2Options> desiredCapabilitiesMockedConstruction = mockConstruction(Mac2Options.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getMac2()).thenReturn(mac2Configuration);
        when(mac2Configuration.getCapabilities()).thenReturn(capabilities);

        mac2.buildCapabilities();

        final Mac2Options actual = (Mac2Options) Reflections.getFieldValue("capabilities", mac2);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildDriverFor should return a new instance of Mac2Driver for the provided url and the instance capabilities")
    public void buildDriverFor() {
        MockedConstruction<Mac2Driver> mac2DriverMockedConstruction = mockConstruction(Mac2Driver.class, (mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(mac2Options, context.arguments().get(1));
        });

        assertEquals(mac2.buildDriverFor(url), mac2DriverMockedConstruction.constructed().getFirst());

        mac2DriverMockedConstruction.close();
    }
}