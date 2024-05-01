package io.github.giulong.spectrum.utils.reporters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogReporterTest {

    @InjectMocks
    private DummyLogReporter logReporter;

    @Test
    @DisplayName("doOutputFrom should just log the interpolated template")
    public void doOutputFrom() {
        final String interpolatedTemplate = "interpolatedTemplate";
        logReporter.doOutputFrom(interpolatedTemplate);

        // no verifications/assertions needed
    }

    @Test
    @DisplayName("cleanupOldReports should do nothing")
    public void cleanupOldReports() {
        logReporter.cleanupOldReports();

        // no verifications/assertions needed
    }

    private static class DummyLogReporter extends LogReporter {}
}
