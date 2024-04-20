package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebElement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JsWebElementListInvocationHandler")
class JsWebElementListInvocationHandlerTest {

    @Mock
    private Method method;

    @Mock
    private Object result;

    @Mock
    private WebElement webElement;

    @Mock
    private WebElement webElementProxy;

    @Mock
    private List<WebElement> webElements;

    @Mock
    private JsWebElementProxyBuilder jsWebElementProxyBuilder;

    @Captor
    private ArgumentCaptor<Object[]> argsArgumentCaptor;

    @InjectMocks
    private JsWebElementListInvocationHandler jsWebElementListInvocationHandler;

    @Test
    @DisplayName("invoke should invoke the original method and return the original result if it's not a webElement")
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        final String arg = "arg";

        when(method.invoke(eq(webElements), argsArgumentCaptor.capture())).thenReturn(result);

        assertEquals(result, jsWebElementListInvocationHandler.invoke(null, method, new Object[]{arg}));
        assertArrayEquals(new Object[]{arg}, argsArgumentCaptor.getValue());

        verifyNoInteractions(jsWebElementProxyBuilder);
    }

    @Test
    @DisplayName("invoke should invoke the original method and return a proxy if the result is a webElement")
    public void invokeWebElement() throws InvocationTargetException, IllegalAccessException {
        final String arg = "arg";

        when(method.invoke(eq(webElements), argsArgumentCaptor.capture())).thenReturn(webElement);
        when(jsWebElementProxyBuilder.buildFor(webElement)).thenReturn(webElementProxy);

        assertEquals(webElementProxy, jsWebElementListInvocationHandler.invoke(null, method, new Object[]{arg}));
    }
}