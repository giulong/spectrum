package io.github.giulong.spectrum.verify_browsers.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.ExtentReportPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
@DisplayName("Extent Report Verifier")
public class ExtentReportVerifierIT extends SpectrumTest<Data> {

    private ExtentReportPage extentReportPage;

    @Test
    @DisplayName("should check the report")
    public void report() {
        final Map<String, String> testLabels = data.getExtentReport().getTestLabels();

        webDriver.get(String.format("file:///%s/it-testbook/target/spectrum/reports/report.html", Path.of(System.getProperty("user.dir")).getParent()));

        assertEquals(8, extentReportPage.getTestViewTests().size(), "Total tests");
        assertEquals(6, countTestsWithStatus("pass"), "Passed tests");
        assertEquals(1, countTestsWithStatus("skip"), "Skipped tests");
        assertEquals(1, countTestsWithStatus("fail"), "Failed tests");

        assertEquals(testLabels.get("skippedTest"), extentReportPage.getSkippedTest().getText());
        assertEquals(testLabels.get("upload"), extentReportPage.getUpload().getText());
        assertEquals(testLabels.get("customEvents"), extentReportPage.getCustomEvents().getText());
        assertEquals(testLabels.get("fail"), extentReportPage.getFail().getText());
        assertEquals(testLabels.get("loginFalse"), extentReportPage.getLoginFalse().getText());
        assertEquals(testLabels.get("loginTrue"), extentReportPage.getLoginTrue().getText());
        assertEquals(testLabels.get("noDisplayName"), extentReportPage.getNoDisplayName().getText());
        assertEquals(testLabels.get("download"), extentReportPage.getDownload().getText());

        assertTrue(isPresent(By.id("video-checkboxit-testwithnodisplayname()")));
        assertTrue(isPresent(By.id("video-filesit-upload")));
        assertTrue(isPresent(By.id("video-demoit-sending-custom-events")));
        assertTrue(isPresent(By.id("video-demoit-this-one-should-fail-for-demonstration-purposes")));
        assertTrue(isPresent(By.id("video-loginformit-with-user-giulio-we-expect-login-to-be-successful:-false")));
        assertTrue(isPresent(By.id("video-loginformit-with-user-tom-we-expect-login-to-be-successful:-true")));
        assertTrue(isPresent(By.id("video-filesit-download")));
        assertFalse(isPresent(By.id("video-demoit-skipped-test")));

        // check screenshot was added programmatically with the screenshotInfo(String) method
        assertFalse(extentReportPage.getScreenshotContainers().isEmpty());
    }

    private long countTestsWithStatus(final String status) {
        return extentReportPage.getTestViewTests()
                .stream()
                .map(webElement -> webElement.getAttribute("status"))
                .filter(s -> s.equals(status))
                .count();
    }
}
