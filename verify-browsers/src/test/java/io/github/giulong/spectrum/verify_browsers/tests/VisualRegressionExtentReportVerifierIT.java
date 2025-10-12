package io.github.giulong.spectrum.verify_browsers.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.VisualRegressionExtentReportPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.nio.file.Path;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(20, extentReportPage.getScreenshotMessages().size(), "Screenshot messages should be displayed");

        extentReportPage.getTestViewTests().get(2).click();
        assertEquals(1, visibleElementsOf(extentReportPage.getVisualRegressions()).size(), "There should only be one visual regression");
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
