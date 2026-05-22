package io.github.giulong.spectrum.drivers;

import static io.github.giulong.spectrum.drivers.Android.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Map;

import io.appium.java_client.android.options.UiAutomator2Options;
import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

class UiAutomator2Test {

    @Mock
    private UiAutomator2Options uiAutomator2Options;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Configuration.Drivers.UiAutomator2 uiAutomator2Configuration;

    @InjectMocks
    private UiAutomator2 uiAutomator2;

    @Test
    @DisplayName("buildCapabilities should do nothing")
    void buildCapabilities() {
        assertEquals(uiAutomator2, uiAutomator2.buildCapabilities());
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should build a new instance of UiAutomator2Options " +
            "and set the capabilities from the yaml on it, when a relative path is provided as 'app' capability")
    void mergeCapabilitiesWith() {
        final Path path = Path.of("relative", "path");
        final String appPath = path.toString();
        final String appAbsolutePath = path.toAbsolutePath().toString();

        Reflections.setField("capabilities", uiAutomator2, uiAutomator2Options);

        try (MockedConstruction<UiAutomator2Options> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(capabilities, context.arguments().getFirst()))) {

            when(drivers.getUiAutomator2()).thenReturn(uiAutomator2Configuration);
            when(uiAutomator2Configuration.getCapabilities()).thenReturn(capabilities);

            when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

            assertEquals(uiAutomator2, uiAutomator2.mergeCapabilitiesWith(drivers));

            final UiAutomator2Options actual = Reflections.getFieldValue("capabilities", uiAutomator2);
            assertEquals(mockedConstruction.constructed().getFirst(), actual);

            verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should build a new instance of UiAutomator2Options " +
            "and set the capabilities from the yaml on it, when an absolute path is provided as 'app' capability")
    void mergeCapabilitiesWithAbsoluteAppPath() {
        final String appPath = Path.of("absolute", "path").toAbsolutePath().toString();

        Reflections.setField("capabilities", uiAutomator2, uiAutomator2Options);

        try (MockedConstruction<UiAutomator2Options> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(capabilities, context.arguments().getFirst()))) {

            when(drivers.getUiAutomator2()).thenReturn(uiAutomator2Configuration);
            when(uiAutomator2Configuration.getCapabilities()).thenReturn(capabilities);

            when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

            assertEquals(uiAutomator2, uiAutomator2.mergeCapabilitiesWith(drivers));

            final UiAutomator2Options actual = Reflections.getFieldValue("capabilities", uiAutomator2);
            assertEquals(mockedConstruction.constructed().getFirst(), actual);
        }
    }
}
