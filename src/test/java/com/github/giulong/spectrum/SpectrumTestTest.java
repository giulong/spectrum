package com.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.github.giulong.spectrum.interfaces.Endpoint;
import com.github.giulong.spectrum.internals.EventsListener;
import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.pojos.testbook.TestBookTest;
import com.github.giulong.spectrum.types.DownloadWait;
import com.github.giulong.spectrum.types.ImplicitWait;
import com.github.giulong.spectrum.types.PageLoadWait;
import com.github.giulong.spectrum.types.ScriptWait;
import com.github.giulong.spectrum.utils.FileUtils;
import com.github.giulong.spectrum.utils.FreeMarkerWrapper;
import com.github.giulong.spectrum.utils.testbook.TestBook;
import com.github.giulong.spectrum.utils.testbook.parsers.TestBookParser;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.github.giulong.spectrum.enums.Result.NOT_RUN;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpectrumTest")
public class SpectrumTestTest<T> {

    private MockedStatic<FileUtils> fileUtilsMockedStatic;
    private MockedStatic<FreeMarkerWrapper> freeMarkerWrapperMockedStatic;

    @Mock
    private WebDriver webDriver;

    @Mock
    private T data;

    @InjectMocks
    private FakeSpectrumTest<T> spectrumTest;

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
    private EventsListener eventsListener;

    @Mock
    private Actions actions;

    @Mock
    private Configuration configuration;

    @Mock
    private ExtentReports extentReports;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private Properties properties;

    @Mock
    private Configuration.Application application;

    @Mock
    private TestBook testBook;

    @Mock
    private TestBookParser testBookParser;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private Configuration.FreeMarker freeMarker;

    @InjectMocks
    private FakeChild<T> childTest;

    @BeforeEach
    public void beforeEach() {
        spectrumTest.data = data;
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
    }

    @AfterEach
    public void afterEach() {
        fileUtilsMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
    }

    @Test
    @DisplayName("initPage should init the provided field")
    public void testInitPage() throws NoSuchFieldException {
        final SpectrumPage<T> actual = spectrumTest.initPage(spectrumTest.getClass().getDeclaredField("testPage"));

        assertEquals(spectrumTest.testPage, actual);
        assertThat(spectrumTest.testPage, instanceOf(FakeSpectrumPage.class));

        assertEquals("blah", spectrumTest.testPage.endpoint);

        assertEquals(webDriver, spectrumTest.testPage.webDriver);
        assertEquals(implicitWait, spectrumTest.testPage.implicitWait);
        assertEquals(pageLoadWait, spectrumTest.testPage.pageLoadWait);
        assertEquals(scriptWait, spectrumTest.testPage.scriptWait);
        assertEquals(downloadWait, spectrumTest.testPage.downloadWait);
        assertEquals(extentTest, spectrumTest.testPage.extentTest);
        assertEquals(eventsListener, spectrumTest.testPage.eventsListener);
        assertEquals(actions, spectrumTest.testPage.actions);
        assertEquals(data, spectrumTest.testPage.data);
    }

