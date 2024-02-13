package io.github.giulong.spectrum.drivers;

import io.appium.java_client.android.options.EspressoOptions;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;

import static io.github.giulong.spectrum.drivers.Appium.APP_CAPABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Espresso")
class EspressoTest {

    @Mock
    private EspressoOptions espressoOptions;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriver;

    @Mock
    private Map<String, Object> capabilities;

    @Mock
    private Map<String, Object> gridCapabilities;

    @Mock
    private Configuration.WebDriver.Espresso espressoConfiguration;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

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
        final String appPath = "relative/path";
        final String appAbsolutePath = System.getProperty("user.dir") + "/relative/path";

        MockedConstruction<EspressoOptions> desiredCapabilitiesMockedConstruction = mockConstruction(EspressoOptions.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getWebDriver()).thenReturn(webDriver);
        when(webDriver.getEspresso()).thenReturn(espressoConfiguration);
        when(espressoConfiguration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        espresso.buildCapabilities();

        final EspressoOptions actual = (EspressoOptions) Reflections.getFieldValue("capabilities", espresso);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        verify(capabilities).put(APP_CAPABILITY, appAbsolutePath);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilities should build a new instance of espresso and set the capabilities from the yaml on it, when an absolute path is provided as 'app' capability")
    public void buildCapabilitiesAbsoluteAppPath() {
        final String appPath = "/absolute/path";

        MockedConstruction<EspressoOptions> desiredCapabilitiesMockedConstruction = mockConstruction(EspressoOptions.class, (mock, context) -> {
            assertEquals(capabilities, context.arguments().getFirst());
        });

        when(configuration.getWebDriver()).thenReturn(webDriver);
        when(webDriver.getEspresso()).thenReturn(espressoConfiguration);
        when(espressoConfiguration.getCapabilities()).thenReturn(capabilities);

        when(capabilities.get(APP_CAPABILITY)).thenReturn(appPath);

        espresso.buildCapabilities();

        final EspressoOptions actual = (EspressoOptions) Reflections.getFieldValue("capabilities", espresso);
        assertEquals(desiredCapabilitiesMockedConstruction.constructed().getFirst(), actual);

        desiredCapabilitiesMockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities and return the capabilities")
    public void mergeGridCapabilitiesFrom() {
        when(espressoOptions.merge(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(espressoOptions);

        MockedConstruction<DesiredCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction(DesiredCapabilities.class, (mock, context) -> {
            assertEquals(gridCapabilities, context.arguments().getFirst());
        });

        final EspressoOptions actual = espresso.mergeGridCapabilitiesFrom(gridCapabilities);
        verify(espressoOptions).merge(desiredCapabilitiesMockedConstruction.constructed().getFirst());
        assertEquals(actual, espressoOptions);

        desiredCapabilitiesMockedConstruction.close();
    }
}