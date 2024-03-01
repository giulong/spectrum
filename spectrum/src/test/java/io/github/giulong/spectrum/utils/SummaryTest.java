package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.interfaces.reports.CanReportSummary;
import io.github.giulong.spectrum.utils.reporters.FileReporter;
import io.github.giulong.spectrum.utils.reporters.Reporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mvel2.MVEL;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Summary")
class SummaryTest {

    private static MockedStatic<MVEL> mvelMockedStatic;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private FileUtils fileUtils;

    @Mock(extraInterfaces = CanReportSummary.class)
    private FileReporter reporter1;

    @Mock(extraInterfaces = CanReportSummary.class)
    private Reporter reporter2;

    @Mock
    private SummaryGeneratingListener summaryGeneratingListener;

    @Mock
    private TestExecutionSummary testExecutionSummary;

    @Mock
    private Map<String, Object> vars;

    @Captor
    private ArgumentCaptor<Map<String, Object>> freeMarkerVarsArgumentCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> mvelVarsArgumentCaptor;

    @InjectMocks
    private Summary summary;

    @BeforeEach
    public void beforeEach() {
        Reflections.setField("freeMarkerWrapper", summary, freeMarkerWrapper);
        Reflections.setField("fileUtils", summary, fileUtils);
        Reflections.setField("summaryGeneratingListener", summary, summaryGeneratingListener);

        mvelMockedStatic = mockStatic(MVEL.class);
    }

    @AfterEach
    public void afterEach() {
        mvelMockedStatic.close();
        Vars.getInstance().clear();
    }

    @Test
    @DisplayName("sessionOpened should log where to find the summary for each reporter")
    public void sessionOpened() {
        final String output = "output";
        final String extension = "extension";

        Reflections.setField("reporters", summary, List.of(reporter1, reporter2));

        when(fileUtils.getExtensionOf(output)).thenReturn(extension);
        when(reporter1.getOutput()).thenReturn(output);

        summary.sessionOpened();
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
        final String globalVar = "globalVar";
        final String globalValue = "globalValue";
        final String condition = "condition";
        final String interpolatedCondition = "interpolatedCondition";

        Vars.getInstance().put(globalVar, globalValue);
        Reflections.setField("reporters", summary, List.of(reporter1, reporter2));
        Reflections.setField("condition", summary, condition);
        assertTrue(summary.getVars().isEmpty());

        when(summaryGeneratingListener.getSummary()).thenReturn(testExecutionSummary);
        when(testExecutionSummary.getTestsFoundCount()).thenReturn(testsFoundCount);
        when(testExecutionSummary.getTestsSucceededCount()).thenReturn(testsSucceededCount);
        when(testExecutionSummary.getTestsFailedCount()).thenReturn(testsFailedCount);
        when(testExecutionSummary.getTestsAbortedCount()).thenReturn(testsAbortedCount);
        when(testExecutionSummary.getTestsSkippedCount()).thenReturn(testsSkippedCount);
        when(testExecutionSummary.getTimeStarted()).thenReturn(timeStarted);
        when(testExecutionSummary.getTimeFinished()).thenReturn(timeFinished);
        when(freeMarkerWrapper.interpolate(eq(condition), freeMarkerVarsArgumentCaptor.capture())).thenReturn(interpolatedCondition);
        when(MVEL.eval(eq(interpolatedCondition), mvelVarsArgumentCaptor.capture())).thenReturn(true);

        summary.sessionClosed();

        final Map<String, Object> vars = summary.getVars();

        assertEquals(12, vars.size());
        assertEquals(globalValue, vars.get(globalVar));
        assertEquals(testExecutionSummary, vars.get("summary"));
        assertEquals("00:04:10", vars.get("duration"));
        assertThat(String.valueOf(vars.get("timestamp")), matchesPattern("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}"));
        assertEquals(testsFoundCount, vars.get("total"));
        assertEquals((double) testsSucceededCount / testsFoundCount * 100, vars.get("successfulPercentage"));
        assertEquals((double) testsFailedCount / testsFoundCount * 100, vars.get("failedPercentage"));
        assertEquals((double) testsAbortedCount / testsFoundCount * 100, vars.get("abortedPercentage"));
        assertEquals((double) testsSkippedCount / testsFoundCount * 100, vars.get("disabledPercentage"));
        assertEquals(condition, vars.get("condition"));
        assertEquals(interpolatedCondition, vars.get("interpolatedCondition"));
        assertEquals(true, vars.get("executionSuccessful"));

        assertEquals(mvelVarsArgumentCaptor.getValue(), freeMarkerVarsArgumentCaptor.getValue());

        verify(reporter1).flush(summary);
        verify(reporter2).flush(summary);
    }

    @DisplayName("isExecutionSuccessful should evaluate the summary condition")
    @ParameterizedTest(name = "with condition evaluated to {0} we expect {0}")
    @ValueSource(booleans = {true, false})
    public void isExecutionSuccessful(final boolean expected) {
        final String condition = "condition";
        final String interpolatedCondition = "interpolatedCondition";

        Reflections.setField("condition", summary, condition);
        Reflections.setField("vars", summary, vars);

        when(freeMarkerWrapper.interpolate(condition, vars)).thenReturn(interpolatedCondition);
        when(MVEL.eval(interpolatedCondition, vars)).thenReturn(expected);

        assertEquals(expected, summary.isExecutionSuccessful());
    }
}
