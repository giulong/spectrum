package io.github.giulong.spectrum.verify_browsers.tests;

import static io.github.giulong.spectrum.verify_commons.CommonExtentVerifier.assertVideoDuration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.VisualRegressionExtentReportPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

class VisualRegressionExtentReportVerifierIT extends SpectrumTest<Data> {

    @SuppressWarnings("unused")
    private VisualRegressionExtentReportPage extentReportPage;

    @Test
    @DisplayName("should check the report")
    void report() {
        driver.get(String.format("file:///%s/it-visual-regression/target/spectrum/reports/report-chrome/report-chrome.html", Path.of(System.getProperty("user.dir")).getParent()));

        assertEquals(6, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(4, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(0, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(2, countTestsWithStatus("fail"), "Failed tests");

        assertFalse(extentReportPage.getScreenshotMessages().isEmpty(), "Screenshot messages should be displayed");

        assertVideoDuration(extentReportPage.getVideoFilesItUploads().getFirst(), 15);
        assertVideoDuration(extentReportPage.getVideoFilesItUploads().get(1), 15);
        assertVideoDuration(extentReportPage.getVideoFilesItUploads().get(2), 2);

        assertEquals(4, extentReportPage.getVisualRegressions().size());

        extentReportPage.getTestViewTests().get(2).click();
        assertThat(extentReportPage.getTextOf(extentReportPage.getVisualRegressionException()), containsString("There were 1 visual regressions"));

        final WebElement visualRegression1 = extentReportPage.getVisualRegressions().getFirst();
        assertEquals("visualregressionit-alwaysthesame", visualRegression1.getDomAttribute("data-test-id"));
        assertEquals("2", visualRegression1.getDomAttribute("data-frame"));

        extentReportPage.getTestViewTests().get(5).click();
        assertThat(extentReportPage.getTextOf(extentReportPage.getVisualRegressionException()), containsString("There were 3 visual regressions"));

        final WebElement visualRegression2 = extentReportPage.getVisualRegressions().get(1);
        final WebElement visualRegression3 = extentReportPage.getVisualRegressions().get(2);
        final WebElement visualRegression4 = extentReportPage.getVisualRegressions().get(3);

        assertEquals("visualregressionfailatendit-alwaysthesame", visualRegression2.getDomAttribute("data-test-id"));
        assertEquals("2", visualRegression2.getDomAttribute("data-frame"));

        assertEquals("visualregressionfailatendit-alwaysthesame", visualRegression3.getDomAttribute("data-test-id"));
        assertEquals("5", visualRegression3.getDomAttribute("data-frame"));

        assertEquals("visualregressionfailatendit-alwaysthesame", visualRegression4.getDomAttribute("data-test-id"));
        assertEquals("10", visualRegression4.getDomAttribute("data-frame"));
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
