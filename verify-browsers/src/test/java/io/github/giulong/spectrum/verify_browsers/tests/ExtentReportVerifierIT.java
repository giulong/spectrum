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
class ExtentReportVerifierIT extends SpectrumTest<Data> {

    private static final String VIDEO_PATTERN = "data:video/mp4;base64,(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?";
    private static final String IMAGE_PATTERN = "data:image/png;base64,(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?";

    private ExtentReportPage extentReportPage;

    private void commonChecksFor(final String url) {
        final Map<String, String> testLabels = data.getExtentReport().getTestLabels();

        driver.get(url);

        assertEquals(28, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(25, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(1, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(2, countTestsWithStatus("fail"), "Failed tests");

        assertEquals(testLabels.get("noDisplayName"), extentReportPage.getNoDisplayName().getText());
        assertEquals(testLabels.get("noDisplayNameTestName"), extentReportPage.getNoDisplayNameTestName().getText());
        assertEquals(testLabels.get("customEvents"), extentReportPage.getCustomEvents().getText());
        assertEquals(testLabels.get("customEventsTestName"), extentReportPage.getCustomEventsTestName().getText());
        assertEquals(testLabels.get("skippedTest"), extentReportPage.getSkippedTest().getText());
        assertEquals(testLabels.get("skippedTestTestName"), extentReportPage.getSkippedTestTestName().getText());
        assertEquals(testLabels.get("fail"), extentReportPage.getFail().getText());
        assertEquals(testLabels.get("failTestName"), extentReportPage.getFailTestName().getText());
        assertEquals(testLabels.get("dynamic"), extentReportPage.getDynamicItNavigationToProveAutoWaitHelpsALot().getText());
        assertEquals(testLabels.get("dynamicTestName"), extentReportPage.getDynamicItNavigationToProveAutoWaitHelpsALotTestName().getText());

        assertEquals(testLabels.get("upload"), extentReportPage.getUpload().getText());
        assertEquals(testLabels.get("uploadTestName"), extentReportPage.getUploadTestName().getText());

        assertEquals(testLabels.get("login"), extentReportPage.getLoginFalse().getText());
        assertEquals(testLabels.get("loginFalseTestName"), extentReportPage.getLoginFalseTestName().getText());
        assertEquals(testLabels.get("login"), extentReportPage.getLoginTrue().getText());
        assertEquals(testLabels.get("loginTrueTestName"), extentReportPage.getLoginTrueTestName().getText());

        assertEquals(testLabels.get("download"), extentReportPage.getDownload().getText());
        assertEquals(testLabels.get("downloadTestName"), extentReportPage.getDownloadTestName().getText());
        assertEquals(testLabels.get("faker"), extentReportPage.getFakerItTheLoginShouldFailLeveragingRandomNameGeneratedByFaker().getText());
        assertEquals(testLabels.get("fakerTestName"), extentReportPage.getFakerItTheLoginShouldFailLeveragingRandomNameGeneratedByFakerTestName().getText());
        assertEquals(testLabels.get("faker"), extentReportPage.getFakerItWith0Increments().getText());
        assertEquals(testLabels.get("faker0TestName"), extentReportPage.getFakerItWith0IncrementsTestName().getText());
        assertEquals(testLabels.get("faker"), extentReportPage.getFakerItWith2Increments().getText());
        assertEquals(testLabels.get("faker2TestName"), extentReportPage.getFakerItWith2IncrementsTestName().getText());
        assertEquals(testLabels.get("faker"), extentReportPage.getFakerItWith5Increments().getText());
        assertEquals(testLabels.get("faker5TestName"), extentReportPage.getFakerItWith5IncrementsTestName().getText());

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

        assertEquals("2", extentReportPage.getVideoFilesItUpload().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoFilesItDownload().getDomProperty("duration"));

        assertEquals("1", extentReportPage.getVideoFakerItTheLoginShouldFailLeveragingRandomNameGeneratedByFaker().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoFakerItWith0Increments().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoFakerItWith2Increments().getDomProperty("duration"));
        assertEquals("1", extentReportPage.getVideoFakerItWith5Increments().getDomProperty("duration"));

        assertEquals("18", extentReportPage.getVideoDynamicItNavigationToProveAutoWaitHelpsALot().getDomProperty("duration"));

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
    void report() {
        commonChecksFor(String.format("file:///%s/it/target/spectrum/reports/report-chrome/report-chrome.html", Path.of(System.getProperty("user.dir")).getParent()));
        commonChecksFor(String.format("file:///%s/it/target/spectrum/reports/report-firefox/report-firefox.html", Path.of(System.getProperty("user.dir")).getParent()));
        commonChecksFor(String.format("file:///%s/it/target/spectrum/reports/report-edge/report-edge.html", Path.of(System.getProperty("user.dir")).getParent()));
    }

    @Test
    @DisplayName("should check the inline report")
    void inlineReport() {
        commonChecksFor(String.format("file:///%s/it/target/spectrum/inline-reports/report-chrome.html", Path.of(System.getProperty("user.dir")).getParent()));
        commonChecksFor(String.format("file:///%s/it/target/spectrum/inline-reports/report-firefox.html", Path.of(System.getProperty("user.dir")).getParent()));
        commonChecksFor(String.format("file:///%s/it/target/spectrum/inline-reports/report-edge.html", Path.of(System.getProperty("user.dir")).getParent()));

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
