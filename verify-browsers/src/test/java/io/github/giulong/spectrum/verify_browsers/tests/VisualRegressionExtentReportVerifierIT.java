package io.github.giulong.spectrum.verify_browsers.tests;

import static io.github.giulong.spectrum.verify_commons.CommonExtentVerifier.assertVideoDuration;
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
    @DisplayName("should check the visual regression report")
    void report() {
        driver.get(String.format("file:///%s/it-visual-regression/target/spectrum/reports/report-chrome/report-chrome.html", Path.of(System.getProperty("user.dir")).getParent()));

        assertEquals(5, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(3, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(0, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(2, countTestsWithStatus("fail"), "Failed tests");

        assertFalse(extentReportPage.getScreenshotMessages().isEmpty(), "Screenshot messages should be displayed");

        final List<WebElement> videos = extentReportPage.getVideosFailFast();
        assertVideoDuration(videos.getFirst(), 16);
        assertVideoDuration(videos.get(1), 16);
        assertVideoDuration(videos.get(2), 2);

        assertVideoDuration(extentReportPage.getVideoTestFactoryItDynamicTestsWithContainers(), 8);

        assertEquals(1, extentReportPage.getVisualRegressions().size());

        extentReportPage.getTestViewTests().get(4).click();
        assertThat(extentReportPage.getTextInSecondContainerOf(extentReportPage.getVisualRegressionException()), containsString("There were 1 visual regressions"));

        final WebElement visualRegression = extentReportPage.getVisualRegressions().getFirst();
        assertEquals("visualregressionit-alwaysthesame", visualRegression.getDomAttribute("data-test-id"));
        assertEquals("2", visualRegression.getDomAttribute("data-frame"));
    }

    @Test
    @DisplayName("should check the visual regression fae report")
    void reportFailAtEnd() {
        driver.get(String.format("file:///%s/it-visual-regression-fae/target/spectrum/reports/report-chrome/report-chrome.html", Path.of(System.getProperty("user.dir"))
                .getParent()));

        assertEquals(5, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(3, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(0, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(2, countTestsWithStatus("fail"), "Failed tests");

        assertFalse(extentReportPage.getScreenshotMessages().isEmpty(), "Screenshot messages should be displayed");

        final List<WebElement> videos = extentReportPage.getVideosNotFailFast();
        assertVideoDuration(videos.getFirst(), 16);
        assertVideoDuration(videos.get(1), 16);
        assertVideoDuration(videos.get(2), 16);

        assertVideoDuration(extentReportPage.getVideoTestFactoryItDynamicTestsWithContainers(), 8);

        assertEquals(2, extentReportPage.getVisualRegressions().size());

        extentReportPage.getTestViewTests().get(4).click();
        assertThat(extentReportPage.getTextInSecondContainerOf(extentReportPage.getVisualRegressionException()), containsString("There were 2 visual regressions"));

        final WebElement visualRegression1 = extentReportPage.getVisualRegressions().getFirst();
        final WebElement visualRegression2 = extentReportPage.getVisualRegressions().get(1);

        assertEquals("visualregressionfailatendit-alwaysthesame", visualRegression1.getDomAttribute("data-test-id"));
        assertEquals("2", visualRegression1.getDomAttribute("data-frame"));

        assertEquals("visualregressionfailatendit-alwaysthesame", visualRegression2.getDomAttribute("data-test-id"));
        assertEquals("5", visualRegression2.getDomAttribute("data-frame"));
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
