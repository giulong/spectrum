package io.github.giulong.spectrum.drivers;

import io.appium.java_client.mac.Mac2Driver;
import io.appium.java_client.mac.options.Mac2Options;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mac2")
class Mac2Test {

    @Mock
    private Mac2Options mac2Options;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriver;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Map<String, Object> gridCapabilities;

    @Mock
    private Configuration.WebDriver.Mac2 mac2Configuration;

    @Mock
    private URL url;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

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

        when(configuration.getWebDriver()).thenReturn(webDriver);
        when(webDriver.getMac2()).thenReturn(mac2Configuration);
        when(mac2Configuration.getCapabilities()).thenReturn(capabilities);

        mac2.buildCapabilities();

        final Mac2Options actual = (Mac2Options) Reflections.getFieldValue("capabilities", mac2);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities and return the capabilities")
    public void mergeGridCapabilitiesFrom() {
        when(mac2Options.merge(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(mac2Options);

        MockedConstruction<DesiredCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction(DesiredCapabilities.class, (mock, context) -> {
            assertEquals(gridCapabilities, context.arguments().getFirst());
        });

        final Mac2Options actual = mac2.mergeGridCapabilitiesFrom(gridCapabilities);
        verify(mac2Options).merge(desiredCapabilitiesMockedConstruction.constructed().getFirst());
        assertEquals(actual, mac2Options);

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