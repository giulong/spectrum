package com.github.giulong.spectrum.extensions.resolvers;

import com.github.giulong.spectrum.internals.EventsListener;
import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.browsers.Browser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.openqa.selenium.support.events.WebDriverListener;

import static com.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebDriverResolver")
class WebDriverResolverTest {

    private static MockedStatic<EventsListener> eventsListenerMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Browser<?, ?, ?> browser;

    @Mock
    private WebDriver webDriver;

    @Mock
    private WebDriver decoratedWebDriver;

    @Mock
    private Configuration.Events events;

    @Mock
    private EventsListener.EventsListenerBuilder eventsListenerBuilder;

    @Mock
    private EventsListener eventsListener;

    @InjectMocks
    private WebDriverResolver webDriverResolver;

    @BeforeEach
    public void beforeEach() {
        eventsListenerMockedStatic = mockStatic(EventsListener.class);
    }

    @AfterEach
    public void afterEach() {
        eventsListenerMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the instance of the webdriver decorated with the default event listener")
    public void resolveParameterEventListener() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(browser).when(runtime).getBrowser();
        when(browser.build(configuration)).thenReturn(webDriver);
        when(configuration.getEvents()).thenReturn(events);

        when(EventsListener.builder()).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.store(store)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.events(events)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.build()).thenReturn(eventsListener);

        //noinspection rawtypes
        MockedConstruction<EventFiringDecorator> mockedConstruction = mockConstruction(EventFiringDecorator.class, (mock, context) -> {
            assertEquals(eventsListener, ((WebDriverListener[]) context.arguments().get(0))[0]);

            //noinspection unchecked
            when(mock.decorate(webDriver)).thenReturn(decoratedWebDriver);
        });
        WebDriver actual = webDriverResolver.resolveParameter(parameterContext, extensionContext);
        verify(store).put(WebDriverResolver.WEB_DRIVER, decoratedWebDriver);

        assertEquals(decoratedWebDriver, actual);

        mockedConstruction.close();
    }
}
