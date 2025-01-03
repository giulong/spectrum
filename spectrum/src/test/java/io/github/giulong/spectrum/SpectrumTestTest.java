package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import net.datafaker.Faker;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import io.github.giulong.spectrum.types.*;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.js.Js;
import io.github.giulong.spectrum.utils.js.JsWebElementProxyBuilder;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpectrumTestTest {

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
    private Faker faker;

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
    private WebElement webElement;

    @Mock
    private List<WebElement> webElementList;

    @Mock
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

        Reflections.setField("yamlUtils", spectrumTest, yamlUtils);
        Reflections.setField("yamlUtils", childTest, yamlUtils);
        Reflections.setField("yamlUtils", childTestVoid, yamlUtils);
        Reflections.setField("yamlUtils", fakeParentSpectrumTestVoid, yamlUtils);
    }

    @Test
    @DisplayName("beforeEach should set all the provided args resolved via JUnit, and call injectDataIn and injectPagesInto")
    void testBeforeEach() {
        // injectDataIn
        final String folder = "folder";
        when(configuration.getData()).thenReturn(dataConfiguration);
        when(dataConfiguration.getFolder()).thenReturn(folder);
        when(yamlUtils.readClient(folder + "/data.yaml", FakeData.class)).thenReturn(data);

        assertNull(childTestVoid.childTestPage);
        assertNull(childTestVoid.getParentTestPage());

        childTestVoid.beforeEach(testContext, configuration, testData, statefulExtentTest, webDriver, implicitWait, pageLoadWait, scriptWait, downloadWait,
                extentReports, actions, eventsDispatcher, js, faker, jsWebElementProxyBuilder, null);

        assertEquals(testContext, spectrumTest.testContext);
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
        assertEquals(js, spectrumTest.js);
        assertEquals(faker, spectrumTest.faker);
        assertEquals(jsWebElementProxyBuilder, spectrumTest.jsWebElementProxyBuilder);
        assertEquals(data, spectrumTest.data);

        // injectPages
        assertNull(childTestVoid.toSkip);
        assertNotNull(childTestVoid.childTestPage);
        assertInstanceOf(FakeSpectrumPage.class, childTestVoid.childTestPage);
        assertNull(childTestVoid.getParentToSkip());
        assertNotNull(childTestVoid.getParentTestPage());
        assertInstanceOf(FakeSpectrumPage.class, childTestVoid.getParentTestPage());
    }

    @Test
    @DisplayName("injectPages should init also init pages from super classes")
    void injectPages() {
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
    @DisplayName("injectDataInPages should inject the data field in pages when we have SpectrumTest<Void>")
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
