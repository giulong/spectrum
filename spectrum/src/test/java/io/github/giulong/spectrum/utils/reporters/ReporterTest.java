package io.github.giulong.spectrum.utils.reporters;

import io.github.giulong.spectrum.utils.testbook.TestBook;
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
class ReporterTest {

    @Mock
    private TestBook testBook;

    @InjectMocks
    private DummyReporter reporter;

    @Test
    @DisplayName("flush should call the doOutputFrom method with the template interpolated with the testbook vars")
    public void flush() {
        final Map<String, Object> vars = Map.of();

        when(testBook.getVars()).thenReturn(vars);
        reporter.flush(testBook);

        assertTrue(reporter.doOutputCalled);
        assertTrue(reporter.cleanupOldReportsCalled);
    }

    @Getter
    private static class DummyReporter extends Reporter {

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
