package io.github.giulong.spectrum.verify_browsers.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.BiDiExtentReportPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BiDiExtentReportVerifierIT extends SpectrumTest<Data> {

    private static final String BASE_PATH = String.format("file:///%s/it-bidi/target/spectrum/", Path.of(System.getProperty("user.dir")).getParent());

    @SuppressWarnings("unused")
    private BiDiExtentReportPage extentReportPage;

    private void commonChecksFor(final String url) {
        final Map<String, String> testLabels = data.getExtentReport().getTestLabels();

        driver.get(BASE_PATH + url);

        assertEquals(7, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(7, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(0, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(0, countTestsWithStatus("fail"), "Failed tests");

        assertEquals(testLabels.get("noDisplayName"), extentReportPage.getNoDisplayName().getText());
        assertEquals(testLabels.get("noDisplayNameTestName"), extentReportPage.getNoDisplayNameTestName().getText());

        assertEquals("5", extentReportPage.getVideoCheckboxItTestWithNoDisplayName().getDomProperty("duration"));

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
    }

    @Test
    @DisplayName("should check the report")
    void report() {
        commonChecksFor("reports/report-chrome/report-chrome.html");
        commonChecksFor("reports/report-firefox/report-firefox.html");
        commonChecksFor("reports/report-edge/report-edge.html");
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
