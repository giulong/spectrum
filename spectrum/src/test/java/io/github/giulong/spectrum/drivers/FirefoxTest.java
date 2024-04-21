package io.github.giulong.spectrum.drivers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.remote.service.DriverService;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FirefoxTest {

    @Mock
    private FirefoxOptions firefoxOptions;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers driversConfig;

    @Mock
    private Configuration.Drivers.Firefox firefoxConfig;

    @Mock
    private FirefoxDriverLogLevel firefoxDriverLogLevel;

    @Mock
    private Configuration.Drivers.Firefox.Service service;

    @Mock
    private File profileRoot;

    @InjectMocks
    private Firefox firefox;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("configuration", firefox, configuration);
        Reflections.setField("capabilities", firefox, firefoxOptions);
    }

    @Test
    @DisplayName("getDriverServiceBuilder should return a new instance of GeckoDriverService.Builder()")
    public void getDriverServiceBuilder() {
        final String allowHosts = "allowHosts";

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getService()).thenReturn(service);
        when(service.getAllowHosts()).thenReturn(allowHosts);
        when(service.getLogLevel()).thenReturn(FirefoxDriverLogLevel.TRACE);
        when(service.isTruncatedLogs()).thenReturn(true);
        when(service.getProfileRoot()).thenReturn(profileRoot);

        MockedConstruction<GeckoDriverService.Builder> chromeDriverServiceMockedConstruction = mockConstruction(GeckoDriverService.Builder.class, (mock, context) -> {
            when(mock.withAllowHosts(allowHosts)).thenReturn(mock);
            when(mock.withLogLevel(FirefoxDriverLogLevel.TRACE)).thenReturn(mock);
            when(mock.withTruncatedLogs(true)).thenReturn(mock);
            when(mock.withProfileRoot(profileRoot)).thenReturn(mock);
        });

        final DriverService.Builder<GeckoDriverService, GeckoDriverService.Builder> driverServiceBuilder = firefox.getDriverServiceBuilder();
        assertEquals(chromeDriverServiceMockedConstruction.constructed().getFirst(), driverServiceBuilder);

        chromeDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Firefox based on the provided configuration")
    public void buildCapabilitiesFrom() {
        final List<String> arguments = List.of("args");

        when(configuration.getDrivers()).thenReturn(driversConfig);
        when(driversConfig.getFirefox()).thenReturn(firefoxConfig);
        when(firefoxConfig.getArgs()).thenReturn(arguments);
        when(firefoxConfig.getLogLevel()).thenReturn(firefoxDriverLogLevel);
        when(firefoxConfig.getPreferences()).thenReturn(Map.of("one", "value"));

        MockedConstruction<FirefoxOptions> firefoxOptionsMockedConstruction = mockConstruction(FirefoxOptions.class, (mock, context) -> {
            when(mock.addArguments(arguments)).thenReturn(mock);
            when(mock.setLogLevel(firefoxDriverLogLevel)).thenReturn(mock);
        });

        firefox.buildCapabilities();

        final FirefoxOptions firefoxOptions = firefoxOptionsMockedConstruction.constructed().getFirst();
        verify(firefoxOptions).addPreference("one", "value");
        assertEquals(firefoxOptions, Reflections.getFieldValue("capabilities", firefox));

        firefoxOptionsMockedConstruction.close();
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
