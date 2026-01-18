package io.github.giulong.spectrum;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import io.github.giulong.spectrum.exceptions.TestFailedException;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import io.github.giulong.spectrum.interfaces.LocatorFactory;
import io.github.giulong.spectrum.types.DownloadWait;
import io.github.giulong.spectrum.types.ImplicitWait;
import io.github.giulong.spectrum.types.PageLoadWait;
import io.github.giulong.spectrum.types.ScriptWait;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.js.Js;
import io.github.giulong.spectrum.utils.js.JsWebElementProxyBuilder;

import net.datafaker.Faker;

import lombok.Getter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.module.BrowsingContextInspector;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

class SpectrumTestTest {

    @Mock
    private TestContext testContext;

    @Mock(extraInterfaces = {TakesScreenshot.class, JavascriptExecutor.class})
    private WebDriver driver;

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
    private Faker faker;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration configuration;

    @Mock
    private Configuration.Drivers drivers;

    @Mock
    private Configuration.Drivers.Waits waits;

    @Mock
    private Configuration.Drivers.Waits.AutoWait auto;

    @Mock
    private Duration timeout;

    @Mock
    private ExtentReports extentReports;

    @Mock
    private TestData testData;

    @Mock
    private TestFailedException testFailedException;

    @Mock
    private Js js;

    @Mock
    private JsWebElementProxyBuilder jsWebElementProxyBuilder;

    @Mock
    private LogInspector logInspector;

    @Mock
    private BrowsingContext browsingContext;

    @Mock
    private BrowsingContextInspector browsingContextInspector;

    @Mock
    private Network network;

    @Mock
    private WebElement webElement;

    @Mock
    private List<WebElement> webElementList;

    @MockFinal
    @SuppressWarnings("unused")
    private YamlUtils yamlUtils;

    @InjectMocks
    private FakeSpectrumTest<FakeData> spectrumTest;

    @InjectMocks
    private FakeChild<FakeData> childTest;

    @InjectMocks
    private FakeChild<Void> childTestVoid;

    @InjectMocks
    private FakeParentSpectrumTestVoid fakeParentSpectrumTestVoid;

    @BeforeEach
    void beforeEach() {
        spectrumTest.data = data;
        spectrumTest.testPage.jsWebElement = webElement;
        spectrumTest.testPage.jsWebElementList = webElementList;
    }

    @Test
    @DisplayName("beforeAll should inject all the provided static args")
    void testBeforeAll() {
        SpectrumTest.configuration = null;
        SpectrumTest.eventsDispatcher = null;
        SpectrumTest.extentReports = null;
        SpectrumTest.faker = null;

        SpectrumTest.beforeAll(configuration, eventsDispatcher, extentReports, faker);

        assertEquals(configuration, SpectrumTest.configuration);
        assertEquals(eventsDispatcher, SpectrumTest.eventsDispatcher);
        assertEquals(extentReports, SpectrumTest.extentReports);
        assertEquals(faker, SpectrumTest.faker);
    }

