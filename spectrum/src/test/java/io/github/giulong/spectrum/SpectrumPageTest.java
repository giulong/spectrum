package io.github.giulong.spectrum;

import io.github.giulong.spectrum.interfaces.JsWebElement;
import io.github.giulong.spectrum.interfaces.Secured;
import io.github.giulong.spectrum.internals.page_factory.SpectrumFieldDecorator;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.TestContext;
import io.github.giulong.spectrum.utils.js.JsWebElementListInvocationHandler;
import io.github.giulong.spectrum.utils.js.JsWebElementProxyBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class SpectrumPageTest {

    private MockedStatic<PageFactory> pageFactoryMockedStatic;
    private MockedStatic<JsWebElementListInvocationHandler> jsWebElementListInvocationHandlerMockedStatic;

    @Mock
    private JsWebElementListInvocationHandler.JsWebElementListInvocationHandlerBuilder jsWebElementListInvocationHandlerBuilder;

    @Mock
    private JsWebElementListInvocationHandler jsWebElementListInvocationHandler;

    @Mock
    private Field field;

    @Mock
    private WebElement webElement;

    @Mock
    private List<WebElement> webElementList;

    @Mock
    private JsWebElementProxyBuilder jsWebElementProxyBuilder;

    @Mock
    private static Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Configuration.Drivers.Waits waits;

    @Mock
    private Configuration.Drivers.Waits.AutoWait auto;

    @Mock
    private Duration timeout;

    @Mock
    private Configuration.Application application;

    @Mock
    private WebDriver webDriver;

    @Mock
    private TestContext testContext;

    @Captor
    private ArgumentCaptor<WebElement> webElementArgumentCaptor;

    @Captor
    private ArgumentCaptor<ClassLoader> classLoaderArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<?>[]> classesArgumentCaptor;

    @InjectMocks
    private DummySpectrumPage<?> spectrumPage;

    @BeforeEach
    void beforeEach() {
        pageFactoryMockedStatic = mockStatic(PageFactory.class);
        jsWebElementListInvocationHandlerMockedStatic = mockStatic(JsWebElementListInvocationHandler.class);
    }

    @AfterEach
    void afterEach() {
        pageFactoryMockedStatic.close();
        jsWebElementListInvocationHandlerMockedStatic.close();
    }

    @Test
    @DisplayName("open should get the configured base url and wait for the page to be loaded")
    void open() {
        final String url = "url";
        final String endpoint = "/endpoint";
        Reflections.setField("endpoint", spectrumPage, endpoint);

        when(configuration.getApplication()).thenReturn(application);
        when(application.getBaseUrl()).thenReturn(url);

        assertEquals(spectrumPage, spectrumPage.open());
        verify(webDriver).get(url + endpoint);
    }

    @Test
    @DisplayName("waitForPageLoading should do nothing but return the page instance")
    void waitForPageLoading() {
        assertEquals(spectrumPage, spectrumPage.waitForPageLoading());
    }

    @DisplayName("isLoaded should check if the current page url matches the endpoint")
    @ParameterizedTest(name = "with base url {0}, endpoint {1}, and current url {2} we expect {3}")
    @MethodSource("valuesProvider")
    void isLoaded(final String baseUrl, final String endpoint, final String currentUrl, final boolean expected) {
        Reflections.setField("endpoint", spectrumPage, endpoint);

        //noinspection DataFlowIssue
        when(webDriver.getCurrentUrl()).thenReturn(currentUrl);
        when(configuration.getApplication()).thenReturn(application);
        when(application.getBaseUrl()).thenReturn(baseUrl);

        assertEquals(expected, spectrumPage.isLoaded());
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("baseUrl", "/endpoint", "baseUrl/endpoint", true),
                arguments("baseUrl", "endpoint", "baseUrl/endpoint", true),
                arguments("baseUrl", "/endpoint", "nope", false)
        );
    }

    @Test
    @DisplayName("init should set the endpoint, init the web elements, add the secured web elements, set the js web elements, and return the page instance")
    void init() {
        final WebElement proxy = mock();
        final long seconds = 123L;

        when(jsWebElementProxyBuilder.buildFor(webElementArgumentCaptor.capture())).thenReturn(proxy);
        when(JsWebElementListInvocationHandler.builder()).thenReturn(jsWebElementListInvocationHandlerBuilder);

        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getWaits()).thenReturn(waits);
        when(waits.getAuto()).thenReturn(auto);
        when(auto.getTimeout()).thenReturn(timeout);
        when(timeout.toSeconds()).thenReturn(seconds);

        final MockedConstruction<AjaxElementLocatorFactory> factoryMockedConstruction = mockConstruction(AjaxElementLocatorFactory.class, (mock, context) -> {
            assertEquals(webDriver, context.arguments().getFirst());
            assertEquals((int) seconds, context.arguments().get(1));
        });

        final MockedConstruction<SpectrumFieldDecorator> decoratorMockedConstruction = mockConstruction(SpectrumFieldDecorator.class);

        assertEquals(spectrumPage, spectrumPage.init());

        final SpectrumFieldDecorator decorator = decoratorMockedConstruction.constructed().getFirst();

        verifyNoMoreInteractions(testContext);
        pageFactoryMockedStatic.verify(() -> PageFactory.initElements(decorator, spectrumPage));

        assertEquals(proxy, Reflections.getFieldValue("jsWebElement", spectrumPage));

        factoryMockedConstruction.close();
        decoratorMockedConstruction.close();
    }

    @Test
    @DisplayName("injectJsWebElementProxyInto should set a jsWebElementProxy instance on each webElement field")
    void injectJsWebElementProxyInto() throws IllegalAccessException {
        final WebElement proxy = mock();

        when(field.get(spectrumPage)).thenReturn(webElement);
        when(jsWebElementProxyBuilder.buildFor(webElement)).thenReturn(proxy);

        spectrumPage.injectJsWebElementProxyInto(field);

        verify(field).set(spectrumPage, proxy);
    }

    @Test
    @DisplayName("setJsWebElementProxy should set a JsWebElementListInvocationHandler instance on each List field annotated with @JsWebElement")
    void injectJsWebElementProxyIntoList() throws IllegalAccessException {
        final MockedStatic<Proxy> proxyMockedStatic = mockStatic(Proxy.class);
        final WebElement proxy = mock();

        when(field.get(spectrumPage)).thenReturn(webElementList);
        when(JsWebElementListInvocationHandler.builder()).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.jsWebElementProxyBuilder(jsWebElementProxyBuilder)).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.webElements(webElementList)).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.build()).thenReturn(jsWebElementListInvocationHandler);

        when(Proxy.newProxyInstance(classLoaderArgumentCaptor.capture(), classesArgumentCaptor.capture(), eq(jsWebElementListInvocationHandler))).thenReturn(proxy);

        spectrumPage.injectJsWebElementProxyInto(field);

        assertEquals(List.class.getClassLoader(), classLoaderArgumentCaptor.getValue());
        assertArrayEquals(new Class<?>[]{List.class}, classesArgumentCaptor.getValue());

        verify(field).set(spectrumPage, proxy);

        proxyMockedStatic.close();
    }

    private static class DummySpectrumPage<T> extends SpectrumPage<DummySpectrumPage<T>, T> {

        @SuppressWarnings("unused")
        private WebElement webElement;

        @SuppressWarnings("unused")
        @JsWebElement
        private WebElement jsWebElement;

        @Secured
        @SuppressWarnings("unused")
        private WebElement securedWebElement;

        DummySpectrumPage() {
            configuration = SpectrumPageTest.configuration;
        }
    }
}
