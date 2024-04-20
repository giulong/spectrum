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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JsWebElementInvocationHandler")
class JsWebElementInvocationHandlerTest {

    @Mock
    private Pattern locatorPattern;

    @Mock
    private Matcher matcher;

    @Mock
    private WebElement webElement;

    @Mock
    private Js js;

    @Mock
    private Map<Method, Method> methods;

    @Mock
    private Method method;

    @Mock
    private Object result;

    @Mock
    private Method proxyMethod;

    @Captor
    private ArgumentCaptor<Object[]> argsArgumentCaptor;

    @InjectMocks
    private JsWebElementInvocationHandler jsWebElementInvocationHandler;

    @Test
    @DisplayName("click should click with javascript on the provided webElement and return the Js instance")
    public void click() {
        jsWebElementInvocationHandler.click();

        verify(js).click(webElement);
    }

    @Test
    @DisplayName("invoke should find the wanted method with the same signature and call that instead of the original")
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        final String fullWebElement = "[[ChromeDriver: chrome on WINDOWS (5db9fd1ca57389187f02aa09397ea93c)] -> id: message]";
        final String expected = "id: message";
        final String arg = "arg";
        final String methodName = "methodName";

        when(webElement.toString()).thenReturn(fullWebElement);
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(matcher.find()).thenReturn(true);
        when(matcher.group(1)).thenReturn(expected);

        when(method.getName()).thenReturn(methodName);
        when(methods.get(method)).thenReturn(proxyMethod);
        when(proxyMethod.invoke(eq(jsWebElementInvocationHandler), argsArgumentCaptor.capture())).thenReturn(result);

        assertEquals(result, jsWebElementInvocationHandler.invoke(null, method, new Object[]{arg}));
        assertArrayEquals(new Object[]{arg}, argsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("invoke should find the wanted method with the same signature and call that instead of the original")
    public void invokeNoLocatorMatch() throws InvocationTargetException, IllegalAccessException {
        final String fullWebElement = "[[not matching]";
        final String arg = "arg";
        final String methodName = "methodName";

        when(webElement.toString()).thenReturn(fullWebElement);
        when(locatorPattern.matcher(fullWebElement)).thenReturn(matcher);
        when(matcher.find()).thenReturn(false);

        when(method.getName()).thenReturn(methodName);
        when(methods.get(method)).thenReturn(proxyMethod);
        when(proxyMethod.invoke(eq(jsWebElementInvocationHandler), argsArgumentCaptor.capture())).thenReturn(result);

        assertEquals(result, jsWebElementInvocationHandler.invoke(null, method, new Object[]{arg}));
        assertArrayEquals(new Object[]{arg}, argsArgumentCaptor.getValue());
    }
}