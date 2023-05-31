package com.github.giulong.spectrum.utils.testbook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.giulong.spectrum.enums.QualityGateStatus;
import com.github.giulong.spectrum.enums.TestBookResult;
import com.github.giulong.spectrum.pojos.testbook.QualityGate;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics.TestStatistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookTest;
import com.github.giulong.spectrum.utils.testbook.parsers.TestBookParser;
import com.github.giulong.spectrum.utils.testbook.reporters.TestBookReporter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.giulong.spectrum.enums.QualityGateStatus.OK;
import static com.github.giulong.spectrum.enums.TestBookResult.*;

@Getter
@Slf4j
public class TestBook {

    private QualityGate qualityGate;

    private TestBookParser parser;

    private List<TestBookReporter> reporters;

    @JsonIgnore
    private final Map<String, TestBookTest> tests = new HashMap<>();

    @JsonIgnore
    private final Map<String, TestBookTest> unmappedTests = new HashMap<>();

    @JsonIgnore
    private final TestBookStatistics statistics = new TestBookStatistics();

    @JsonIgnore
    private final Map<String, Object> vars = new HashMap<>();

    @SuppressWarnings("FieldMayBeFinal")
    @JsonIgnore
    private QualityGateStatus qualityGateStatus = OK;

    public TestBook() {
        Arrays
                .stream(TestBookResult.values())
                .forEach(result -> {
                    statistics.getTotalCount().put(result, new TestStatistics());
                    statistics.getGrandTotalCount().put(result, new TestStatistics());
                    statistics.getTotalWeightedCount().put(result, new TestStatistics());
                    statistics.getGrandTotalWeightedCount().put(result, new TestStatistics());
                });
    }

    public void mapVars() {
        final Map<TestBookResult, TestStatistics> totalCount = statistics.getTotalCount();
        final Map<TestBookResult, TestStatistics> grandTotalCount = statistics.getGrandTotalCount();
        final Map<TestBookResult, TestStatistics> totalWeightedCount = statistics.getTotalWeightedCount();
        final Map<TestBookResult, TestStatistics> grandTotalWeightedCount = statistics.getGrandTotalWeightedCount();

        vars.put("tests", tests);
        vars.put("unmappedTests", unmappedTests);
        vars.put("statistics", statistics);
        vars.put("qg", qualityGate);
        vars.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        vars.put("successful", totalCount.get(SUCCESSFUL));
        vars.put("failed", totalCount.get(FAILED));
        vars.put("aborted", totalCount.get(ABORTED));
        vars.put("disabled", totalCount.get(DISABLED));
        vars.put("notRun", totalCount.get(NOT_RUN));
        vars.put("grandSuccessful", grandTotalCount.get(SUCCESSFUL));
        vars.put("grandFailed", grandTotalCount.get(FAILED));
        vars.put("grandAborted", grandTotalCount.get(ABORTED));
        vars.put("grandDisabled", grandTotalCount.get(DISABLED));
        vars.put("grandNotRun", grandTotalCount.get(NOT_RUN));
        vars.put("weightedSuccessful", totalWeightedCount.get(SUCCESSFUL));
        vars.put("weightedFailed", totalWeightedCount.get(FAILED));
        vars.put("weightedAborted", totalWeightedCount.get(ABORTED));
        vars.put("weightedDisabled", totalWeightedCount.get(DISABLED));
        vars.put("weightedNotRun", totalWeightedCount.get(NOT_RUN));
        vars.put("grandWeightedSuccessful", grandTotalWeightedCount.get(SUCCESSFUL));
        vars.put("grandWeightedFailed", grandTotalWeightedCount.get(FAILED));
        vars.put("grandWeightedAborted", grandTotalWeightedCount.get(ABORTED));
        vars.put("grandWeightedDisabled", grandTotalWeightedCount.get(DISABLED));
        vars.put("grandWeightedNotRun", grandTotalWeightedCount.get(NOT_RUN));
    }

    public int getWeightedTotalOf(final Map<String, TestBookTest> testsMap) {
        return testsMap
                .values()
                .stream()
                .map(TestBookTest::getWeight)
                .reduce(0, Integer::sum);
    }

    public void flush(final int total, final Map<TestBookResult, TestStatistics> map) {
        statistics.getGrandTotalWeighted().set(total);

        final double totalSuccessful = map.get(SUCCESSFUL).getTotal().doubleValue();
        final double totalFailed = map.get(FAILED).getTotal().doubleValue();
        final double totalAborted = map.get(ABORTED).getTotal().doubleValue();
        final double totalDisabled = map.get(DISABLED).getTotal().doubleValue();
        final double totalNotRun = total - totalSuccessful - totalFailed - totalAborted - totalDisabled;

        map.get(NOT_RUN).getTotal().set((int) totalNotRun);

        map.get(SUCCESSFUL).getPercentage().set(totalSuccessful / total * 100);
        map.get(FAILED).getPercentage().set(totalFailed / total * 100);
        map.get(ABORTED).getPercentage().set(totalAborted / total * 100);
        map.get(DISABLED).getPercentage().set(totalDisabled / total * 100);
        map.get(NOT_RUN).getPercentage().set(totalNotRun / total * 100);
    }

    public void flush() {
        log.debug("Updating testBook percentages");

        final int testsTotal = tests.size();
        final int unmappedTestsTotal = unmappedTests.size();
        final int weightedTestsTotal = getWeightedTotalOf(tests);

        statistics.getGrandTotal().set(testsTotal + unmappedTestsTotal);
        statistics.getTotalWeighted().set(weightedTestsTotal);

        flush(testsTotal, statistics.getTotalCount());
        flush(testsTotal + unmappedTestsTotal, statistics.getGrandTotalCount());
        flush(weightedTestsTotal, statistics.getTotalWeightedCount());
        flush(weightedTestsTotal + getWeightedTotalOf(unmappedTests), statistics.getGrandTotalWeightedCount());

        mapVars();
        TestBookReporter.evaluateQualityGateStatusFrom(this);
        reporters.forEach(reporter -> reporter.flush(this));
    }
}
