package io.github.giulong.spectrum.utils.summary.reporters;

import io.github.giulong.spectrum.utils.summary.Summary;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SummaryReporter")
class SummaryReporterTest {

    @Mock
    private Summary summary;

    @InjectMocks
    private DummySummaryReporter summaryReporter;

    @Test
    @DisplayName("flush should call the doOutputFrom method with the template interpolated with summary in vars")
    public void flush() {
        final Map<String, Object> vars = Map.of();

        when(summary.getVars()).thenReturn(vars);
        summaryReporter.flush(summary);

        assertTrue(summaryReporter.doOutputCalled);
        assertTrue(summaryReporter.cleanupOldReportsCalled);
    }

    @Getter
    private static class DummySummaryReporter extends SummaryReporter {

        private boolean doOutputCalled;
        private boolean cleanupOldReportsCalled;

        @Override
        public String getTemplate() {
            return "template";
        }

        @Override
        public void doOutputFrom(String interpolatedTemplate) {
            doOutputCalled = true;
        }

        @Override
        public void cleanupOldReports() {
            cleanupOldReportsCalled = true;
        }
    }
}
