package io.github.giulong.spectrum.it_testbook_verifier.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_testbook_verifier.data.Data;
import io.github.giulong.spectrum.it_testbook_verifier.pages.ExtentReportPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Extent Report Verifier")
public class ExtentReportVerifierIT extends SpectrumTest<Data> {

    private ExtentReportPage extentReportPage;

    @Test
    @DisplayName("should check the report")
    public void report() {
        final Map<String, String> testLabels = data.getExtentReport().getTestLabels();

        webDriver.get(String.format("file:///%s/it-testbook/target/spectrum/reports/report.html", Path.of(System.getProperty("user.dir")).getParent()));

        assertEquals(8, extentReportPage.getTestViewTests().size());
        assertEquals(6, countTestsWithStatus("pass"));
        assertEquals(1, countTestsWithStatus("skip"));
        assertEquals(1, countTestsWithStatus("fail"));

        assertEquals(testLabels.get("skippedTest"), extentReportPage.getSkippedTest().getText());
        assertEquals(testLabels.get("upload"), extentReportPage.getUpload().getText());
        assertEquals(testLabels.get("customEvents"), extentReportPage.getCustomEvents().getText());
        assertEquals(testLabels.get("fail"), extentReportPage.getFail().getText());
        assertEquals(testLabels.get("loginFalse"), extentReportPage.getLoginFalse().getText());
        assertEquals(testLabels.get("loginTrue"), extentReportPage.getLoginTrue().getText());
        assertEquals(testLabels.get("noDisplayName"), extentReportPage.getNoDisplayName().getText());
        assertEquals(testLabels.get("download"), extentReportPage.getDownload().getText());
    }

    private long countTestsWithStatus(final String status) {
        return extentReportPage.getTestViewTests()
                .stream()
                .map(webElement -> webElement.getAttribute("status"))
                .filter(s -> s.equals(status))
                .count();
    }
}
