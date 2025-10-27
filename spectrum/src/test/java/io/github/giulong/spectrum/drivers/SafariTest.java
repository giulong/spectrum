package io.github.giulong.spectrum.drivers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.safari.SafariDriverService;
import org.openqa.selenium.safari.SafariOptions;

class SafariTest {

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Configuration.Drivers.Safari safariConfig;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Drivers.Safari.Service service;

    @InjectMocks
    private Safari safari;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", safari, configuration);
    }

    @DisplayName("getDriverServiceBuilder should return a new instance of SafariDriverService.Builder()")
    @ParameterizedTest(name = "with logging {0}")
    @ValueSource(booleans = {true, false})
    void getDriverServiceBuilder(final boolean logging) {
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getSafari()).thenReturn(safariConfig);
        when(safariConfig.getService()).thenReturn(service);
        when(service.isLogging()).thenReturn(logging);

        MockedConstruction<SafariDriverService.Builder> safariDriverServiceMockedConstruction = mockConstruction(SafariDriverService.Builder.class, (mock, context) -> {
            when(mock.withLogging(logging)).thenReturn(mock);
        });

        final DriverService.Builder<SafariDriverService, SafariDriverService.Builder> driverServiceBuilder = safari.getDriverServiceBuilder();
        assertEquals(safariDriverServiceMockedConstruction.constructed().getFirst(), driverServiceBuilder);

        safariDriverServiceMockedConstruction.close();
    }

    @Test
    @DisplayName("buildCapabilitiesFrom should build an instance of Safari based on the provided configuration")
    void buildCapabilitiesFrom() {
        MockedConstruction<SafariOptions> safariOptionsMockedConstruction = mockConstruction(SafariOptions.class);

        safari.buildCapabilities();

        final SafariOptions safariOptions = safariOptionsMockedConstruction.constructed().getFirst();
        assertEquals(safariOptions, Reflections.getFieldValue("capabilities", safari));

        safariOptionsMockedConstruction.close();
    }
}
