package com.github.giulong.spectrum.utils.testbook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.giulong.spectrum.enums.Result;
import com.github.giulong.spectrum.pojos.testbook.QualityGate;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookStatistics.Statistics;
import com.github.giulong.spectrum.pojos.testbook.TestBookTest;
import com.github.giulong.spectrum.utils.testbook.parsers.TestBookParser;
import com.github.giulong.spectrum.utils.testbook.reporters.TestBookReporter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.github.giulong.spectrum.enums.Result.*;

@Getter
@Setter
@Slf4j
public class TestBook {

    private QualityGate qualityGate;

    private TestBookParser parser;

    private List<TestBookReporter> reporters;

    @JsonIgnore
    private final Map<String, TestBookTest> mappedTests = new HashMap<>();

    @JsonIgnore
    private final Map<String, TestBookTest> unmappedTests = new HashMap<>();

    @JsonIgnore
    private final Map<String, Set<TestBookTest>> groupedMappedTests = new HashMap<>();

    @JsonIgnore
    private final Map<String, Set<TestBookTest>> groupedUnmappedTests = new HashMap<>();

    @JsonIgnore
    private final TestBookStatistics statistics = new TestBookStatistics();

    @JsonIgnore
    private final Map<String, Object> vars = new HashMap<>();

    public TestBook() {
        final Map<Result, Statistics> totalCount = statistics.getTotalCount();
        final Map<Result, Statistics> grandTotalCount = statistics.getGrandTotalCount();
        final Map<Result, Statistics> totalWeightedCount = statistics.getTotalWeightedCount();
        final Map<Result, Statistics> grandTotalWeightedCount = statistics.getGrandTotalWeightedCount();

        Arrays
                .stream(Result.values())
                .forEach(result -> {
                    totalCount.put(result, new Statistics());
                    grandTotalCount.put(result, new Statistics());
                    totalWeightedCount.put(result, new Statistics());
                    grandTotalWeightedCount.put(result, new Statistics());
                });
    }

    public void mapVars() {
        final Map<Result, Statistics> totalCount = statistics.getTotalCount();
        final Map<Result, Statistics> grandTotalCount = statistics.getGrandTotalCount();
        final Map<Result, Statistics> totalWeightedCount = statistics.getTotalWeightedCount();
        final Map<Result, Statistics> grandTotalWeightedCount = statistics.getGrandTotalWeightedCount();

        vars.put("mappedTests", mappedTests);
        vars.put("unmappedTests", unmappedTests);
        vars.put("groupedMappedTests", groupedMappedTests);
        vars.put("groupedUnmappedTests", groupedUnmappedTests);
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

    public int getWeightedTotalOf(final Map<String, TestBookTest> tests) {
        return tests
                .values()
                .stream()
                .map(TestBookTest::getWeight)
                .reduce(0, Integer::sum);
    }

    public void flush(final int total, final Map<Result, Statistics> map) {
        final Statistics successful = map.get(SUCCESSFUL);
        final Statistics failed = map.get(FAILED);
        final Statistics aborted = map.get(ABORTED);
        final Statistics disabled = map.get(DISABLED);
        final Statistics notRun = map.get(NOT_RUN);

        final double totalSuccessful = successful.getTotal().doubleValue();
        final double totalFailed = failed.getTotal().doubleValue();
        final double totalAborted = aborted.getTotal().doubleValue();
        final double totalDisabled = disabled.getTotal().doubleValue();
        final double totalNotRun = total - totalSuccessful - totalFailed - totalAborted - totalDisabled;

        successful.getPercentage().set(totalSuccessful / total * 100);
        failed.getPercentage().set(totalFailed / total * 100);
        aborted.getPercentage().set(totalAborted / total * 100);
        disabled.getPercentage().set(totalDisabled / total * 100);
        notRun.getPercentage().set(totalNotRun / total * 100);
        notRun.getTotal().set((int) totalNotRun);
    }

    public void flush() {
        log.debug("Updating testBook percentages");

        final int testsTotal = mappedTests.size();
        final int unmappedTestsTotal = unmappedTests.size();
        final int weightedTestsTotal = getWeightedTotalOf(mappedTests);
        final int weightedTestsGrandTotal = weightedTestsTotal + getWeightedTotalOf(unmappedTests);

        statistics.getGrandTotal().set(testsTotal + unmappedTestsTotal);
        statistics.getTotalWeighted().set(weightedTestsTotal);
        statistics.getGrandTotalWeighted().set(weightedTestsGrandTotal);

        flush(testsTotal, statistics.getTotalCount());
        flush(testsTotal + unmappedTestsTotal, statistics.getGrandTotalCount());
        flush(weightedTestsTotal, statistics.getTotalWeightedCount());
        flush(weightedTestsGrandTotal, statistics.getGrandTotalWeightedCount());

        mapVars();
        TestBookReporter.evaluateQualityGateStatusFrom(this);
        reporters.forEach(reporter -> reporter.flush(this));
    }
}
