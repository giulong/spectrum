package com.giuliolongfils.spectrum;

import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.spectrum.interfaces.Endpoint;
import com.giuliolongfils.spectrum.interfaces.Shared;
import com.giuliolongfils.spectrum.internals.EventsListener;
import lombok.Getter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpectrumTest")
public class SpectrumTestTest<T> {

    private List<Field> sharedFields;

    @Mock
    private WebDriver webDriver;

    @Mock
    private T data;

    @InjectMocks
    private FakeSpectrumTest<T> spectrumTest;

    @Mock
    private WebDriverWait implicitWait;

    @Mock
    private WebDriverWait pageLoadWait;

    @Mock
    private WebDriverWait scriptWait;

    @Mock
    private WebDriverWait downloadWait;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private EventsListener eventsListener;

    @Mock
    private Actions actions;


    @InjectMocks
    private FakeChild<T> childTest;

    @BeforeEach
    public void beforeEach() {
        sharedFields = Arrays
                .stream(spectrumTest.getClass().getFields())
                .filter(f -> f.isAnnotationPresent(Shared.class))
                .toList();

        spectrumTest.data = data;
    }

    @Test
    @DisplayName("initPage should init the provided field")
    public void testInitPage() throws NoSuchFieldException {
        final SpectrumPage<T> actual = spectrumTest.initPage(spectrumTest.getClass().getDeclaredField("testPage"), sharedFields);

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
        final SpectrumPage<T> actual = spectrumTest.initPage(spectrumTest.getClass().getDeclaredField("testPageWithoutEndpoint"), sharedFields);

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
