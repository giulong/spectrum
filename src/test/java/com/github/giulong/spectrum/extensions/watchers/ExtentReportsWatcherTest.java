package com.github.giulong.spectrum.extensions.watchers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.Markup;
import com.github.giulong.spectrum.SpectrumTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

import static com.aventstack.extentreports.Status.*;
import static com.github.giulong.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static com.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static com.github.giulong.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentReportsWatcher")
class ExtentReportsWatcherTest {

    private final String className = "className";
    private final String displayName = "displayName";

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private WebDriver webDriver;

    @Mock
    private ExtentReports extentReports;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private Status status;

    @Mock
    private SpectrumTest<?> spectrumTest;

    @Mock
    private RuntimeException exception;

    @Captor
    private ArgumentCaptor<Markup> markupArgumentCaptor;

    @Captor
    private ArgumentCaptor<Markup> skipMarkupArgumentCaptor;

    @InjectMocks
    private ExtentReportsWatcher extentReportsWatcher;

    private void finalizeTestStubs() {
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(store.get(WEB_DRIVER, WebDriver.class)).thenReturn(webDriver);
        when(store.get(EXTENT_TEST, ExtentTest.class)).thenReturn(extentTest);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
    }

    private void createExtentTestStubs() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(EXTENT_REPORTS, ExtentReports.class)).thenReturn(extentReports);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
        when(extentReports.createTest(String.format("<div>%s</div>%s", className, displayName))).thenReturn(extentTest);
    }

    @DisplayName("testDisabled should create the test in the report and delegate to finalizeTest")
    @ParameterizedTest(name = "with reason {0}")
    @CsvSource(value = {
            "reason provided,reason provided",
            "NIL,no reason"
    }, nullValues = "NIL")
    public void testDisabled(final String reason, Object expected) {
        createExtentTestStubs();
        finalizeTestStubs();

        extentReportsWatcher.testDisabled(extensionContext, Optional.ofNullable(reason));
        verify(webDriver).quit();
        verify(extentTest).skip(skipMarkupArgumentCaptor.capture());
        verify(extentTest).log(eq(SKIP), markupArgumentCaptor.capture());

        assertEquals("<span class='badge white-text amber'>Skipped: " + expected + "</span>", skipMarkupArgumentCaptor.getValue().getMarkup());
        assertEquals("<span class='badge white-text amber'>END TEST</span>", markupArgumentCaptor.getValue().getMarkup());
    }

    @Test
    @DisplayName("testSuccessful should just delegate to finalizeTest")
    public void testSuccessful() {
        finalizeTestStubs();

        extentReportsWatcher.testSuccessful(extensionContext);
        verify(webDriver).quit();
        verify(extentTest).log(eq(PASS), markupArgumentCaptor.capture());

        assertEquals("<span class='badge white-text green'>END TEST</span>", markupArgumentCaptor.getValue().getMarkup());
    }

    @Test
    @DisplayName("testAborted should just delegate to finalizeTest")
    public void testAborted() {
        finalizeTestStubs();

        extentReportsWatcher.testAborted(extensionContext, new RuntimeException());
        verify(webDriver).quit();
        verify(extentTest).log(eq(FAIL), markupArgumentCaptor.capture());

        assertEquals("<span class='badge white-text red'>END TEST</span>", markupArgumentCaptor.getValue().getMarkup());
    }

    @Test
    @DisplayName("testFailed should add a screenshot to the report and delegate to finalizeTest")
    public void testFailed() {
        finalizeTestStubs();
        when(extensionContext.getRequiredTestInstance()).thenReturn(spectrumTest);

        extentReportsWatcher.testFailed(extensionContext, exception);
        verify(extentTest).fail(exception);
        verify(spectrumTest).addScreenshotToReport("<span class='badge white-text red'>TEST FAILED</span>", FAIL);
        verify(webDriver).quit();
        verify(extentTest).log(eq(FAIL), markupArgumentCaptor.capture());

        assertEquals("<span class='badge white-text red'>END TEST</span>", markupArgumentCaptor.getValue().getMarkup());
    }

    @Test
    @DisplayName("finalizeTest should close the webDriver and add a log in the extent report")
    public void finalizeTest() {
        finalizeTestStubs();
        when(status.name()).thenReturn("name");

        extentReportsWatcher.finalizeTest(extensionContext, status);
        verify(webDriver).quit();
        verify(extentTest).log(eq(status), markupArgumentCaptor.capture());

        assertEquals("<span class='badge white-text green'>END TEST</span>", markupArgumentCaptor.getValue().getMarkup());
    }
}
