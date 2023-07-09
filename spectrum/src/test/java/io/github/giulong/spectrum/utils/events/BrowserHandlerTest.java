package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.browsers.Browser;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.events.Event;
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
@DisplayName("BrowserHandler")
class BrowserHandlerTest {

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
    private Browser<?, ?, ?> browser;

    @InjectMocks
    private BrowserHandler browserHandler;

    @Test
    @DisplayName("handle should shutdown the browser")
    public void handle() {
        when(event.getContext()).thenReturn(context);
        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getRuntime()).thenReturn(runtime);
        doReturn(browser).when(runtime).getBrowser();

        browserHandler.handle(event);

        verify(browser).shutdown();
    }
}
