package io.github.giulong.spectrum.browsers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.service.DriverService;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Firefox")
class FirefoxTest {

    @Mock
    private FirefoxOptions firefoxOptions;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriverConfig;

    @Mock
    private Configuration.WebDriver.Firefox firefoxConfig;

    @Mock
    private FirefoxDriverLogLevel firefoxDriverLogLevel;

    @Mock
    private Map<String, String> gridCapabilities;

    @Captor
    private ArgumentCaptor<DesiredCapabilities> desiredCapabilitiesArgumentCaptor;

    @InjectMocks
    private Firefox firefox;

    @BeforeEach
    public void beforeEach() {
        Reflections.setParentField("configuration", firefox, firefox.getClass().getSuperclass(), configuration);
        Reflections.setParentField("capabilities", firefox, firefox.getClass().getSuperclass(), firefoxOptions);
    }

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of GeckoDriverService.Builder()")
    public void getDriverServiceBuilder() {
        MockedConstruction<GeckoDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction(GeckoDriverService.Builder.class);

        final DriverService.Builder<GeckoDriverService, GeckoDriverService.Builder> driverServiceBuilder = firefox.getDriverServiceBuilder();
        assertEquals(chromeDriverServiceMockedConstruction.constructed().getFirst(), driverServiceBuilder);

        chromeDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Firefox based on the provided configuration")
    public void buildCapabilitiesFrom() {
        final List<String> arguments = List.of("args");

        when(configuration.getWebDriver()).thenReturn(webDriverConfig);
        when(webDriverConfig.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getArgs()).thenReturn(arguments);
        when(firefoxConfig.getLogLevel()).thenReturn(firefoxDriverLogLevel);
        when(firefoxConfig.getPreferences()).thenReturn(Map.of("one", "value"));

        MockedConstruction<FirefoxOptions> firefoxOptionsMockedConstruction = mockConstruction(FirefoxOptions.class);

        firefox.buildCapabilities();
        final FirefoxOptions firefoxOptions = firefoxOptionsMockedConstruction.constructed().getFirst();
        verify(firefoxOptions).addArguments(arguments);
        verify(firefoxOptions).setLogLevel(firefoxDriverLogLevel);
        verify(firefoxOptions).addPreference("one", "value");

        firefoxOptionsMockedConstruction.close();
    }

    @Test
    @DisplayName("mergeGridCapabilitiesFrom should add the provided grid capabilities and return the capabilities")
    public void mergeGridCapabilitiesFrom() {
        when(firefoxOptions.merge(desiredCapabilitiesArgumentCaptor.capture())).thenReturn(firefoxOptions);

        MockedConstruction<DesiredCapabilities> desiredCapabilitiesMockedConstruction = mockConstruction(DesiredCapabilities.class, (mock, context) -> {
            assertEquals(gridCapabilities, context.arguments().getFirst());
        });

        final FirefoxOptions actual = firefox.mergeGridCapabilitiesFrom(gridCapabilities);
        verify(firefoxOptions).merge(desiredCapabilitiesMockedConstruction.constructed().getFirst());
        assertEquals(actual, firefoxOptions);

        desiredCapabilitiesMockedConstruction.close();
    }

    @DisplayName("addPreference should add the correct preference based on the value type")
    @ParameterizedTest(name = "with value {0} we expect {1}")
    @MethodSource("valuesProvider")
    public void addPreference(final Object value, final Object expected) {
        firefox.capabilities = firefoxOptions;

        firefox.addPreference("key", value);
        verify(firefoxOptions).addPreference("key", expected);
    }

    public static Stream<Arguments> valuesProvider() {
        final DummyObject dummyObject = new DummyObject();

        return Stream.of(
                arguments(true, true),
                arguments(123, 123),
                arguments("123", "123"),
                arguments("true", "true"),
                arguments(dummyObject, dummyObject.toString())
        );
    }

    private static class DummyObject {

        @Override
        public String toString() {
            return "toString";
        }
    }
}
