package com.github.giulong.spectrum.utils.testbook;

import com.github.giulong.spectrum.enums.Result;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics.Statistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookTest;
import com.github.giulong.spectrum.utils.testbook.reporters.TestBookReporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.giulong.spectrum.enums.Result.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestBook")
class TestBookUnitTest {

    private MockedStatic<TestBookReporter> testBookReporterMockedStatic;

    @Mock
    private TestBookReporter reporter1;

    @Mock
    private TestBookReporter reporter2;

    @InjectMocks
    private TestBook testBook;

    @BeforeEach
    public void beforeEach() {
        testBookReporterMockedStatic = mockStatic(TestBookReporter.class);
    }

    @AfterEach
    public void afterEach() {
        testBookReporterMockedStatic.close();
    }

    private void mapVarsAssertions() {
        final Map<String, Object> vars = testBook.getVars();
        final TestBookStatistics statistics = testBook.getStatistics();
        final Map<Result, Statistics> totalCount = statistics.getTotalCount();
        final Map<Result, Statistics> grandTotalCount = statistics.getGrandTotalCount();
        final Map<Result, Statistics> totalWeightedCount = statistics.getTotalWeightedCount();
        final Map<Result, Statistics> grandTotalWeightedCount = statistics.getGrandTotalWeightedCount();

        assertEquals(27, vars.size());
        assertSame(testBook.getMappedTests(), vars.get("mappedTests"));
        assertSame(testBook.getUnmappedTests(), vars.get("unmappedTests"));
        assertSame(testBook.getGroupedMappedTests(), vars.get("groupedMappedTests"));
        assertSame(testBook.getGroupedUnmappedTests(), vars.get("groupedUnmappedTests"));
        assertSame(testBook.getStatistics(), vars.get("statistics"));
        assertSame(testBook.getQualityGate(), vars.get("qg"));
        assertThat(vars.get("timestamp").toString(), matchesPattern("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}"));
        assertSame(totalCount.get(SUCCESSFUL), vars.get("successful"));
        assertSame(totalCount.get(FAILED), vars.get("failed"));
        assertSame(totalCount.get(ABORTED), vars.get("aborted"));
        assertSame(totalCount.get(DISABLED), vars.get("disabled"));
        assertSame(totalCount.get(NOT_RUN), vars.get("notRun"));
        assertSame(grandTotalCount.get(SUCCESSFUL), vars.get("grandSuccessful"));
        assertSame(grandTotalCount.get(FAILED), vars.get("grandFailed"));
        assertSame(grandTotalCount.get(ABORTED), vars.get("grandAborted"));
        assertSame(grandTotalCount.get(DISABLED), vars.get("grandDisabled"));
        assertSame(grandTotalCount.get(NOT_RUN), vars.get("grandNotRun"));
        assertSame(totalWeightedCount.get(SUCCESSFUL), vars.get("weightedSuccessful"));
        assertSame(totalWeightedCount.get(FAILED), vars.get("weightedFailed"));
        assertSame(totalWeightedCount.get(ABORTED), vars.get("weightedAborted"));
        assertSame(totalWeightedCount.get(DISABLED), vars.get("weightedDisabled"));
        assertSame(totalWeightedCount.get(NOT_RUN), vars.get("weightedNotRun"));
        assertSame(grandTotalWeightedCount.get(SUCCESSFUL), vars.get("grandWeightedSuccessful"));
        assertSame(grandTotalWeightedCount.get(FAILED), vars.get("grandWeightedFailed"));
        assertSame(grandTotalWeightedCount.get(ABORTED), vars.get("grandWeightedAborted"));
        assertSame(grandTotalWeightedCount.get(DISABLED), vars.get("grandWeightedDisabled"));
        assertSame(grandTotalWeightedCount.get(NOT_RUN), vars.get("grandWeightedNotRun"));
    }

    @Test
    @DisplayName("mapVars should put all the needed vars to interpolate templates correctly")
    public void mapVars() {
        testBook.mapVars();
        mapVarsAssertions();
    }

