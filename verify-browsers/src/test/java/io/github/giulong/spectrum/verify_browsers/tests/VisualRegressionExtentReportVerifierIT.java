package io.github.giulong.spectrum.verify_browsers.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Path;
import java.util.List;

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

        assertEquals(3, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(2, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(0, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(1, countTestsWithStatus("fail"), "Failed tests");

        assertFalse(extentReportPage.getScreenshotMessages().isEmpty(), "Screenshot messages should be displayed");

        assertEquals("15", extentReportPage.getVideoFilesItUploads().getFirst().getDomProperty("duration"), "video duration should match");
        assertEquals("15", extentReportPage.getVideoFilesItUploads().get(1).getDomProperty("duration"), "video duration should match");
        assertEquals("6", extentReportPage.getVideoFilesItUploads().get(2).getDomProperty("duration"), "video duration should match");

        extentReportPage.getTestViewTests().get(2).click();
        assertThat(extentReportPage.getTextOf(extentReportPage.getVisualRegressionException()), containsString("There were 1 visual regressions"));
    }

    private long countTestsWithStatus(final String status) {
        return extentReportPage
                .getTestViewTests()
                .stream()
                .map(webElement -> webElement.getDomAttribute("status"))
                .filter(status::equals)
                .count();
    }

    private List<WebElement> visibleElementsOf(final List<WebElement> webElements) {
        return webElements.stream().filter(WebElement::isDisplayed).toList();
    }
}
