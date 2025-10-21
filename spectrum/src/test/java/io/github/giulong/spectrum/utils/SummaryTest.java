package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.MockSingleton;
import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.interfaces.reports.CanReportSummary;
import io.github.giulong.spectrum.utils.reporters.FileReporter;
import io.github.giulong.spectrum.utils.reporters.Reporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.mockito.*;
import org.mvel2.MVEL;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.enums.Result.FAILED;
import static io.github.giulong.spectrum.enums.Result.SUCCESSFUL;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class SummaryTest {

    private static MockedStatic<MVEL> mvelMockedStatic;

    @MockSingleton
    @SuppressWarnings("unused")
    private FreeMarkerWrapper freeMarkerWrapper;

    @MockSingleton
    @SuppressWarnings("unused")
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
    void beforeEach() {
        Reflections.setField("summaryGeneratingListener", summary, summaryGeneratingListener);

        mvelMockedStatic = mockStatic(MVEL.class);
    }

    @AfterEach
    void afterEach() {
        mvelMockedStatic.close();
        Vars.getInstance().clear();
    }

    @Test
    @DisplayName("sessionOpened should log where to find the summary for each reporter")
    void sessionOpened() {
        final String output = "output";
        final String extension = "extension";

        Reflections.setField("reporters", summary, List.of(reporter1, reporter2));

        when(fileUtils.getExtensionOf(output)).thenReturn(extension);
        when(reporter1.getOutput()).thenReturn(output);

        summary.sessionOpened();
    }

    @Test
    @DisplayName("sessionClosed should put the summary in the vars and flush each reporter")
    void sessionClosed() {
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

        final Map<String, Object> localVars = summary.getVars();

        assertEquals(12, localVars.size());
        assertEquals(globalValue, localVars.get(globalVar));
        assertEquals(testExecutionSummary, localVars.get("summary"));
        assertEquals("00:04:10", localVars.get("duration"));
        assertThat(String.valueOf(localVars.get("timestamp")), matchesPattern("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}"));
        assertEquals(testsFoundCount, localVars.get("total"));
        assertEquals((double) testsSucceededCount / testsFoundCount * 100, localVars.get("successfulPercentage"));
        assertEquals((double) testsFailedCount / testsFoundCount * 100, localVars.get("failedPercentage"));
        assertEquals((double) testsAbortedCount / testsFoundCount * 100, localVars.get("abortedPercentage"));
        assertEquals((double) testsSkippedCount / testsFoundCount * 100, localVars.get("disabledPercentage"));
        assertEquals(condition, localVars.get("condition"));
        assertEquals(interpolatedCondition, localVars.get("interpolatedCondition"));
        assertEquals(true, localVars.get("executionSuccessful"));

        assertEquals(mvelVarsArgumentCaptor.getValue(), freeMarkerVarsArgumentCaptor.getValue());

        verify(reporter1).flush(summary);
        verify(reporter2).flush(summary);
    }

    @DisplayName("isExecutionSuccessful should evaluate the summary condition")
    @ParameterizedTest(name = "with condition evaluated to {0} we expect {0}")
    @ValueSource(booleans = {true, false})
    void isExecutionSuccessful(final boolean expected) {
        final String condition = "condition";
        final String interpolatedCondition = "interpolatedCondition";

        Reflections.setField("condition", summary, condition);
        Reflections.setField("vars", summary, vars);

        when(freeMarkerWrapper.interpolate(condition, vars)).thenReturn(interpolatedCondition);
        when(MVEL.eval(interpolatedCondition, vars)).thenReturn(expected);

        assertEquals(expected, summary.isExecutionSuccessful());
    }

    @DisplayName("toResult should map the execution to the Result enum")
    @ParameterizedTest(name = "with execution successful {0} we expect {0}")
    @MethodSource("valuesProvider")
    void toResult(final boolean successful, final Result expected) {
        final String condition = "condition";
        final String interpolatedCondition = "interpolatedCondition";

        Reflections.setField("condition", summary, condition);
        Reflections.setField("vars", summary, vars);

        when(freeMarkerWrapper.interpolate(condition, vars)).thenReturn(interpolatedCondition);
        when(MVEL.eval(interpolatedCondition, vars)).thenReturn(successful);

        assertEquals(expected, summary.toResult());
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(true, SUCCESSFUL),
                arguments(false, FAILED)
        );
    }
}
