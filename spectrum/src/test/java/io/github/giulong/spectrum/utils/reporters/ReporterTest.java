package io.github.giulong.spectrum.utils.reporters;

import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class ReporterTest {

    private MockedStatic<FileUtils> fileUtilsMockedStatic;
    private MockedStatic<FreeMarkerWrapper> freeMarkerWrapperMockedStatic;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private TestBook testBook;

    @InjectMocks
    private DummyReporter reporter;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("fileUtils", reporter, fileUtils);
        Reflections.setField("freeMarkerWrapper", reporter, freeMarkerWrapper);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
    }

    @AfterEach
    void afterEach() {
        freeMarkerWrapperMockedStatic.close();
        fileUtilsMockedStatic.close();
    }

    @Test
    @DisplayName("flush should call the doOutputFrom method with the template interpolated with the testbook vars")
    void flush() {
        final String readTemplate = "readTemplate";
        final String interpolatedTemplate = "interpolatedTemplate";
        final Map<String, Object> vars = Map.of();

        when(fileUtils.read(reporter.getTemplate())).thenReturn(readTemplate);
        when(testBook.getVars()).thenReturn(vars);
        when(freeMarkerWrapper.interpolate(readTemplate, vars)).thenReturn(interpolatedTemplate);

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