    @Test
    @DisplayName("initPages without endpoint")
    public void initPageWithoutEndpoint() throws NoSuchFieldException {
        final SpectrumPage<T> actual = spectrumTest.initPage(spectrumTest.getClass().getDeclaredField("testPageWithoutEndpoint"));

        assertEquals(spectrumTest.testPageWithoutEndpoint, actual);
        assertThat(spectrumTest.testPageWithoutEndpoint, instanceOf(FakeSpectrumPageWithoutEndpoint.class));

        assertEquals("", spectrumTest.testPageWithoutEndpoint.endpoint);

        assertEquals(webDriver, spectrumTest.testPageWithoutEndpoint.webDriver);
        assertEquals(implicitWait, spectrumTest.testPageWithoutEndpoint.implicitWait);
        assertEquals(pageLoadWait, spectrumTest.testPageWithoutEndpoint.pageLoadWait);
        assertEquals(scriptWait, spectrumTest.testPageWithoutEndpoint.scriptWait);
        assertEquals(downloadWait, spectrumTest.testPageWithoutEndpoint.downloadWait);
        assertEquals(extentTest, spectrumTest.testPageWithoutEndpoint.extentTest);
        assertEquals(eventsListener, spectrumTest.testPageWithoutEndpoint.eventsListener);
        assertEquals(actions, spectrumTest.testPageWithoutEndpoint.actions);
        assertEquals(data, spectrumTest.testPageWithoutEndpoint.data);
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
    @DisplayName("beforeAll should leverage a reentrant lock to initialize spectrum once for the entire suite")
    public void testBeforeAll() {
        final String banner = "banner";
        final String version = "version";
        final Map<String, TestBookTest> mappedTests = new HashMap<>();
        final TestBookTest test1 = TestBookTest.builder()
                .className("test 1")
                .testName("one")
                .build();

        final TestBookTest test2 = TestBookTest.builder()
                .className("another test")
                .testName("another")
                .build();
        final List<TestBookTest> tests = List.of(test1, test2);

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.readProperties("/spectrum.properties")).thenReturn(properties);
        when(fileUtils.read("/banner.txt")).thenReturn(banner);
        when(properties.getProperty("version")).thenReturn(version);
        when(configuration.getApplication()).thenReturn(application);
        when(application.getTestBook()).thenReturn(testBook);
        when(testBook.getMappedTests()).thenReturn(mappedTests);
        when(testBook.getParser()).thenReturn(testBookParser);
        when(testBookParser.parse()).thenReturn(tests);
        when(configuration.getFreeMarker()).thenReturn(freeMarker);

        when(FreeMarkerWrapper.getInstance()).thenReturn(freeMarkerWrapper);

        // explicitly called twice to ensure verifications are called just once
        SpectrumTest.beforeAll(configuration, extentReports);
        SpectrumTest.beforeAll(configuration, extentReports);

        assertTrue(SpectrumTest.isSuiteInitialised());
        assertEquals(configuration, SpectrumTest.configuration);
        assertEquals(extentReports, SpectrumTest.extentReports);
        assertEquals(2, mappedTests.size());
        mappedTests.values().stream().map(TestBookTest::getResult).forEach(result -> assertEquals(NOT_RUN, result));

        verify(freeMarkerWrapper).setupFrom(freeMarker);
    }

    @Test
    @DisplayName("beforeEach should set all the provided args resolved via JUnit, and call initPages")
    public void testBeforeEach() {
        childTest.beforeEach(webDriver, implicitWait, pageLoadWait, scriptWait, downloadWait, extentTest, actions, data);

        assertEquals(webDriver, spectrumTest.webDriver);
        assertEquals(implicitWait, spectrumTest.implicitWait);
        assertEquals(pageLoadWait, spectrumTest.pageLoadWait);
        assertEquals(scriptWait, spectrumTest.scriptWait);
        assertEquals(downloadWait, spectrumTest.downloadWait);
        assertEquals(extentTest, spectrumTest.extentTest);
        assertEquals(actions, spectrumTest.actions);
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
    @DisplayName("afterAll should flush the testbook and the extent reports")
    public void afterAll() {
        when(configuration.getApplication()).thenReturn(application);
        when(application.getTestBook()).thenReturn(testBook);

        SpectrumTest.afterAll(extentReports, configuration);

        verify(testBook).flush();
        verify(extentReports).flush();
    }

    @SuppressWarnings("unused")
    static class FakeSpectrumTest<T> extends SpectrumTest<T> {
        private String toSkip;
        private FakeSpectrumPage<T> testPage;
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
    static class FakeSpectrumPage<T> extends SpectrumPage<T> {
    }

    static class FakeSpectrumPageWithoutEndpoint<T> extends SpectrumPage<T> {
    }
}
