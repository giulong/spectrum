package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.js.Js;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.JsResolver.JS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class JsResolverTest {

    private MockedStatic<Js> jsMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock(extraInterfaces = JavascriptExecutor.class)
    private WebDriver webDriver;

    @Mock
    private Js.JsBuilder jsBuilder;

    @Mock
    private Js js;

    @InjectMocks
    private JsResolver jsResolver;

    @BeforeEach
    void beforeEach() {
        jsMockedStatic = mockStatic(Js.class);
    }

    @AfterEach
    void afterEach() {
        jsMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the instance of Js")
    void resolveParameter() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(DRIVER, WebDriver.class)).thenReturn(webDriver);
        when(Js.builder()).thenReturn(jsBuilder);
        when(jsBuilder.driver((JavascriptExecutor) webDriver)).thenReturn(jsBuilder);
        when(jsBuilder.build()).thenReturn(js);

        assertEquals(js, jsResolver.resolveParameter(parameterContext, extensionContext));

        verify(store).put(JS, js);
    }
}
