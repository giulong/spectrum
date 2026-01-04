package io.github.giulong.spectrum.verify_browsers.tests;

import static io.github.giulong.spectrum.verify_commons.CommonExtentVerifier.assertVideoDuration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.ExtentReportPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

class ExtentReportVerifierIT extends SpectrumTest<Data> {

    private static final String VIDEO_PATTERN = "data:video/mp4;base64,(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?";
    private static final String IMAGE_PATTERN = "data:image/png;base64,(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?";
    private static final String BASE_PATH = String.format("file:///%s/it/target/spectrum/", Path.of(System.getProperty("user.dir")).getParent());

    @SuppressWarnings("unused")
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

        assertVideoDuration(extentReportPage.getVideoJsWebElementItCheckingJsWebElements(), 3);
        assertVideoDuration(extentReportPage.getVideoJsWebElementItTestFindElementsMethod(), 1);
        assertVideoDuration(extentReportPage.getVideoJsWebElementItShadowDom(), 1);
        assertVideoDuration(extentReportPage.getVideoJsWebElementItTestInputFieldActions(), 1);

        assertVideoDuration(extentReportPage.getVideoJavascriptItTestInputFieldActions(), 1);
        assertVideoDuration(extentReportPage.getVideoJavascriptItTestFindElementMethod(), 2);
        assertVideoDuration(extentReportPage.getVideoJavascriptItShadowDom(), 2);
        assertVideoDuration(extentReportPage.getVideoJavascriptItTestWithNoDisplayName(), 4);
        assertVideoDuration(extentReportPage.getVideoJavascriptItTestWebElementGetMethods(), 1);
        assertVideoDuration(extentReportPage.getVideoJavascriptItTestFindElementsMethod(), 2);

        assertVideoDuration(extentReportPage.getVideoTestFactoryItDynamicTestsWithContainers(), 5);

        assertVideoDuration(extentReportPage.getVideoNavigationItTestToShowNavigationAndProducedVideo(), 15);

        assertVideoDuration(extentReportPage.getVideoCheckboxItTestWithNoDisplayName(), 5);

        assertVideoDuration(extentReportPage.getVideoDemoItSendingCustomEvents(), 1);
        assertVideoDuration(extentReportPage.getVideoDemoItThisOneShouldFailForDemonstrationPurposes(), 1);

        assertVideoDuration(extentReportPage.getVideoLoginFormItWithUserGiulioWeExpectLoginToBeSuccessfulFalse(), 3);
        assertVideoDuration(extentReportPage.getVideoLoginFormItWithUserTomWeExpectLoginToBeSuccessfulTrue(), 3);

        assertVideoDuration(extentReportPage.getVideoFilesItUpload(), 2);
        assertVideoDuration(extentReportPage.getVideoFilesItDownload(), 1);

        assertVideoDuration(extentReportPage.getVideoFakerItTheLoginShouldFailLeveragingRandomNameGeneratedByFaker(), 1);
        assertVideoDuration(extentReportPage.getVideoFakerItWith0Increments(), 1);
        assertVideoDuration(extentReportPage.getVideoFakerItWith2Increments(), 1);
        assertVideoDuration(extentReportPage.getVideoFakerItWith5Increments(), 1);

        assertVideoDuration(extentReportPage.getVideoDynamicItNavigationToProveAutoWaitHelpsALot(), 18);

        // check screenshot was added programmatically with the screenshotInfo(String) method
        assertFalse(extentReportPage.getScreenshotMessages().isEmpty(), "Screenshot messages should be displayed");

        assertTrue(Objects.requireNonNull(extentReportPage.getVideoFilesItUpload().getDomAttribute("class")).contains("class-added-from-js"));

        final List<String> originalTests = extentReportPage
                .getTestViewTestsDetails()
                .stream()
                .map(webElement -> webElement.getDomProperty("id"))
                .toList();

        final List<String> sortedTest = new ArrayList<>(originalTests).stream().sorted().toList();
        assertEquals(originalTests, sortedTest);

