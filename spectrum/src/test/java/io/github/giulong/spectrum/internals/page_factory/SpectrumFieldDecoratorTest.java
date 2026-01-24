package io.github.giulong.spectrum.internals.page_factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.*;
import java.util.List;

import io.github.giulong.spectrum.interfaces.Secured;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementListHandler;

class SpectrumFieldDecoratorTest {

    @Mock
    private ElementLocatorFactory factory;

    @Mock
    private Field field;

    @Mock
    private ElementLocator locator;

    @Mock
    private Object proxy;

    @Mock
    private List<Object> proxyList;

    @Mock
    private ParameterizedType parameterizedType;

    @Mock
    private FindBy findBy;

    @Captor
    private ArgumentCaptor<InvocationHandler> invocationHandlerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<?>[]> classesArgumentCaptor;

    @InjectMocks
    private SpectrumFieldDecorator spectrumFieldDecorator;

    @Test
    @DisplayName("decorate should return null if the provided field is not a WebElement nor a WebElement list")
    void decorateNull() {
        final ClassLoader classLoader = mock();

        assertNull(spectrumFieldDecorator.decorate(classLoader, field));
    }

    @Test
    @DisplayName("decorate should return null if the locator for the provided field is null")
    void decorateNullLocator() {
        final ClassLoader classLoader = mock();

        when(factory.createLocator(field)).thenReturn(null);

        assertNull(spectrumFieldDecorator.decorate(classLoader, field));
    }

    @Test
    @DisplayName("decorate should proxy the provided field with a SpectrumLocatingElementHandler")
    void decorate() {
        final ClassLoader classLoader = mock();

        when(factory.createLocator(field)).thenReturn(locator);
        doReturn(WebElement.class).when(field).getType();
        when(field.isAnnotationPresent(Secured.class)).thenReturn(true);

        final MockedConstruction<SpectrumLocatingElementHandler> mockedConstruction = mockConstruction((mock, context) -> {
            assertEquals(locator, context.arguments().getFirst());
            assertTrue((boolean) context.arguments().get(1));
        });

        final MockedStatic<Proxy> proxyMockedStatic = mockStatic();
        when(Proxy.newProxyInstance(eq(classLoader), classesArgumentCaptor.capture(), invocationHandlerArgumentCaptor.capture())).thenReturn(proxy);

        assertEquals(proxy, spectrumFieldDecorator.decorate(classLoader, field));

        assertArrayEquals(new Class<?>[]{WebElement.class, WrapsElement.class, Locatable.class}, classesArgumentCaptor.getValue());

        proxyMockedStatic.close();
        mockedConstruction.close();
    }

    @Test
    @DisplayName("decorate should proxy the provided list field with a LocatingElementListHandler")
    void decorateList() {
        final ClassLoader classLoader = mock();

        when(factory.createLocator(field)).thenReturn(locator);
        doReturn(List.class).when(field).getType();
        when(field.getGenericType()).thenReturn(parameterizedType);
        when(parameterizedType.getActualTypeArguments()).thenReturn(new Type[]{WebElement.class});
        when(field.getAnnotation(FindBy.class)).thenReturn(findBy);

        final MockedConstruction<LocatingElementListHandler> mockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(locator, context.arguments().getFirst()));

        final MockedStatic<Proxy> proxyMockedStatic = mockStatic();
        when(Proxy.newProxyInstance(eq(classLoader), classesArgumentCaptor.capture(), invocationHandlerArgumentCaptor.capture())).thenReturn(proxyList);

        assertEquals(proxyList, spectrumFieldDecorator.decorate(classLoader, field));

        assertArrayEquals(new Class<?>[]{List.class}, classesArgumentCaptor.getValue());

        proxyMockedStatic.close();
        mockedConstruction.close();
    }
}
