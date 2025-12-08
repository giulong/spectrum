package io.github.giulong.spectrum.interfaces;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import io.github.giulong.spectrum.drivers.Chrome;
import io.github.giulong.spectrum.utils.Configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.chrome.ChromeOptions;

class BiDiDriverTest {

    @Mock
    private ChromeOptions capabilities;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers.Chrome biDiDriverConfiguration;

    @Mock
    private Configuration.Drivers drivers;

    @InjectMocks
    private Chrome chrome;

    @DisplayName("activateBiDi should set the webSocketUrl capability according to the provided bidi configuration")
    @ParameterizedTest(name = "with all drivers bidi {0} and specific driver bidi {1} we expect {2}")
    @MethodSource("valuesProvider")
    void activateBiDi(final boolean allDriversBiDi, final boolean driverBiDi, final boolean expected) {
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.isBiDi()).thenReturn(allDriversBiDi);
        lenient().when(biDiDriverConfiguration.isBiDi()).thenReturn(driverBiDi);

        chrome.activateBiDi(capabilities, configuration, biDiDriverConfiguration);

        verify(capabilities).setCapability("webSocketUrl", expected);
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(true, true, true),
                arguments(true, false, true),
                arguments(false, true, true),
                arguments(false, false, false));
    }
}
