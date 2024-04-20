package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import io.github.giulong.spectrum.types.*;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpectrumTestTest<T> {

    private MockedStatic<JsWebElementListInvocationHandler> jsWebElementListInvocationHandlerMockedStatic;

    @Mock
    private JsWebElementListInvocationHandler.JsWebElementListInvocationHandlerBuilder jsWebElementListInvocationHandlerBuilder;

    @Mock
    private JsWebElementListInvocationHandler jsWebElementListInvocationHandler;

    @Mock
    private WebDriver webDriver;

    @Mock
    private T data;

    @Mock
    private ImplicitWait implicitWait;

    @Mock
    private PageLoadWait pageLoadWait;

    @Mock
    private ScriptWait scriptWait;

    @Mock
    private DownloadWait downloadWait;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private Actions actions;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @Mock
    private Configuration configuration;

    @Mock
    private ExtentReports extentReports;

    @Mock
    private TestData testData;

    @Mock
    private Js js;

    @Mock
    private JsWebElementProxyBuilder jsWebElementProxyBuilder;

    @Mock
    private Field field;

    @Mock
    private WebElement webElement;

    @Mock
    private List<WebElement> webElementList;

    @Mock
    private WebElement webElementProxy;

    @Mock
    private WebElement proxy;

    @Captor
    private ArgumentCaptor<WebElement> webElementArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<WebElement>> webElementListArgumentCaptor;

    @Captor
    private ArgumentCaptor<ClassLoader> classLoaderArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<?>[]> classesArgumentCaptor;

    @InjectMocks
    private FakeSpectrumTest<T> spectrumTest;

    @InjectMocks
    private FakeChild<T> childTest;

    @BeforeEach
    public void beforeEach() {
        spectrumTest.data = data;
        spectrumTest.testPage.jsWebElement = webElement;
        spectrumTest.testPage.jsWebElementList = webElementList;
        jsWebElementListInvocationHandlerMockedStatic = mockStatic(JsWebElementListInvocationHandler.class);
    }

    @AfterEach
    public void afterEach() {
        jsWebElementListInvocationHandlerMockedStatic.close();
    }

    @Test
    @DisplayName("beforeEach should set all the provided args resolved via JUnit, and call initPages")
    public void testBeforeEach() {
        childTest.beforeEach(configuration, testData, extentTest, webDriver, implicitWait, pageLoadWait, scriptWait, downloadWait,
                extentReports, actions, eventsDispatcher, js, jsWebElementProxyBuilder, data);

        assertEquals(configuration, spectrumTest.configuration);
        assertEquals(webDriver, spectrumTest.driver);
        assertEquals(implicitWait, spectrumTest.implicitWait);
        assertEquals(pageLoadWait, spectrumTest.pageLoadWait);
        assertEquals(scriptWait, spectrumTest.scriptWait);
        assertEquals(downloadWait, spectrumTest.downloadWait);
        assertEquals(extentReports, spectrumTest.extentReports);
        assertEquals(extentTest, spectrumTest.extentTest);
        assertEquals(actions, spectrumTest.actions);
        assertEquals(eventsDispatcher, spectrumTest.eventsDispatcher);
        assertEquals(testData, spectrumTest.testData);
        assertEquals(jsWebElementProxyBuilder, Reflections.getFieldValue("jsWebElementProxyBuilder", spectrumTest));
        assertEquals(data, spectrumTest.data);

        // initPages
        assertNull(childTest.toSkip);
        assertNotNull(childTest.childTestPage);
        assertThat(childTest.childTestPage, instanceOf(FakeSpectrumPage.class));

        assertNull(childTest.getParentToSkip());
        assertNotNull(childTest.getParentTestPage());
        assertThat(childTest.getParentTestPage(), instanceOf(FakeSpectrumPage.class));
    }

    @Test
    @DisplayName("initPages should init also init pages from super classes")
    public void initPages() {
        childTest.initPages();

        assertNull(childTest.toSkip);
        assertNotNull(childTest.childTestPage);
        assertThat(childTest.childTestPage, instanceOf(FakeSpectrumPage.class));

        assertNull(childTest.getParentToSkip());
        assertNotNull(childTest.getParentTestPage());
        assertThat(childTest.getParentTestPage(), instanceOf(FakeSpectrumPage.class));
    }

    @Test
    @DisplayName("initPage should init the provided field")
    public void testInitPage() {
        final SpectrumPage<?, T> actual = spectrumTest.initPage(Reflections.getField("testPage", spectrumTest), spectrumTest.getSharedFields());

        assertEquals(spectrumTest.testPage, actual);
        assertThat(spectrumTest.testPage, instanceOf(FakeSpectrumPage.class));

        assertEquals("blah", spectrumTest.testPage.getEndpoint());

        assertEquals(webDriver, spectrumTest.testPage.driver);
        assertEquals(implicitWait, spectrumTest.testPage.implicitWait);
        assertEquals(pageLoadWait, spectrumTest.testPage.pageLoadWait);
        assertEquals(scriptWait, spectrumTest.testPage.scriptWait);
        assertEquals(downloadWait, spectrumTest.testPage.downloadWait);
        assertEquals(extentTest, spectrumTest.testPage.extentTest);
        assertEquals(actions, spectrumTest.testPage.actions);
        assertEquals(data, spectrumTest.testPage.data);
    }

    @Test
    @DisplayName("initPage without endpoint")
    public void initPageWithoutEndpoint() {
        final SpectrumPage<?, T> actual = spectrumTest.initPage(Reflections.getField("testPageWithoutEndpoint", spectrumTest), spectrumTest.getSharedFields());

        assertEquals(spectrumTest.testPageWithoutEndpoint, actual);
        assertThat(spectrumTest.testPageWithoutEndpoint, instanceOf(FakeSpectrumPageWithoutEndpoint.class));

        assertEquals("", spectrumTest.testPageWithoutEndpoint.getEndpoint());

        assertEquals(webDriver, spectrumTest.testPageWithoutEndpoint.driver);
        assertEquals(implicitWait, spectrumTest.testPageWithoutEndpoint.implicitWait);
        assertEquals(pageLoadWait, spectrumTest.testPageWithoutEndpoint.pageLoadWait);
        assertEquals(scriptWait, spectrumTest.testPageWithoutEndpoint.scriptWait);
        assertEquals(downloadWait, spectrumTest.testPageWithoutEndpoint.downloadWait);
        assertEquals(extentTest, spectrumTest.testPageWithoutEndpoint.extentTest);
        assertEquals(actions, spectrumTest.testPageWithoutEndpoint.actions);
        assertEquals(data, spectrumTest.testPageWithoutEndpoint.data);
    }

    @Test
    @DisplayName("initJsWebElements should call the setJsWebElementProxy on each field of the current page annotated with @JsWebElement")
    public void initJsWebElements() {
        when(jsWebElementProxyBuilder.buildFor(webElementArgumentCaptor.capture())).thenReturn(webElementProxy);
        when(JsWebElementListInvocationHandler.builder()).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.jsWebElementProxyBuilder(jsWebElementProxyBuilder)).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.webElements(webElementListArgumentCaptor.capture())).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.build()).thenReturn(jsWebElementListInvocationHandler);

        spectrumTest.initJsWebElements(spectrumTest.testPage);

        assertEquals(webElementProxy, Reflections.getFieldValue("jsWebElement", spectrumTest.testPage));
        assertEquals(webElement, webElementArgumentCaptor.getValue());
        assertEquals(webElementList, webElementListArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("setJsWebElementProxy should set a jsWebElementProxy instance on each webElement field")
    public void setJsWebElementProxy() throws IllegalAccessException {
        when(field.get(spectrumTest.testPage)).thenReturn(webElement);
        when(jsWebElementProxyBuilder.buildFor(webElement)).thenReturn(webElementProxy);

        spectrumTest.setJsWebElementProxy(field, spectrumTest.testPage);

        verify(field).set(spectrumTest.testPage, webElementProxy);
    }

    @Test
    @DisplayName("setJsWebElementProxy should set a JsWebElementListInvocationHandler instance on each List field annotated with @JsWebElement")
    public void setJsWebElementProxyList() throws IllegalAccessException {
        final MockedStatic<Proxy> proxyMockedStatic = mockStatic(Proxy.class);

        when(field.get(spectrumTest.testPage)).thenReturn(webElementList);
        when(JsWebElementListInvocationHandler.builder()).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.jsWebElementProxyBuilder(jsWebElementProxyBuilder)).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.webElements(webElementList)).thenReturn(jsWebElementListInvocationHandlerBuilder);
        when(jsWebElementListInvocationHandlerBuilder.build()).thenReturn(jsWebElementListInvocationHandler);

        when(Proxy.newProxyInstance(classLoaderArgumentCaptor.capture(), classesArgumentCaptor.capture(), eq(jsWebElementListInvocationHandler))).thenReturn(proxy);

        spectrumTest.setJsWebElementProxy(field, spectrumTest.testPage);

        assertEquals(List.class.getClassLoader(), classLoaderArgumentCaptor.getValue());
        assertArrayEquals(new Class<?>[]{List.class}, classesArgumentCaptor.getValue());

        verify(field).set(spectrumTest.testPage, proxy);

        proxyMockedStatic.close();
    }

    @SuppressWarnings("unused")
    static class FakeSpectrumTest<T> extends SpectrumTest<T> {
        private String toSkip;
        private final FakeSpectrumPage<T> testPage = new FakeSpectrumPage<>();
        private FakeSpectrumPageWithoutEndpoint<T> testPageWithoutEndpoint;
    }

    @SuppressWarnings("unused")
    static class FakeChild<T> extends FakeParentSpectrumTest<T> {
        private String toSkip;
        private FakeSpectrumPage<T> childTestPage;
    }

    @Getter
    @SuppressWarnings("unused")
    static class FakeParentSpectrumTest<T> extends SpectrumTest<T> {
        private String parentToSkip;
        private FakeSpectrumPage<T> parentTestPage;
    }

    @Endpoint("blah")
    @SuppressWarnings("unused")
    static class FakeSpectrumPage<T> extends SpectrumPage<FakeSpectrumPage<T>, T> {

        @JsWebElement
        private WebElement jsWebElement;

        @JsWebElement
        private List<WebElement> jsWebElementList;
    }

    static class FakeSpectrumPageWithoutEndpoint<T> extends SpectrumPage<FakeSpectrumPageWithoutEndpoint<T>, T> {
    }
}
