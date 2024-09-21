package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.types.*;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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

class SpectrumTestTest {

    private MockedStatic<JsWebElementListInvocationHandler> jsWebElementListInvocationHandlerMockedStatic;

    @Mock
    private JsWebElementListInvocationHandler.JsWebElementListInvocationHandlerBuilder jsWebElementListInvocationHandlerBuilder;

    @Mock
    private JsWebElementListInvocationHandler jsWebElementListInvocationHandler;

    @Mock
    private TestContext testContext;

    @Mock
    private WebDriver webDriver;

    @Mock
    private FakeData data;

    @Mock
    private Configuration.Data dataConfiguration;

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
    private StatefulExtentTest statefulExtentTest;

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

    @Mock
    private YamlUtils yamlUtils;

    @Captor
    private ArgumentCaptor<WebElement> webElementArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<WebElement>> webElementListArgumentCaptor;

    @Captor
    private ArgumentCaptor<ClassLoader> classLoaderArgumentCaptor;

    @Captor
    private ArgumentCaptor<Class<?>[]> classesArgumentCaptor;

    @InjectMocks
    private FakeSpectrumTest<FakeData> spectrumTest;

    @InjectMocks
    private FakeChild<FakeData> childTest;

    @InjectMocks
    private FakeChild<Void> childTestVoid;

    @InjectMocks
    private FakeParentSpectrumTestVoid fakeParentSpectrumTestVoid;

    @BeforeEach
    public void beforeEach() {
        spectrumTest.data = data;
        spectrumTest.testPage.jsWebElement = webElement;
        spectrumTest.testPage.jsWebElementList = webElementList;
        jsWebElementListInvocationHandlerMockedStatic = mockStatic(JsWebElementListInvocationHandler.class);

        Reflections.setField("yamlUtils", spectrumTest, yamlUtils);
        Reflections.setField("yamlUtils", childTest, yamlUtils);
        Reflections.setField("yamlUtils", childTestVoid, yamlUtils);
        Reflections.setField("yamlUtils", fakeParentSpectrumTestVoid, yamlUtils);
    }

    @AfterEach
    public void afterEach() {
        jsWebElementListInvocationHandlerMockedStatic.close();
    }

    @Test
    @DisplayName("beforeEach should set all the provided args resolved via JUnit, and call initPages")
    public void testBeforeEach() {
        childTest.beforeEach(testContext, configuration, testData, statefulExtentTest, webDriver, implicitWait, pageLoadWait, scriptWait, downloadWait,
                extentReports, actions, eventsDispatcher, js, jsWebElementProxyBuilder, data);

        verifyNoInteractions(testContext);

        assertEquals(configuration, spectrumTest.configuration);
        assertEquals(webDriver, spectrumTest.driver);
        assertEquals(implicitWait, spectrumTest.implicitWait);
        assertEquals(pageLoadWait, spectrumTest.pageLoadWait);
        assertEquals(scriptWait, spectrumTest.scriptWait);
        assertEquals(downloadWait, spectrumTest.downloadWait);
        assertEquals(extentReports, spectrumTest.extentReports);
        assertEquals(statefulExtentTest, spectrumTest.statefulExtentTest);
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
        final String folder = "folder";

        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFolder()).thenReturn(folder);
        when(yamlUtils.read(folder + "/data.yaml", FakeData.class)).thenReturn(data);

        childTest.initPages();

        assertNull(childTest.toSkip);
        assertNotNull(childTest.childTestPage);
        assertThat(childTest.childTestPage, instanceOf(FakeSpectrumPage.class));
        assertEquals(data, childTest.childTestPage.data);

