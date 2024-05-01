package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JsWebElementProxyBuilderTest {

    private MockedStatic<Proxy> proxyMockedStatic;
    private MockedStatic<JsWebElementInvocationHandler> jsWebElementInvocationHandlerMockedStatic;

    @Mock
    private JsWebElementInvocationHandler.JsWebElementInvocationHandlerBuilder jsWebElementInvocationHandlerBuilder;

    @Mock
    private JsWebElementInvocationHandler jsWebElementInvocationHandler;

    @Mock
    private Js js;

    @Mock
    private Pattern locatorPattern;

    @Mock
    private Map<Method, Method> methods;

    @Mock
    private WebElement webElement;

    @Mock
    private WebElement proxy;

    @Captor
    private ArgumentCaptor<ClassLoader> classLoaderArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<?>[]> classesArgumentCaptor;

    @InjectMocks
    private JsWebElementProxyBuilder jsWebElementProxyBuilder;

    @BeforeEach
    public void beforeEach() {
        proxyMockedStatic = mockStatic(Proxy.class);
        jsWebElementInvocationHandlerMockedStatic = mockStatic(JsWebElementInvocationHandler.class);
    }

    @AfterEach
    public void afterEach() {
        proxyMockedStatic.close();
        jsWebElementInvocationHandlerMockedStatic.close();
    }

    @Test
    @DisplayName("buildFor should return a proxy for the provided webElement")
    public void buildFor() {
        when(JsWebElementInvocationHandler.builder()).thenReturn(jsWebElementInvocationHandlerBuilder);
        when(jsWebElementInvocationHandlerBuilder.js(js)).thenReturn(jsWebElementInvocationHandlerBuilder);
        when(jsWebElementInvocationHandlerBuilder.webElement(webElement)).thenReturn(jsWebElementInvocationHandlerBuilder);
        when(jsWebElementInvocationHandlerBuilder.locatorPattern(locatorPattern)).thenReturn(jsWebElementInvocationHandlerBuilder);
        when(jsWebElementInvocationHandlerBuilder.methods(methods)).thenReturn(jsWebElementInvocationHandlerBuilder);
        when(jsWebElementInvocationHandlerBuilder.build()).thenReturn(jsWebElementInvocationHandler);

        when(Proxy.newProxyInstance(classLoaderArgumentCaptor.capture(), classesArgumentCaptor.capture(), eq(jsWebElementInvocationHandler))).thenReturn(proxy);

        assertEquals(proxy, jsWebElementProxyBuilder.buildFor(webElement));
        assertEquals(WebElement.class.getClassLoader(), classLoaderArgumentCaptor.getValue());
        assertArrayEquals(new Class<?>[]{WebElement.class}, classesArgumentCaptor.getValue());
    }
}