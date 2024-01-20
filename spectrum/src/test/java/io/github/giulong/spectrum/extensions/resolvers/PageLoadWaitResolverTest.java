package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.types.PageLoadWait;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PageLoadWaitResolver")
class PageLoadWaitResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private WebDriver webDriver;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.WebDriver webDriverConfiguration;

    @Mock
    private Configuration.WebDriver.Waits waits;

    @Mock
    private Duration duration;

    @InjectMocks
    private PageLoadWaitResolver pageLoadWaitResolver;

    @Test
    @DisplayName("resolveParameter should return an instance of PageLoadWaits on the current stored WebDriver")
    public void testResolveParameter() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(WebDriverResolver.WEB_DRIVER, WebDriver.class)).thenReturn(webDriver);
        when(rootStore.get(ConfigurationResolver.CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getWebDriver()).thenReturn(webDriverConfiguration);
        when(webDriverConfiguration.getWaits()).thenReturn(waits);
        when(waits.getPageLoadTimeout()).thenReturn(duration);

        MockedConstruction<PageLoadWait> mockedConstruction = mockConstruction(PageLoadWait.class, (mock, context) -> {
            assertEquals(webDriver, context.arguments().getFirst());
            assertEquals(duration, context.arguments().get(1));
        });

        PageLoadWait actual = pageLoadWaitResolver.resolveParameter(parameterContext, extensionContext);
        PageLoadWait pageLoadWait = mockedConstruction.constructed().getFirst();
        verify(store).put(PageLoadWaitResolver.PAGE_LOAD_WAIT, pageLoadWait);
        assertEquals(pageLoadWait, actual);

        mockedConstruction.close();
    }
}
