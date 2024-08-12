package io.github.giulong.spectrum.utils.reporters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

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

    private static class DummyLogReporter extends LogReporter {
    }
}
