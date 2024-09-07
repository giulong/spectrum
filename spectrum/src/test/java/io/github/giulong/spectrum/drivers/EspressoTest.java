package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.EspressoOptions;
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

import static io.github.giulong.spectrum.drivers.Appium.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EspressoTest {

    @Mock
    private EspressoOptions espressoOptions;

    @Mock
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
    public void beforeEach() {
        Reflections.setField("configuration", espresso, configuration);
        Reflections.setField("capabilities", espresso, espressoOptions);
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of espresso and set the capabilities from the yaml on it, when a relative path is provided as 'app' capability")
    public void buildCapabilities() {
        final Path path = Path.of("relative", "path");
        final String appPath = path.toString();
        final String appAbsolutePath = path.toAbsolutePath().toString();

        MockedConstruction<EspressoOptions> desiredCapabilitiesMockedConstruction = mockConstruction(EspressoOptions.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getEspresso()).thenReturn(espressoConfiguration);
        when(espressoConfiguration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        espresso.buildCapabilities();

        final EspressoOptions actual = Reflections.getFieldValue("capabilities", espresso, EspressoOptions.class);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of espresso and set the capabilities from the yaml on it, when an absolute path is provided as 'app' capability")
    public void buildCapabilitiesAbsoluteAppPath() {
        final String appPath = Path.of("absolute", "path").toAbsolutePath().toString();

        MockedConstruction<EspressoOptions> desiredCapabilitiesMockedConstruction = mockConstruction(EspressoOptions.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getEspresso()).thenReturn(espressoConfiguration);
        when(espressoConfiguration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        espresso.buildCapabilities();

        final EspressoOptions actual = Reflections.getFieldValue("capabilities", espresso, EspressoOptions.class);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }
}