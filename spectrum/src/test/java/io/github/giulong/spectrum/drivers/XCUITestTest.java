package io.github.giulong.spectrum.drivers;

import static io.github.giulong.spectrum.drivers.Appium.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

class XCUITestTest {

    @Mock
    private XCUITestOptions xcuiTestOptions;

    @MockFinal
    @SuppressWarnings("unused")
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

    @Test
    @DisplayName("buildCapabilities should build a new instance of XCUITestOptions")
    void buildCapabilities() {
        try (MockedConstruction<XCUITestOptions> mockedConstruction = mockConstruction()) {
            assertEquals(xcuiTest, xcuiTest.buildCapabilities());

            final XCUITestOptions actual = Reflections.getFieldValue("capabilities", xcuiTest);
            assertEquals(mockedConstruction.constructed().getFirst(), actual);
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should build a new instance of xcuiTestOptions " +
            "and set the capabilities from the yaml on it, when a relative path is provided as 'app' capability")
    void mergeCapabilitiesWith() {
        final Path path = Path.of("relative", "path");
        final String appPath = path.toString();
        final String appAbsolutePath = path.toAbsolutePath().toString();

        Reflections.setField("capabilities", xcuiTest, xcuiTestOptions);

        try (MockedConstruction<XCUITestOptions> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(capabilities, context.arguments().getFirst()))) {

            when(drivers.getXcuiTest()).thenReturn(xcuiTestConfiguration);
            when(xcuiTestConfiguration.getCapabilities()).thenReturn(capabilities);

            when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

            xcuiTest.mergeCapabilitiesWith(drivers);

            verify(xcuiTestOptions).merge(mockedConstruction.constructed().getFirst());
            verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should build a new instance of UiAutomator2Options " +
            "and set the capabilities from the yaml on it, when an absolute path is provided as 'app' capability")
    void mergeCapabilitiesWithAbsoluteAppPath() {
        final String appPath = Path.of("absolute", "path").toAbsolutePath().toString();

        Reflections.setField("capabilities", xcuiTest, xcuiTestOptions);

        try (MockedConstruction<XCUITestOptions> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(capabilities, context.arguments().getFirst()))) {

            when(drivers.getXcuiTest()).thenReturn(xcuiTestConfiguration);
            when(xcuiTestConfiguration.getCapabilities()).thenReturn(capabilities);

            when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

            assertEquals(xcuiTest, xcuiTest.mergeCapabilitiesWith(drivers));

            verify(xcuiTestOptions).merge(mockedConstruction.constructed().getFirst());
        }
    }

    @Test
    @DisplayName("buildDriverFor should return a new instance of IOSDriver for the provided url and the instance capabilities")
    void buildDriverFor() {
        Reflections.setField("capabilities", xcuiTest, xcuiTestOptions);

        try (MockedConstruction<IOSDriver> mockedConstruction = mockConstruction((mock, context) -> {
            assertEquals(url, context.arguments().getFirst());
            assertEquals(xcuiTestOptions, context.arguments().get(1));
        })) {
            assertEquals(xcuiTest.buildDriverFor(url), mockedConstruction.constructed().getFirst());
        }
    }
}
