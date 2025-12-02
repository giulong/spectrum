package io.github.giulong.spectrum.utils.events;

import static io.github.giulong.spectrum.enums.Result.*;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.BrowsingContextInspectorResolver.BROWSING_CONTEXT_INSPECTOR;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.LogInspectorResolver.LOG_INSPECTOR;
import static io.github.giulong.spectrum.extensions.resolvers.bidi.NetworkResolver.NETWORK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import io.github.giulong.spectrum.drivers.Driver;
import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.environments.LocalEnvironment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.bidi.module.BrowsingContextInspector;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;

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

    @DisplayName("shouldAccept should check if the test is disabled")
    @ParameterizedTest(name = "with result {0} we expect {1}")
    @MethodSource("valuesProvider")
    void shouldAccept(final Result result, final boolean expected) {
        when(event.getResult()).thenReturn(result);

        assertEquals(expected, driverConsumer.shouldAccept(event));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(NOT_RUN, true),
                arguments(SUCCESSFUL, true),
                arguments(FAILED, true),
                arguments(ABORTED, true),
                arguments(DISABLED, false));
    }

    @Test
    @DisplayName("accept should shutdown the driver and the environment and close BiDi objects when the driver supports bidi")
    void accept() {
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