    @Test
    @DisplayName("getWeightedTotalOf should return the sum of the weights of the provided map")
    public void getWeightedTotalOf() {
        final TestBookTest test1 = TestBookTest.builder()
                .className("test 1")
                .testName("one")
                .build();

        final TestBookTest test2 = TestBookTest.builder()
                .className("another test")
                .testName("another")
                .weight(8)
                .build();

        final TestBookTest test3 = TestBookTest.builder()
                .className("three")
                .testName("name")
                .weight(3)
                .build();

        assertEquals(test1.getWeight() + test2.getWeight() + test3.getWeight(), testBook.getWeightedTotalOf(Map.of("a", test1, "b", test2, "c", test3)));
    }

    @Test
    @DisplayName("flush should update the statistics of the provided tests map")
    public void flush() {
        final Map<Result, Statistics> statistics = new HashMap<>();
        final int totalSuccessful = 1;
        final int totalFailed = 2;
        final int totalAborted = 3;
        final int totalDisabled = 4;
        final int total = 123;
        final int totalNotRun = total - totalSuccessful - totalFailed - totalAborted - totalDisabled;

        statistics.put(SUCCESSFUL, new Statistics());
        statistics.put(FAILED, new Statistics());
        statistics.put(ABORTED, new Statistics());
        statistics.put(DISABLED, new Statistics());
        statistics.put(NOT_RUN, new Statistics());

        statistics.get(SUCCESSFUL).getTotal().set(totalSuccessful);
        statistics.get(FAILED).getTotal().set(totalFailed);
        statistics.get(ABORTED).getTotal().set(totalAborted);
        statistics.get(DISABLED).getTotal().set(totalDisabled);

        testBook.flush(total, statistics);

        assertEquals((double) totalSuccessful / total * 100, statistics.get(SUCCESSFUL).getPercentage().get());
        assertEquals((double) totalFailed / total * 100, statistics.get(FAILED).getPercentage().get());
        assertEquals((double) totalAborted / total * 100, statistics.get(ABORTED).getPercentage().get());
        assertEquals((double) totalDisabled / total * 100, statistics.get(DISABLED).getPercentage().get());
        assertEquals((double) totalNotRun / total * 100, statistics.get(NOT_RUN).getPercentage().get());
        assertEquals(totalNotRun, statistics.get(NOT_RUN).getTotal().get());
    }

    @Test
    @DisplayName("flush should")
    public void flushAll() {
        testBook.setReporters(List.of(reporter1, reporter2));
        testBook.getMappedTests().put("a", TestBookTest.builder().weight(1).build());
        testBook.getUnmappedTests().put("b", TestBookTest.builder().weight(2).build());
        testBook.getUnmappedTests().put("c", TestBookTest.builder().weight(3).build());

        testBook.flush();

        assertEquals(3, testBook.getStatistics().getGrandTotal().get());
        assertEquals(1, testBook.getStatistics().getTotalWeighted().get());
        assertEquals(6, testBook.getStatistics().getGrandTotalWeighted().get());

        // since NOT_RUN total is set in the overloaded flush method, we can assert it to indirectly check that method is called
        assertEquals(1, testBook.getStatistics().getTotalCount().get(NOT_RUN).getTotal().get());
        assertEquals(3, testBook.getStatistics().getGrandTotalCount().get(NOT_RUN).getTotal().get());
        assertEquals(1, testBook.getStatistics().getTotalWeightedCount().get(NOT_RUN).getTotal().get());
        assertEquals(6, testBook.getStatistics().getGrandTotalWeightedCount().get(NOT_RUN).getTotal().get());

        mapVarsAssertions();
        testBookReporterMockedStatic.verify(() -> TestBookReporter.evaluateQualityGateStatusFrom(testBook));
        verify(reporter1).flush(testBook);
        verify(reporter2).flush(testBook);
    }
}
