package io.github.giulong.spectrum.utils.reporters;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Map;

import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.testbook.TestBook;

import lombok.Getter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ReporterTest {

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private TestBook testBook;

    @InjectMocks
    private DummyReporter reporter;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("freeMarkerWrapper", reporter, freeMarkerWrapper);
    }

    @Test
    @DisplayName("flush should call the doOutputFrom method with the template interpolated with the testbook vars")
    void flush() {
        final String interpolatedTemplate = "interpolatedTemplate";
        final Map<String, Object> vars = Map.of();

        when(testBook.getVars()).thenReturn(vars);
        when(freeMarkerWrapper.interpolateTemplate(reporter.getTemplate(), vars)).thenReturn(interpolatedTemplate);

        reporter.flush(testBook);

        assertTrue(reporter.doOutputCalled);
        assertTrue(reporter.cleanupOldReportsCalled);
    }

    @Getter
    private static final class DummyReporter extends Reporter {

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
