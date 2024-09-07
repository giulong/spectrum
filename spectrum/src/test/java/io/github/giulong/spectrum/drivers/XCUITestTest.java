package io.github.giulong.spectrum.drivers;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

import static io.github.giulong.spectrum.drivers.Appium.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class XCUITestTest {

    @Mock
    private XCUITestOptions xcuiTestOptions;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Configuration.Drivers.XCUITest xcuiTestConfiguration;

    @Mock
    private URL url;

    @InjectMocks
    private XCUITest xcuiTest;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("configuration", xcuiTest, configuration);
        Reflections.setField("capabilities", xcuiTest, xcuiTestOptions);
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of xcuiTestOptions and set the capabilities from the yaml on it, when a relative path is provided as 'app' capability")
    public void buildCapabilities() {
        final Path path = Path.of("relative", "path");
        final String appPath = path.toString();
        final String appAbsolutePath = path.toAbsolutePath().toString();

        MockedConstruction<XCUITestOptions> desiredCapabilitiesMockedConstruction = mockConstruction(XCUITestOptions.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getXcuiTest()).thenReturn(xcuiTestConfiguration);
        when(xcuiTestConfiguration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        xcuiTest.buildCapabilities();

        final XCUITestOptions actual = Reflections.getFieldValue("capabilities", xcuiTest, XCUITestOptions.class);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of UiAutomator2Options and set the capabilities from the yaml on it, when an absolute path is provided as 'app' capability")
    public void buildCapabilitiesAbsoluteAppPath() {
        final String appPath = Path.of("absolute", "path").toAbsolutePath().toString();

        MockedConstruction<XCUITestOptions> desiredCapabilitiesMockedConstruction = mockConstruction(XCUITestOptions.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getXcuiTest()).thenReturn(xcuiTestConfiguration);
        when(xcuiTestConfiguration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        xcuiTest.buildCapabilities();

        final XCUITestOptions actual = Reflections.getFieldValue("capabilities", xcuiTest, XCUITestOptions.class);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildDriverFor should return a new instance of IOSDriver for the provided url and the instance capabilities")
    public void buildDriverFor() {
        MockedConstruction<IOSDriver> iosDriverMockedConstruction = mockConstruction(IOSDriver.class, (mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(xcuiTestOptions, context.arguments().get(1));
        });

        assertEquals(xcuiTest.buildDriverFor(url), iosDriverMockedConstruction.constructed().getFirst());

        iosDriverMockedConstruction.close();
    }
}