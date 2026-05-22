package io.github.giulong.spectrum.drivers;

import static io.github.giulong.spectrum.drivers.Appium.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Map;

import io.appium.java_client.android.options.EspressoOptions;
import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

class EspressoTest {

    @Mock
    private EspressoOptions espressoOptions;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Configuration.Drivers.Espresso espressoConfiguration;

    @InjectMocks
    private Espresso espresso;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("capabilities", espresso, espressoOptions);
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of EspressoOptions")
    void buildCapabilities() {
        try (MockedConstruction<EspressoOptions> mockedConstruction = mockConstruction()) {
            assertEquals(espresso, espresso.buildCapabilities());

            final EspressoOptions actual = Reflections.getFieldValue("capabilities", espresso);
            assertEquals(mockedConstruction.constructed().getFirst(), actual);
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should merge a new instance of espresso and set the provided capabilities, when a relative path is provided as 'app' capability")
    void mergeCapabilitiesWith() {
        final Path path = Path.of("relative", "path");
        final String appPath = path.toString();
        final String appAbsolutePath = path.toAbsolutePath().toString();

        Reflections.setField("capabilities", espresso, espressoOptions);

        try (MockedConstruction<EspressoOptions> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(capabilities, context.arguments().getFirst()))) {

            when(drivers.getEspresso()).thenReturn(espressoConfiguration);
            when(espressoConfiguration.getCapabilities()).thenReturn(capabilities);

            when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

            assertEquals(espresso, espresso.mergeCapabilitiesWith(drivers));

            verify(espressoOptions).merge(mockedConstruction.constructed().getFirst());
            verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);
        }
    }

    @Test
    @DisplayName("mergeCapabilitiesWith should merge a new instance of espresso and set the provided capabilities, when an absolute path is provided as 'app' capability")
    void mergeCapabilitiesWithAbsoluteAppPath() {
        final String appPath = Path.of("absolute", "path").toAbsolutePath().toString();

        Reflections.setField("capabilities", espresso, espressoOptions);

        try (MockedConstruction<EspressoOptions> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(capabilities, context.arguments().getFirst()))) {

            when(drivers.getEspresso()).thenReturn(espressoConfiguration);
            when(espressoConfiguration.getCapabilities()).thenReturn(capabilities);

            when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

            assertEquals(espresso, espresso.mergeCapabilitiesWith(drivers));

            verify(espressoOptions).merge(mockedConstruction.constructed().getFirst());
        }
    }
}
