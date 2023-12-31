package io.github.giulong.spectrum.utils.summary.reporters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogSummaryReporter")
class LogSummaryReporterTest {

    @InjectMocks
    private LogSummaryReporter summaryReporter;

    @Test
    @DisplayName("doOutputFrom should just log the interpolated template")
    public void doOutputFrom() {
        final String interpolatedTemplate = "interpolatedTemplate";
        summaryReporter.doOutputFrom(interpolatedTemplate);

        // no verifications/assertions needed
    }

    @Test
    @DisplayName("cleanupOldReports should do nothing")
    public void cleanupOldReports() {
        summaryReporter.cleanupOldReports();

        // no verifications/assertions needed
    }
}
