package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.environments.LocalEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static io.github.giulong.spectrum.enums.Result.DISABLED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static org.mockito.Mockito.*;

class DriverConsumerTest {

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Event event;

    @Mock
    private Driver<?, ?, ?> driver;

    @Mock
    private LocalEnvironment environment;

    @InjectMocks
    private DriverConsumer driverConsumer;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("configuration", driverConsumer, configuration);
    }

    @Test
    @DisplayName("accept should do nothing when the test is skipped")
    void acceptSkipped() {
        when(event.getResult()).thenReturn(DISABLED);

        driverConsumer.accept(event);

        verifyNoInteractions(driver);
        verifyNoInteractions(environment);
    }

    @Test
    @DisplayName("accept should shutdown the driver and the environment")
    void accept() {
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.isKeepOpen()).thenReturn(false);
        doReturn(driver).when(runtime).getDriver();
        doReturn(environment).when(runtime).getEnvironment();

        driverConsumer.accept(event);

        verify(driver).shutdown();
        verify(environment).shutdown();
    }

    @Test
    @DisplayName("accept should not shutdown the driver if drivers.keepOpen is true")
    void acceptKeepOpen() {
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.isKeepOpen()).thenReturn(true);
        doReturn(environment).when(runtime).getEnvironment();

        driverConsumer.accept(event);

        verify(driver, never()).shutdown();
        verify(environment).shutdown();
    }
}
