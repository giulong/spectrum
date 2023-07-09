package io.github.giulong.spectrum.utils.testbook.reporters;

import io.github.giulong.spectrum.pojos.testbook.QualityGate;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestBookReporter")
class TestBookReporterTest {

    @Mock
    private TestBook testBook;

    @Mock
    private QualityGate qualityGate;

    @InjectMocks
    private DummyTestBookReporter testBookReporter;

    @Test
    @DisplayName("evaluateQualityGateStatusFrom should put in the testbook vars the interpolated quality gate condition")
    public void evaluateQualityGateStatusFrom() {
        final String condition = "condition";
        final Map<String, Object> vars = new HashMap<>();

        when(testBook.getVars()).thenReturn(vars);
        when(testBook.getQualityGate()).thenReturn(qualityGate);
        when(qualityGate.getCondition()).thenReturn(condition);
        TestBookReporter.evaluateQualityGateStatusFrom(testBook);

        assertEquals(1, vars.size());
        assertEquals(condition, vars.get("qgStatus"));
    }

    @Test
    @DisplayName("flush should call the doOutputFrom method with the template interpolated with the testbook vars")
    public void flush() {
        final Map<String, Object> vars = Map.of();

        when(testBook.getVars()).thenReturn(vars);
        testBookReporter.flush(testBook);

        assertTrue(testBookReporter.doOutputCalled);
    }

    @Getter
    private static class DummyTestBookReporter extends TestBookReporter {

        private boolean doOutputCalled;

        @Override
        public String getTemplate() {
            return "template";
        }

        @Override
        public void doOutputFrom(String interpolatedTemplate) {
            doOutputCalled = true;
        }
    }
}