        // video data frames of Checkbox page testWithNoDisplayName()
        assertEquals("Text of tag name: h1 is 'Welcome to the-internet'", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame0()));
        assertEquals("Element css selector: #checkboxes -> tag name: input is selected? false", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame1()));
        assertEquals("Element css selector: #checkboxes -> tag name: input is selected? true", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame2()));
        assertEquals("Element css selector: #checkboxes -> tag name: input is selected? true", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame3()));
        assertEquals("After checking the first checkbox", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame4()));

        // video data frames of Dynamic elements navigation to prove auto-wait helps a lot
        extentReportPage.getDynamicItNavigationToProveAutoWaitHelpsALot().click();
        assertEquals("Element id: loading is displayed? true", extentReportPage.getTextOf(extentReportPage.getDynamicFrame0()));
        assertEquals("Tag name of id: finish is div", extentReportPage.getTextOf(extentReportPage.getDynamicFrame1()));
        assertEquals("Text of id: finish is 'Hello World!'", extentReportPage.getTextOf(extentReportPage.getDynamicFrame2()));
        assertEquals("Element id: loading is displayed? false", extentReportPage.getTextOf(extentReportPage.getDynamicFrame3()));
        assertEquals("Element id: loading is displayed? true", extentReportPage.getTextOf(extentReportPage.getDynamicFrame4()));
        assertEquals("Tag name of id: finish is div", extentReportPage.getTextOf(extentReportPage.getDynamicFrame5()));
        assertEquals("Text of id: finish is 'Hello World!'", extentReportPage.getTextOf(extentReportPage.getDynamicFrame6()));
        assertEquals("Element id: loading is displayed? false", extentReportPage.getTextOf(extentReportPage.getDynamicFrame7()));
        assertEquals("Element css selector: #checkbox -> tag name: input is displayed? true", extentReportPage.getTextOf(extentReportPage.getDynamicFrame8()));
        assertEquals("Element id: loading is displayed? true", extentReportPage.getTextOf(extentReportPage.getDynamicFrame9()));
        assertEquals("Text of id: message is 'It's gone!'", extentReportPage.getTextOf(extentReportPage.getDynamicFrame10()));
        assertEquals("Element id: loading is displayed? false", extentReportPage.getTextOf(extentReportPage.getDynamicFrame11()));
        assertEquals("Element id: loading is displayed? true", extentReportPage.getTextOf(extentReportPage.getDynamicFrame12()));
        assertEquals("Text of id: message is 'It's back!'", extentReportPage.getTextOf(extentReportPage.getDynamicFrame13()));
        assertEquals("Element id: loading is displayed? false", extentReportPage.getTextOf(extentReportPage.getDynamicFrame14()));
        assertEquals("Element css selector: #input\\-example -> tag name: input is enabled? false", extentReportPage.getTextOf(extentReportPage.getDynamicFrame15()));
        assertEquals("Text of id: message is 'It's enabled!'", extentReportPage.getTextOf(extentReportPage.getDynamicFrame16()));
        assertEquals("Text of id: message is 'It's disabled!'", extentReportPage.getTextOf(extentReportPage.getDynamicFrame17()));

        // video data frames of JavascriptIT testWithNoDisplayName()
        extentReportPage.getJavascriptItTestWithNoDisplayName().click();
        assertEquals("Text of tag name: h1 is 'Welcome to the-internet'", extentReportPage.getTextOf(extentReportPage.getJavascriptFrame0()));
        assertEquals("Element css selector: #checkboxes -> tag name: input is displayed? true", extentReportPage.getTextOf(extentReportPage.getJavascriptFrame1()));
        assertEquals("Element css selector: #checkboxes -> tag name: input is displayed? true", extentReportPage.getTextOf(extentReportPage.getJavascriptFrame2()));
        assertEquals("After checking the first checkbox", extentReportPage.getTextOf(extentReportPage.getJavascriptFrame3()));

        // video data frames of TestFactoryIT dynamicTestsWithContainers()
        extentReportPage.getTestFactoryItDynamicTestsWithContainers().click();
        extentReportPage.clickFirstCardHeader();
        assertEquals("Text of tag name: h1 is 'Welcome to the-internet'", extentReportPage.getTextInFirstContainerOf(extentReportPage.getDynamicContainersFrame0()));
        assertEquals("Before checking the checkbox number 1", extentReportPage.getTextInFirstContainerOf(extentReportPage.getDynamicContainersFrame1()));
        assertEquals("After checking the checkbox number 1", extentReportPage.getTextOf(extentReportPage.getDynamicContainersFrame2()));
        assertEquals("Text of tag name: h1 is 'Welcome to the-internet'", extentReportPage.getTextInSecondContainerOf(extentReportPage.getDynamicContainersFrame0()));
        assertEquals("Before checking the checkbox number 2", extentReportPage.getTextInSecondContainerOf(extentReportPage.getDynamicContainersFrame1()));
    }

    @Test
    @DisplayName("should check the report")
    void report() {
        commonChecksFor(BASE_PATH + "reports/report-chrome/report-chrome.html");
        commonChecksFor(BASE_PATH + "reports/report-firefox/report-firefox.html");
        commonChecksFor(BASE_PATH + "reports/report-edge/report-edge.html");

        assertThat(Objects.requireNonNull(extentReportPage.getVideoDemoItSendingCustomEvents().getDomProperty("src")), matchesPattern(VIDEO_PATTERN));
        extentReportPage
                .getInlineImages()
                .stream()
                .map(inlineImage -> inlineImage.getDomProperty("src"))
                .map(Objects::requireNonNull)
                .forEach(src -> assertThat(src, matchesPattern(IMAGE_PATTERN)));
    }

    private long countTestsWithStatus(final String status) {
        return extentReportPage
                .getTestViewTests()
                .stream()
                .map(webElement -> webElement.getDomAttribute("status"))
                .filter(status::equals)
                .count();
    }
}
