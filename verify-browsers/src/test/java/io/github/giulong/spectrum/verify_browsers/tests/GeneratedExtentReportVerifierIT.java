package io.github.giulong.spectrum.verify_browsers.tests;

import static io.github.giulong.spectrum.verify_commons.CommonExtentVerifier.assertVideoDuration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.GeneratedExtentReportPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

class GeneratedExtentReportVerifierIT extends SpectrumTest<Data> {

    private static final String BASE_PATH = String.format("file:///%s/it-generated/target/spectrum/", Path.of(System.getProperty("user.dir")).getParent());

    @SuppressWarnings("unused")
    private GeneratedExtentReportPage extentReportPage;

    private void commonChecksFor(final String url) {
        final Map<String, String> testLabels = data.getExtentReport().getTestLabels();

        driver.get(BASE_PATH + url);

        assertEquals(1, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(1, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(0, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(0, countTestsWithStatus("fail"), "Failed tests");

        assertEquals(testLabels.get("generatedIt"), extentReportPage.getGeneratedIt().getText());
        assertEquals(testLabels.get("generatedItTestName"), extentReportPage.getGeneratedItTestName().getText());

        assertVideoDuration(extentReportPage.getVideoGeneratedIt(), 6);

        final List<String> originalTests = extentReportPage
                .getTestViewTestsDetails()
                .stream()
                .map(webElement -> webElement.getDomProperty("id"))
                .toList();

        final List<String> sortedTest = new ArrayList<>(originalTests).stream().sorted().toList();
        assertEquals(originalTests, sortedTest);

        // video data frames of Checkbox page testWithNoDisplayName()
        assertEquals("About to get https://the-internet.herokuapp.com/", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame0()));
        assertEquals("Clicking on xpath: id(\"content\")/ul[1]/li[6]/a[1", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame1()));
        assertEquals("Clicking on xpath: id(\"checkboxes\")/input[1", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame2()));
        assertEquals("Clicking on xpath: id(\"checkboxes\")/input[2", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame3()));
        assertEquals("Going back", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame4()));
        assertEquals("Going forward", extentReportPage.getTextOf(extentReportPage.getNoDisplayNameFrame5()));
    }

    @TestFactory
    @DisplayName("should check the report")
    Stream<DynamicNode> report() {
        return Stream
                .of("reports/report-chrome/report-chrome.html",
                        "reports/report-firefox/report-firefox.html",
                        "reports/report-edge/report-edge.html")
                .map(report -> dynamicTest(report, () -> commonChecksFor(report)));
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
