package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.interfaces.reports.CanReportSummary;
import io.github.giulong.spectrum.utils.reporters.Reporter;
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
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
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

    @Mock(extraInterfaces = CanReportSummary.class)
    private Reporter reporter1;

    @Mock(extraInterfaces = CanReportSummary.class)
    private Reporter reporter2;

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
        final long testsFoundCount = 1;
        final long testsSucceededCount = 2;
        final long testsFailedCount = 3;
        final long testsAbortedCount = 4;
        final long testsSkippedCount = 5;
        final long timeFinished = 1000000;
        final long timeStarted = 750000;

        ReflectionUtils.setField("reporters", summary, List.of(reporter1, reporter2));
        assertTrue(summary.getVars().isEmpty());

        when(summaryGeneratingListener.getSummary()).thenReturn(testExecutionSummary);
        when(testExecutionSummary.getTestsFoundCount()).thenReturn(testsFoundCount);
        when(testExecutionSummary.getTestsSucceededCount()).thenReturn(testsSucceededCount);
        when(testExecutionSummary.getTestsFailedCount()).thenReturn(testsFailedCount);
        when(testExecutionSummary.getTestsAbortedCount()).thenReturn(testsAbortedCount);
        when(testExecutionSummary.getTestsSkippedCount()).thenReturn(testsSkippedCount);
        when(testExecutionSummary.getTimeStarted()).thenReturn(timeStarted);
        when(testExecutionSummary.getTimeFinished()).thenReturn(timeFinished);

        summary.sessionClosed();

        final Map<String, Object> vars = summary.getVars();

        assertEquals(8, vars.size());
        assertEquals(testExecutionSummary, vars.get("summary"));
        assertEquals("00:04:10", vars.get("duration"));
        assertThat(String.valueOf(vars.get("timestamp")), matchesPattern("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}"));
        assertEquals(testsFoundCount, vars.get("total"));
        assertEquals((double) testsSucceededCount / testsFoundCount * 100, vars.get("successfulPercentage"));
        assertEquals((double) testsFailedCount / testsFoundCount * 100, vars.get("failedPercentage"));
        assertEquals((double) testsAbortedCount / testsFoundCount * 100, vars.get("abortedPercentage"));
        assertEquals((double) testsSkippedCount / testsFoundCount * 100, vars.get("disabledPercentage"));

        verify(reporter1).flush(summary);
        verify(reporter2).flush(summary);
    }
}
