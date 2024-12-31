package io.github.giulong.spectrum.verify_browsers.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.ExtentReportPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
public class ExtentReportVerifierIT extends SpectrumTest<Data> {

    private static final String VIDEO_PATTERN = "data:video/mp4;base64,(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?";
    private static final String IMAGE_PATTERN = "data:image/png;base64,(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?";

    private ExtentReportPage extentReportPage;

    private void commonChecksFor(final String url) {
        final Map<String, String> testLabels = data.getExtentReport().getTestLabels();

        driver.get(url);

        assertEquals(20, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(17, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(1, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(2, countTestsWithStatus("fail"), "Failed tests");

        actions.scrollToElement(extentReportPage.getSkippedTest()).perform();
        assertEquals(testLabels.get("skippedTest"), extentReportPage.getSkippedTest().getText());

        actions.scrollToElement(extentReportPage.getUpload()).perform();
        assertEquals(testLabels.get("upload"), extentReportPage.getUpload().getText());

        actions.scrollToElement(extentReportPage.getCustomEvents()).perform();
        assertEquals(testLabels.get("customEvents"), extentReportPage.getCustomEvents().getText());

        actions.scrollToElement(extentReportPage.getFail()).perform();
        assertEquals(testLabels.get("fail"), extentReportPage.getFail().getText());

        actions.scrollToElement(extentReportPage.getLoginFalse()).perform();
        assertEquals(testLabels.get("loginFalse"), extentReportPage.getLoginFalse().getText());

        actions.scrollToElement(extentReportPage.getLoginTrue()).perform();
        assertEquals(testLabels.get("loginTrue"), extentReportPage.getLoginTrue().getText());

        actions.scrollToElement(extentReportPage.getNoDisplayName()).perform();
        assertEquals(testLabels.get("noDisplayName"), extentReportPage.getNoDisplayName().getText());

        actions.scrollToElement(extentReportPage.getDownload()).perform();
        assertEquals(testLabels.get("download"), extentReportPage.getDownload().getText());

        assertFalse(isPresent(By.id("video-demoit-skipped-test")));

        assertEquals("3", extentReportPage.getVideoJsWebElementItCheckingJsWebElements().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoJsWebElementItTestFindElementsMethod().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoJsWebElementItShadowDom().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoJsWebElementItTestInputFieldActions().getDomProperty("duration"));

        assertEquals("1", extentReportPage.getVideoJavascriptItTestInputFieldActions().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoJavascriptItTestFindElementMethod().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoJavascriptItShadowDom().getDomProperty("duration"));
        assertEquals("4", extentReportPage.getVideoJavascriptItTestWithNoDisplayName().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoJavascriptItTestWebElementGetMethods().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoJavascriptItTestFindElementsMethod().getDomProperty("duration"));

        assertEquals("5", extentReportPage.getVideoTestFactoryItDynamicTestsWithContainers().getDomProperty("duration"));

        assertEquals("15", extentReportPage.getVideoNavigationItTestToShowNavigationAndProducedVideo().getDomProperty("duration"));

        assertEquals("5", extentReportPage.getVideoCheckboxItTestWithNoDisplayName().getDomProperty("duration"));

        assertEquals("1", extentReportPage.getVideoDemoItSendingCustomEvents().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoDemoItThisOneShouldFailForDemonstrationPurposes().getDomProperty("duration"));

        assertEquals("3", extentReportPage.getVideoLoginFormItWithUserGiulioWeExpectLoginToBeSuccessfulFalse().getDomProperty("duration"));
        assertEquals("3", extentReportPage.getVideoLoginFormItWithUserTomWeExpectLoginToBeSuccessfulTrue().getDomProperty("duration"));

        assertEquals("15", extentReportPage.getVideoFilesItUpload().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoFilesItDownload().getDomProperty("duration"));

        // check screenshot was added programmatically with the screenshotInfo(String) method
        assertFalse(extentReportPage.getScreenshotContainers().isEmpty());

        assertTrue(Objects.requireNonNull(extentReportPage.getVideoFilesItUpload().getDomAttribute("class")).contains("class-added-from-js"));

        final List<String> originalTests = extentReportPage
                .getTestViewTestsDetails()
                .stream()
                .map(webElement -> webElement.getDomProperty("id"))
                .toList();

        final List<String> sortedTest = new ArrayList<>(originalTests).stream().sorted().toList();
        assertEquals(originalTests, sortedTest);
    }

    @Test
    @DisplayName("should check the report")
    public void report() {
        commonChecksFor(String.format("file:///%s/it/target/spectrum/reports/report/report.html", Path.of(System.getProperty("user.dir")).getParent()));
    }

    @Test
    @DisplayName("should check the inline report")
    public void inlineReport() {
        commonChecksFor(String.format("file:///%s/it/target/spectrum/inline-reports/report.html", Path.of(System.getProperty("user.dir")).getParent()));

        assertThat(Objects.requireNonNull(extentReportPage.getVideoDemoItSendingCustomEvents().getDomProperty("src")), matchesPattern(VIDEO_PATTERN));
        extentReportPage
                .getInlineImages()
                .stream()
                .map(inlineImage -> inlineImage.getDomProperty("src"))
                .map(Objects::requireNonNull)
                .forEach(src -> assertThat(src, matchesPattern(IMAGE_PATTERN)));
    }

    private long countTestsWithStatus(final String status) {
        return extentReportPage.getTestViewTests()
                .stream()
                .map(webElement -> webElement.getDomAttribute("status"))
                .filter(status::equals)
                .count();
    }
}
