package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.browsers.Browser;
import com.giuliolongfils.spectrum.internals.EventsListener;
import com.giuliolongfils.spectrum.pojos.Configuration;
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

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
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
    private ExtensionContext.Store store;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Browser<?> browser;

    @Mock
    private WebDriver webDriver;

    @Mock
    private WebDriver decoratedWebDriver;

    @Mock
    private Configuration.WebDriver webDriverConfiguration;

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
    @DisplayName("resolveParameter should return the instance of the webdriver based on the runtime parameter")
    public void resolveParameter() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(browser).when(runtime).getBrowser();
        when(browser.build(configuration)).thenReturn(webDriver);
        when(configuration.getWebDriver()).thenReturn(webDriverConfiguration);
        when(webDriverConfiguration.isDefaultEventListenerEnabled()).thenReturn(false);

        WebDriver actual = webDriverResolver.resolveParameter(parameterContext, extensionContext);
        verify(store).put(WEB_DRIVER, webDriver);

        assertEquals(webDriver, actual);
    }

    @Test
    @DisplayName("resolveParameter should return the instance of the webdriver decorated with the default event listener")
    public void resolveParameterEventListener() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(browser).when(runtime).getBrowser();
        when(browser.build(configuration)).thenReturn(webDriver);
        when(configuration.getWebDriver()).thenReturn(webDriverConfiguration);
        when(webDriverConfiguration.isDefaultEventListenerEnabled()).thenReturn(true);
        when(configuration.getEvents()).thenReturn(events);

        when(EventsListener.builder()).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.store(store)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.events(events)).thenReturn(eventsListenerBuilder);
        when(eventsListenerBuilder.build()).thenReturn(eventsListener);

        try (MockedConstruction<EventFiringDecorator> ignored = mockConstruction(EventFiringDecorator.class, (mock, context) -> {
            assertEquals(eventsListener, ((WebDriverListener[]) context.arguments().get(0))[0]);

            when(mock.decorate(webDriver)).thenReturn(decoratedWebDriver);
        })) {
            WebDriver actual = webDriverResolver.resolveParameter(parameterContext, extensionContext);
            verify(store).put(WEB_DRIVER, decoratedWebDriver);

            assertEquals(decoratedWebDriver, actual);
        }
    }
}