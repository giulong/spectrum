package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.environments.LocalEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.bidi.module.BrowsingContextInspector;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;

import static io.github.giulong.spectrum.enums.Result.DISABLED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.BrowsingContextInspectorResolver.BROWSING_CONTEXT_INSPECTOR;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.LogInspectorResolver.LOG_INSPECTOR;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.NetworkResolver.NETWORK;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class DriverConsumerTest {

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private BrowsingContextInspector browsingContextInspector;

    @Mock
    private LogInspector logInspector;

    @Mock
    private Network network;

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
    @DisplayName("accept should shutdown the driver and the environment and close BiDi objects when the driver supports bidi")
    void accept() {
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.isKeepOpen()).thenReturn(false);
        doReturn(driver).when(runtime).getDriver();
        doReturn(environment).when(runtime).getEnvironment();

        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(BROWSING_CONTEXT_INSPECTOR, BrowsingContextInspector.class)).thenReturn(browsingContextInspector);
        when(store.get(LOG_INSPECTOR, LogInspector.class)).thenReturn(logInspector);
        when(store.get(NETWORK, Network.class)).thenReturn(network);

        driverConsumer.accept(event);

        verify(driver).shutdown();
        verify(environment).shutdown();
        verify(browsingContextInspector).close();
        verify(logInspector).close();
        verify(network).close();
    }

    @Test
    @DisplayName("accept should shutdown the driver and the environment but avoid closing BiDi objects when the driver doesn't support bidi")
    void acceptNotBiDi() {
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.isKeepOpen()).thenReturn(false);
        doReturn(driver).when(runtime).getDriver();
        doReturn(environment).when(runtime).getEnvironment();

        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(BROWSING_CONTEXT_INSPECTOR, BrowsingContextInspector.class)).thenReturn(null);
        when(store.get(LOG_INSPECTOR, LogInspector.class)).thenReturn(null);
        when(store.get(NETWORK, Network.class)).thenReturn(null);

        driverConsumer.accept(event);

        verify(driver).shutdown();
        verify(environment).shutdown();
        verify(browsingContextInspector, never()).close();
        verify(logInspector, never()).close();
        verify(network, never()).close();
    }

    @Test
    @DisplayName("accept should not shutdown the driver if drivers.keepOpen is true")
    void acceptKeepOpen() {
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.isKeepOpen()).thenReturn(true);
        doReturn(environment).when(runtime).getEnvironment();

        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(BROWSING_CONTEXT_INSPECTOR, BrowsingContextInspector.class)).thenReturn(browsingContextInspector);
        when(store.get(LOG_INSPECTOR, LogInspector.class)).thenReturn(logInspector);
        when(store.get(NETWORK, Network.class)).thenReturn(network);

        driverConsumer.accept(event);

        verify(driver, never()).shutdown();
        verify(environment).shutdown();
        verify(browsingContextInspector).close();
        verify(logInspector).close();
        verify(network).close();
    }
}
