package io.github.giulong.spectrum.utils.testbook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.enums.Result;
import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.interfaces.reports.CanReportTestBook;
import io.github.giulong.spectrum.interfaces.reports.Reportable;
import io.github.giulong.spectrum.pojos.testbook.QualityGate;
import io.github.giulong.spectrum.pojos.testbook.TestBookStatistics;
import io.github.giulong.spectrum.pojos.testbook.TestBookStatistics.Statistics;
import io.github.giulong.spectrum.pojos.testbook.TestBookTest;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.Vars;
import io.github.giulong.spectrum.utils.reporters.FileReporter;
import io.github.giulong.spectrum.utils.testbook.parsers.TestBookParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.github.giulong.spectrum.enums.Result.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Getter
@Slf4j
public class TestBook implements SessionHook, Reportable {

    @JsonIgnore
    private final FileUtils fileUtils = FileUtils.getInstance();

    @JsonPropertyDescription("Enables the testBook")
    @SuppressWarnings("unused")
    private boolean enabled;

    @JsonPropertyDescription("Quality Gate to be evaluated to consider the execution successful")
    @SuppressWarnings("unused")
    private QualityGate qualityGate;

    @JsonPropertyDescription("Object specifying the kind of testBook provided")
    @SuppressWarnings("unused")
    private TestBookParser parser;

    @JsonPropertyDescription("List of testBook reporters that will produce the execution report in specific formats")
    @SuppressWarnings("unused")
    private List<CanReportTestBook> reporters;

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

    @Override
    public void sessionOpened() {
        if (!enabled) {
            log.debug("TestBook disabled. Skipping parse");
            return;
        }

        reporters
                .stream()
                .filter(canReportTestBook -> canReportTestBook instanceof FileReporter)
                .map(FileReporter.class::cast)
                .map(FileReporter::getOutput)
                .forEach(output -> {
                    final String reportPath = Path.of(output).toAbsolutePath().toString().replace("\\", "/");
                    log.info("After the execution, you'll find the {} testBook at file:///{}", fileUtils.getExtensionOf(output), reportPath);
                });

        final List<TestBookTest> tests = parser.parse();

        mappedTests.putAll(tests
                .stream()
                .collect(toMap(test -> String.format("%s %s", test.getClassName(), test.getTestName()), identity())));

        tests.forEach(test -> updateGroupedTests(groupedMappedTests, test.getClassName(), test));
    }

    @Override
    public void sessionClosed() {
        if (!enabled) {
            log.debug("Testbook disabled. Skipping flush");
            return;
        }

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

        final Map<Result, Statistics> totalCount = statistics.getTotalCount();
        final Map<Result, Statistics> grandTotalCount = statistics.getGrandTotalCount();
        final Map<Result, Statistics> totalWeightedCount = statistics.getTotalWeightedCount();
        final Map<Result, Statistics> grandTotalWeightedCount = statistics.getGrandTotalWeightedCount();

        vars.putAll(Vars.getInstance());
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

        final String qgStatus = "qgStatus";
        final String interpolatedQgStatus = FreeMarkerWrapper.getInstance().interpolate(qualityGate.getCondition(), vars);
        vars.put(qgStatus, interpolatedQgStatus);
        Vars.getInstance().put(qgStatus, interpolatedQgStatus);

        reporters.forEach(reporter -> reporter.flush(this));
    }

    protected void updateGroupedTests(final Map<String, Set<TestBookTest>> groupedTests, final String className, final TestBookTest test) {
        final Set<TestBookTest> tests = groupedTests.getOrDefault(className, new HashSet<>());
        tests.add(test);
        groupedTests.put(className, tests);
    }

    public void updateWithResult(final String className, final String testName, final Result result) {
        if (!enabled) {
            log.debug("TestBook disabled. Skipping consumer");
            return;
        }

        final String fullName = String.format("%s %s", className, testName);

        statistics.getGrandTotalCount().get(result).getTotal().incrementAndGet();

        if (mappedTests.containsKey(fullName)) {
            log.debug("Setting TestBook result {} for test '{}'", result, fullName);
            final TestBookTest actualTest = mappedTests.get(fullName);
            final int weight = actualTest.getWeight();

            actualTest.setResult(result);
            statistics.getTotalCount().get(result).getTotal().incrementAndGet();
            statistics.getTotalWeightedCount().get(result).getTotal().addAndGet(weight);
            statistics.getGrandTotalWeightedCount().get(result).getTotal().addAndGet(weight);
            updateGroupedTests(groupedMappedTests, className, actualTest);
        } else {
            final TestBookTest unmappedTest = TestBookTest.builder()
                    .className(className)
                    .testName(testName)
                    .result(result)
                    .build();

            log.debug("Setting TestBook result {} for unmapped test '{}'", result, unmappedTest);
            unmappedTests.put(fullName, unmappedTest);
            statistics.getGrandTotalWeightedCount().get(result).getTotal().incrementAndGet();
            updateGroupedTests(groupedUnmappedTests, className, unmappedTest);
        }
    }

    public int getWeightedTotalOf(final Map<String, TestBookTest> tests) {
        return tests
                .values()
                .stream()
                .map(TestBookTest::getWeight)
                .reduce(0, Integer::sum);
    }

    protected void flush(final int total, final Map<Result, Statistics> map) {
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
}
