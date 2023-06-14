package com.github.giulong.spectrum.utils.events;

import com.github.giulong.spectrum.pojos.events.Event;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

import static com.github.giulong.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebDriverHandler")
class WebDriverHandlerTest {

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private WebDriver webDriver;

    @Mock
    private Event event;

    @InjectMocks
    private WebDriverHandler webDriverHandler;

    @Test
    @DisplayName("handle should just quit the webDriver")
    public void handle() {
        when(event.getContext()).thenReturn(context);
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(WEB_DRIVER, WebDriver.class)).thenReturn(webDriver);

        webDriverHandler.handle(event);

        verify(webDriver).quit();
    }
}