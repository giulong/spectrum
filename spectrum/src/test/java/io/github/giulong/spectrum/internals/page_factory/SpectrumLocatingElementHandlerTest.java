package io.github.giulong.spectrum.internals.page_factory;

import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

class SpectrumLocatingElementHandlerTest {

    @Mock
    private Object proxy;

    @Mock
    private Object result;

    @Mock
    private WebElement webElement;

    @Mock
    private Method method;

    @Mock
    private ElementLocator elementLocator;

    @Captor
    private ArgumentCaptor<Object[]> argsArgumentCaptor;

    @InjectMocks
    private SpectrumLocatingElementHandler spectrumLocatingElementHandler = new SpectrumLocatingElementHandler(elementLocator, true);

    @DisplayName("constructor should set the secured field")
    @ParameterizedTest(name = "with secured {0}")
    @ValueSource(booleans = {true, false})
    void constructor(final boolean secured) {
        final SpectrumLocatingElementHandler handler = new SpectrumLocatingElementHandler(elementLocator, secured);

        Assertions.assertEquals(secured, Reflections.getFieldValue("secured", handler, Boolean.class));
    }

    @SuppressWarnings({"checkstyle:IllegalThrows"})
    @DisplayName("invoke should call the parent method when the field is not secured or the method is not sendKeys")
    @ParameterizedTest(name = "with secured {0}")
    @ValueSource(booleans = {true, false})
    void invoke(final boolean secured) throws Throwable {
        final CharSequence[] charSequences = new CharSequence[]{"string", "another"};
        final CharSequence[] expectedCharSequences = new CharSequence[]{"string", "another"};
        final Object[] args = new Object[]{charSequences};

        Reflections.setField("secured", spectrumLocatingElementHandler, secured);
        Reflections.setField("locator", spectrumLocatingElementHandler, elementLocator);

        lenient().when(method.getName()).thenReturn("nope");

        when(elementLocator.findElement()).thenReturn(webElement);
        when(method.invoke(eq(webElement), (Object) argsArgumentCaptor.capture())).thenReturn(result);

        assertEquals(result, spectrumLocatingElementHandler.invoke(proxy, method, args));
        assertArrayEquals(args, argsArgumentCaptor.getValue());
        assertArrayEquals(expectedCharSequences, (CharSequence[]) args[0]);
    }

    @SuppressWarnings({"checkstyle:IllegalThrows"})
    @Test
    @DisplayName("invoke should wrap the first charSequence arg when the field is secured and the method is sendKeys")
    void invokeSecured() throws Throwable {
        final CharSequence[] charSequences = new CharSequence[]{"string", "another"};
        final CharSequence[] expectedCharSequences = new CharSequence[]{"@Secured@string@Secured@", "another"};
        final Object[] args = new Object[]{charSequences};

        Reflections.setField("secured", spectrumLocatingElementHandler, true);
        Reflections.setField("locator", spectrumLocatingElementHandler, elementLocator);

        when(method.getName()).thenReturn("sendKeys");

        when(elementLocator.findElement()).thenReturn(webElement);
        when(method.invoke(eq(webElement), (Object) argsArgumentCaptor.capture())).thenReturn(result);

        assertEquals(result, spectrumLocatingElementHandler.invoke(proxy, method, args));
        assertArrayEquals(args, argsArgumentCaptor.getValue());
        assertArrayEquals(expectedCharSequences, (CharSequence[]) args[0]);
    }

    @SuppressWarnings({"checkstyle:IllegalThrows"})
    @Test
    @DisplayName("invoke should immediately invoke the method if it's toString")
    void invokeToString() throws Throwable {
        final CharSequence[] charSequences = new CharSequence[]{"string", "another"};
        final CharSequence[] expectedCharSequences = new CharSequence[]{"string", "another"};
        final Object[] args = new Object[]{charSequences};

        Reflections.setField("locator", spectrumLocatingElementHandler, elementLocator);
        Reflections.setField("elementLocator", spectrumLocatingElementHandler, elementLocator);

        when(method.getName()).thenReturn("toString");

        when(method.invoke(eq(elementLocator), (Object) argsArgumentCaptor.capture())).thenReturn(result);

        assertEquals(result, spectrumLocatingElementHandler.invoke(proxy, method, args));
        assertArrayEquals(args, argsArgumentCaptor.getValue());
        assertArrayEquals(expectedCharSequences, (CharSequence[]) args[0]);
    }
}
