package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

import java.nio.file.Path;
import java.util.Map;

import static io.github.giulong.spectrum.drivers.Android.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UiAutomator2Test {

    @Mock
    private UiAutomator2Options uiAutomator2Options;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Configuration.Drivers.UiAutomator2 uiAutomator2Configuration;

    @InjectMocks
    private UiAutomator2 uiAutomator2;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", uiAutomator2, configuration);
        Reflections.setField("capabilities", uiAutomator2, uiAutomator2Options);
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of UiAutomator2Options " +
            "and set the capabilities from the yaml on it, when a relative path is provided as 'app' capability")
    void buildCapabilities() {
        final Path path = Path.of("relative", "path");
        final String appPath = path.toString();
        final String appAbsolutePath = path.toAbsolutePath().toString();

        MockedConstruction<UiAutomator2Options> desiredCapabilitiesMockedConstruction = mockConstruction(UiAutomator2Options.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getUiAutomator2()).thenReturn(uiAutomator2Configuration);
        when(uiAutomator2Configuration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        uiAutomator2.buildCapabilities();

        final UiAutomator2Options actual = Reflections.getFieldValue("capabilities", uiAutomator2, UiAutomator2Options.class);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of UiAutomator2Options " +
            "and set the capabilities from the yaml on it, when an absolute path is provided as 'app' capability")
    void buildCapabilitiesAbsoluteAppPath() {
        final String appPath = Path.of("absolute", "path").toAbsolutePath().toString();

        MockedConstruction<UiAutomator2Options> desiredCapabilitiesMockedConstruction = mockConstruction(UiAutomator2Options.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getUiAutomator2()).thenReturn(uiAutomator2Configuration);
        when(uiAutomator2Configuration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        uiAutomator2.buildCapabilities();

        final UiAutomator2Options actual = Reflections.getFieldValue("capabilities", uiAutomator2, UiAutomator2Options.class);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }
}
