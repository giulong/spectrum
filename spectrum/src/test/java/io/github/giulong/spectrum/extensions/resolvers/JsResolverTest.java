package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Js;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.function.Function;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.DRIVER;
import static io.github.giulong.spectrum.extensions.resolvers.JsResolver.JS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JsResolver")
class JsResolverTest {

    private MockedStatic<Js> jsMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private ExtensionContext.Store store;

    @Mock(extraInterfaces = JavascriptExecutor.class)
    private WebDriver webDriver;

    @Mock
    private Js.JsBuilder jsBuilder;

    @Mock
    private Js js;

    @Captor
    private ArgumentCaptor<Function<String, Js>> functionArgumentCaptor;

    @InjectMocks
    private JsResolver jsResolver;

    @BeforeEach
    public void beforeEach() {
        jsMockedStatic = mockStatic(Js.class);
    }

    @AfterEach
    public void afterEach() {
        jsMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return the instance of Js")
    public void resolveParameter() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);

        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(DRIVER, WebDriver.class)).thenReturn(webDriver);
        when(Js.builder()).thenReturn(jsBuilder);
        when(jsBuilder.driver((JavascriptExecutor) webDriver)).thenReturn(jsBuilder);
        when(jsBuilder.build()).thenReturn(js);

        when(rootStore.getOrComputeIfAbsent(eq(JS), functionArgumentCaptor.capture(), eq(Js.class))).thenReturn(js);

        assertEquals(js, jsResolver.resolveParameter(parameterContext, extensionContext));

        Function<String, Js> function = functionArgumentCaptor.getValue();
        final Js actual = function.apply("value");

        assertEquals(js, actual);
    }
}