    @Test
    @DisplayName("beforeEach should inject all the provided args resolved via JUnit, and call injectDataIn and injectPagesInto")
    void testBeforeEach() {
        // injectDataIn
        final String folder = "folder";
        final LocatorFactory locatorFactory = mock(LocatorFactory.class);
        final ElementLocatorFactory elementLocatorFactory = mock(ElementLocatorFactory.class);
        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFolder()).thenReturn(folder);
        when(yamlUtils.readClient(folder + "/data.yaml", FakeData.class)).thenReturn(data);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getLocatorFactory()).thenReturn(locatorFactory);
        when(locatorFactory.buildFor(driver)).thenReturn(elementLocatorFactory);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        assertNull(childTestVoid.childTestPage);
        assertNull(childTestVoid.getParentTestPage());

        childTestVoid.beforeEach(testContext, testData, statefulExtentTest, driver, implicitWait, pageLoadWait, scriptWait, downloadWait,
                actions, js, jsWebElementProxyBuilder, logInspector, browsingContext, browsingContextInspector, network, null);

        assertEquals(driver, childTestVoid.driver);
        assertEquals(driver, childTestVoid.takesScreenshot);
        assertEquals(driver, childTestVoid.javascriptExecutor);
        assertEquals(implicitWait, childTestVoid.implicitWait);
        assertEquals(pageLoadWait, childTestVoid.pageLoadWait);
        assertEquals(scriptWait, childTestVoid.scriptWait);
        assertEquals(downloadWait, childTestVoid.downloadWait);
        assertEquals(statefulExtentTest, childTestVoid.statefulExtentTest);
        assertEquals(extentTest, childTestVoid.extentTest);
        assertEquals(actions, childTestVoid.actions);
        assertEquals(testData, childTestVoid.testData);
        assertEquals(js, childTestVoid.js);
        assertEquals(logInspector, childTestVoid.logInspector);
        assertEquals(browsingContext, childTestVoid.browsingContext);
        assertEquals(browsingContextInspector, childTestVoid.browsingContextInspector);
        assertEquals(network, childTestVoid.network);
        assertEquals(jsWebElementProxyBuilder, childTestVoid.jsWebElementProxyBuilder);
        assertNull(childTestVoid.data);
        assertEquals(testContext, childTestVoid.testContext);

        // injectPages
        assertNull(childTestVoid.toSkip);
        assertNotNull(childTestVoid.childTestPage);
        assertInstanceOf(FakeSpectrumPage.class, childTestVoid.childTestPage);
        assertNull(childTestVoid.getParentToSkip());
        assertNotNull(childTestVoid.getParentTestPage());
        assertInstanceOf(FakeSpectrumPage.class, childTestVoid.getParentTestPage());
    }

    @Test
    @DisplayName("baseSpectrumAfterEach should do nothing if testData is NOT marked as failed")
    void testBaseSpectrumAfterEach() {
        when(testData.getTestFailedException()).thenReturn(null);

        assertDoesNotThrow(() -> spectrumTest.baseSpectrumAfterEach());

        verifyNoMoreInteractions(testData);
    }

    @Test
    @DisplayName("baseSpectrumAfterEach should throw the TestFailedException if present")
    void testBaseSpectrumAfterEachThrows() {
        when(testData.getTestFailedException()).thenReturn(testFailedException);

        assertThrows(TestFailedException.class, () -> spectrumTest.baseSpectrumAfterEach());

        verifyNoMoreInteractions(testData);
    }

    @Test
    @DisplayName("injectPages should init also init pages from super classes")
    void injectPages() {
        final LocatorFactory locatorFactory = mock(LocatorFactory.class);
        final ElementLocatorFactory elementLocatorFactory = mock(ElementLocatorFactory.class);
        when(configuration.getDrivers()).thenReturn(drivers);
        when(drivers.getLocatorFactory()).thenReturn(locatorFactory);
        when(locatorFactory.buildFor(driver)).thenReturn(elementLocatorFactory);

        assertNull(childTest.childTestPage);
        assertNull(childTest.getParentTestPage());

        final List<? extends SpectrumPage<?, ?>> actual = childTest.injectPages();

        assertEquals(2, actual.size());

        assertNull(childTest.toSkip);
        assertNotNull(childTest.childTestPage);
        assertInstanceOf(FakeSpectrumPage.class, childTest.childTestPage);

        assertNull(childTest.getParentToSkip());
        assertNotNull(childTest.getParentTestPage());
        assertInstanceOf(FakeSpectrumPage.class, childTest.getParentTestPage());
    }

    @Test
    @DisplayName("injectPageInto should set a new instance of SpectrumPage in the provided field and return the instance")
    void injectPageInto() {
        final SpectrumPage<?, FakeData> actual = spectrumTest.injectPageInto(Reflections.getField("testPage", spectrumTest));

        assertEquals(spectrumTest.testPage, actual);
        assertInstanceOf(FakeSpectrumPage.class, spectrumTest.testPage);
    }

    @Test
    @DisplayName("injectDataInPages should do nothing if data was already injected from SpectrumTest")
    void injectDataInNotNull() {
        spectrumTest.injectDataIn(List.of());

        // we assert it's null since we have SpectrumTest<FakeData>
        assertNull(spectrumTest.testPage.data);
    }

    @Test
    @DisplayName("injectDataIn should inject the data field in pages when we have SpectrumTest<Void>")
    void injectDataIn() {
        final FakeSpectrumPage fakeSpectrumPage = mock(FakeSpectrumPage.class);
        final FakeSpectrumPageVoid fakeSpectrumPageVoid = mock(FakeSpectrumPageVoid.class);
        final String folder = "folder";

        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFolder()).thenReturn(folder);
        when(yamlUtils.readClient(folder + "/data.yaml", FakeData.class)).thenReturn(data);

        childTestVoid.injectDataIn(List.of(fakeSpectrumPage, fakeSpectrumPageVoid));

        assertEquals(data, fakeSpectrumPage.data);
        assertNull(fakeSpectrumPageVoid.data);
    }

    @Test
    @DisplayName("injectDataInPages should not inject the data field in pages when we have SpectrumTest<Void>, if all pages are SpectrumPage<Void>")
    void injectDataInAllVoid() {
        final FakeSpectrumPageVoid fakeSpectrumPageVoid1 = mock(FakeSpectrumPageVoid.class);
        final FakeSpectrumPageVoid fakeSpectrumPageVoid2 = mock(FakeSpectrumPageVoid.class);

        fakeParentSpectrumTestVoid.injectDataIn(List.of(fakeSpectrumPageVoid1, fakeSpectrumPageVoid2));

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