        assertNull(childTest.getParentToSkip());
        assertNotNull(childTest.getParentTestPage());
        assertThat(childTest.getParentTestPage(), instanceOf(FakeSpectrumPage.class));
        assertEquals(data, childTest.getParentTestPage().data);
    }

    @Test
    @DisplayName("initPages should init also init pages from super classes, injecting data field in pages")
    public void initPagesVoid() {
        final String folder = "folder";

        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFolder()).thenReturn(folder);
        when(yamlUtils.read(folder + "/data.yaml", FakeData.class)).thenReturn(data);

        childTestVoid.initPages();

        assertNull(childTestVoid.toSkip);
        assertNotNull(childTestVoid.childTestPage);
        assertThat(childTestVoid.childTestPage, instanceOf(FakeSpectrumPage.class));
        assertEquals(data, childTestVoid.childTestPage.data);

        assertNull(childTestVoid.getParentToSkip());
        assertNotNull(childTestVoid.getParentTestPage());
        assertThat(childTestVoid.getParentTestPage(), instanceOf(FakeSpectrumPage.class));
        assertEquals(data, childTestVoid.getParentTestPage().data);
    }

    @Test
    @DisplayName("initPage should init the provided field")
    public void testInitPage() {
        final SpectrumPage<?, FakeData> actual = spectrumTest.initPage(Reflections.getField("testPage", spectrumTest), spectrumTest.getSharedFields());

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
        final SpectrumPage<?, FakeData> actual = spectrumTest.initPage(Reflections.getField("testPageWithoutEndpoint", spectrumTest), spectrumTest.getSharedFields());

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

    @Test
    @DisplayName("injectDataInPages should do nothing if data was already injected from SpectrumTest")
    public void injectDataInPagesNotNull() {
        spectrumTest.injectDataInPages();

        // we assert it's null since we have SpectrumTest<FakeData>
        assertNull(spectrumTest.testPage.data);
    }

    @Test
    @DisplayName("injectDataInPages should inject the data field in pages when we have SpectrumTest<Void>")
    public void injectDataInPages() {
        final String folder = "folder";

        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFolder()).thenReturn(folder);
        when(yamlUtils.read(folder + "/data.yaml", FakeData.class)).thenReturn(data);

        final FakeSpectrumPage fakeSpectrumPage = mock(FakeSpectrumPage.class);
        final FakeSpectrumPageVoid fakeSpectrumPageVoid = mock(FakeSpectrumPageVoid.class);
        childTestVoid.spectrumPages = List.of(fakeSpectrumPage, fakeSpectrumPageVoid);

        childTestVoid.injectDataInPages();

        assertEquals(data, fakeSpectrumPage.data);
        assertNull(fakeSpectrumPageVoid.data);
    }

    @Test
    @DisplayName("injectDataInPages should not inject the data field in pages when we have SpectrumTest<Void>, if all pages are SpectrumPage<Void>")
    public void injectDataInPagesAllVoid() {
        final FakeSpectrumPageVoid fakeSpectrumPageVoid1 = mock(FakeSpectrumPageVoid.class);
        final FakeSpectrumPageVoid fakeSpectrumPageVoid2 = mock(FakeSpectrumPageVoid.class);
        fakeParentSpectrumTestVoid.spectrumPages = List.of(fakeSpectrumPageVoid1, fakeSpectrumPageVoid2);

        fakeParentSpectrumTestVoid.injectDataInPages();

        assertNull(fakeSpectrumPageVoid1.data);
        assertNull(fakeSpectrumPageVoid2.data);
    }

    @SuppressWarnings("unused")
    static class FakeSpectrumTest<T> extends SpectrumTest<T> {
        private String toSkip;
        private final FakeSpectrumPage testPage = new FakeSpectrumPage();
        private FakeSpectrumPageWithoutEndpoint<T> testPageWithoutEndpoint;
    }

    @SuppressWarnings("unused")
    static class FakeChild<T> extends FakeParentSpectrumTest<T> {
        private String toSkip;
        private FakeSpectrumPage childTestPage;
    }

    @Getter
    @SuppressWarnings("unused")
    static class FakeParentSpectrumTest<T> extends SpectrumTest<T> {
        private String parentToSkip;
        private FakeSpectrumPage parentTestPage;
    }

    @Getter
    @SuppressWarnings("unused")
    static class FakeParentSpectrumTestVoid extends SpectrumTest<Void> {
        private String parentToSkip;
        private FakeSpectrumPageVoid parentTestPage;
    }

    @Endpoint("blah")
    @SuppressWarnings("unused")
    static class FakeSpectrumPage extends SpectrumPage<FakeSpectrumPage, FakeData> {

        @JsWebElement
        private WebElement jsWebElement;

        @JsWebElement
        private List<WebElement> jsWebElementList;
    }

    @Endpoint("blah")
    @SuppressWarnings("unused")
    static class FakeSpectrumPageVoid extends SpectrumPage<FakeSpectrumPageVoid, Void> {
    }

    static class FakeSpectrumPageWithoutEndpoint<T> extends SpectrumPage<FakeSpectrumPageWithoutEndpoint<T>, T> {
    }

    static class FakeData {
    }
}
