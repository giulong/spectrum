package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.environments.LocalEnvironment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DriverConsumer")
class DriverConsumerTest {

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Event event;

    @Mock
    private Driver<?, ?, ?> driver;

    @Mock
    private LocalEnvironment environment;

    @InjectMocks
    private DriverConsumer driverConsumer;

    @Test
    @DisplayName("consumes should shutdown the driver")
    public void consumes() {
        when(event.getContext()).thenReturn(context);
        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(driver).when(runtime).getDriver();
        doReturn(environment).when(runtime).getEnvironment();

        driverConsumer.consumes(event);

        verify(driver).shutdown();
        verify(environment).shutdown();
    }
}
