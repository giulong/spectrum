package io.github.giulong.spectrum.utils.summary;

import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.ReflectionUtils;
import io.github.giulong.spectrum.utils.summary.reporters.SummaryReporter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Summary")
class SummaryTest {

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private SummaryReporter reporter1;

    @Mock
    private SummaryReporter reporter2;

    @Mock
    private SummaryGeneratingListener summaryGeneratingListener;

    @Mock
    private TestExecutionSummary testExecutionSummary;

    @InjectMocks
    private Summary summary;

    @BeforeEach
    public void beforeEach() {
        ReflectionUtils.setField("freeMarkerWrapper", summary, freeMarkerWrapper);
        ReflectionUtils.setField("fileUtils", summary, fileUtils);
        ReflectionUtils.setField("summaryGeneratingListener", summary, summaryGeneratingListener);
    }

    @Test
    @DisplayName("sessionClosed should put the summary in the vars and flush each reporter")
    public void sessionClosed() {
        ReflectionUtils.setField("reporters", summary, List.of(reporter1, reporter2));
        assertTrue(summary.getVars().isEmpty());

        when(summaryGeneratingListener.getSummary()).thenReturn(testExecutionSummary);

        summary.sessionClosed();

        assertEquals(1, summary.getVars().size());
        assertEquals(testExecutionSummary, summary.getVars().get("summary"));

        verify(reporter1).flush(summary);
        verify(reporter2).flush(summary);
    }
}