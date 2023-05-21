package com.giuliolongfils.spectrum.utils.testbook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.giuliolongfils.spectrum.enums.QualityGateStatus;
import com.giuliolongfils.spectrum.pojos.testbook.QualityGate;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookStatistics;
import com.giuliolongfils.spectrum.utils.testbook.parsers.TestBookParser;
import com.giuliolongfils.spectrum.utils.testbook.reporters.TestBookReporter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.giuliolongfils.spectrum.enums.QualityGateStatus.OK;
import static java.util.Locale.US;

@Getter
@Slf4j
public class TestBook {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(US));

    private QualityGate qualityGate;

    private TestBookParser parser;

    private List<TestBookReporter> reporters;

    @JsonIgnore
    private final Map<String, TestBookResult> tests = new HashMap<>();

    @JsonIgnore
    private final Map<String, TestBookResult> unmappedTests = new HashMap<>();

    @JsonIgnore
    private final TestBookStatistics statistics = new TestBookStatistics();

    @JsonIgnore
    private final Map<String, String> replacements = new HashMap<>();

    @JsonIgnore
    private QualityGateStatus qualityGateStatus = OK;

    public void mapReplacements() {
        final TestBookStatistics.Percentages percentages = statistics.getPercentages();

        replacements.put("{{qg.condition}}", qualityGate.getCondition());
        replacements.put("{{timestamp}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        replacements.put("{{grand-total}}", statistics.getGrandTotal().toString());
        replacements.put("{{tests-count}}", String.valueOf(tests.size()));
        replacements.put("{{unmapped-tests-count}}", String.valueOf(unmappedTests.size()));
        replacements.put("{{successful}}", statistics.getSuccessful().toString());
        replacements.put("{{failed}}", statistics.getFailed().toString());
        replacements.put("{{aborted}}", statistics.getAborted().toString());
        replacements.put("{{disabled}}", statistics.getDisabled().toString());
        replacements.put("{{not-run}}", statistics.getNotRun().toString());
        replacements.put("{{grand-total-successful}}", statistics.getGrandTotalSuccessful().toString());
        replacements.put("{{grand-total-failed}}", statistics.getGrandTotalFailed().toString());
        replacements.put("{{grand-total-aborted}}", statistics.getGrandTotalAborted().toString());
        replacements.put("{{grand-total-disabled}}", statistics.getGrandTotalDisabled().toString());
        replacements.put("{{grand-total-not-run}}", statistics.getGrandTotalNotRun().toString());
        replacements.put("{{percentages.tests}}", DECIMAL_FORMAT.format(percentages.getSuccessful()));
        replacements.put("{{percentages.unmapped-tests}}", DECIMAL_FORMAT.format(percentages.getFailed()));
        replacements.put("{{percentages.successful}}", DECIMAL_FORMAT.format(percentages.getSuccessful()));
        replacements.put("{{percentages.failed}}", DECIMAL_FORMAT.format(percentages.getFailed()));
        replacements.put("{{percentages.aborted}}", DECIMAL_FORMAT.format(percentages.getAborted()));
        replacements.put("{{percentages.disabled}}", DECIMAL_FORMAT.format(percentages.getDisabled()));
        replacements.put("{{percentages.not-run}}", DECIMAL_FORMAT.format(percentages.getNotRun()));
        replacements.put("{{percentages.grand-total-successful}}", DECIMAL_FORMAT.format(percentages.getGrandTotalSuccessful()));
        replacements.put("{{percentages.grand-total-failed}}", DECIMAL_FORMAT.format(percentages.getGrandTotalFailed()));
        replacements.put("{{percentages.grand-total-aborted}}", DECIMAL_FORMAT.format(percentages.getGrandTotalAborted()));
        replacements.put("{{percentages.grand-total-disabled}}", DECIMAL_FORMAT.format(percentages.getGrandTotalDisabled()));
        replacements.put("{{percentages.grand-total-not-run}}", DECIMAL_FORMAT.format(percentages.getGrandTotalNotRun()));
    }

    protected void updateQualityGateResult() {
        String qualityGateCondition = qualityGate.getCondition();
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            qualityGateCondition = qualityGateCondition.replace(entry.getKey(), entry.getValue());
        }

        qualityGateStatus = QualityGateStatus.fromValue(MVEL.evalToBoolean(qualityGateCondition, Map.of()));
        replacements.put("{{qg.status}}", qualityGateStatus.name());
    }

    public void flush() {
        final TestBookStatistics.Percentages percentages = statistics.getPercentages();
        final int total = tests.size();
        final int unmappedTestsTotal = unmappedTests.size();
        final int grandTotal = total + unmappedTestsTotal;
        log.debug("Updating testBook percentages");

        final double successful = statistics.getSuccessful().doubleValue();
        final double failed = statistics.getFailed().doubleValue();
        final double aborted = statistics.getAborted().doubleValue();
        final double disabled = statistics.getDisabled().doubleValue();
        final double grandTotalSuccessful = statistics.getGrandTotalSuccessful().doubleValue();
        final double grandTotalFailed = statistics.getGrandTotalFailed().doubleValue();
        final double grandTotalAborted = statistics.getGrandTotalAborted().doubleValue();
        final double grandTotalDisabled = statistics.getGrandTotalDisabled().doubleValue();

        statistics.getGrandTotal().set(grandTotal);
        statistics.getNotRun().set((int) (total - successful - failed - aborted - disabled));
        statistics.getGrandTotalNotRun().set((int) (grandTotal - grandTotalSuccessful - grandTotalFailed - grandTotalAborted - grandTotalDisabled));

        final double successfulPercentage = successful / total * 100;
        final double failedPercentage = failed / total * 100;
        final double abortedPercentage = aborted / total * 100;
        final double disabledPercentage = disabled / total * 100;
        final double notRunPercentage = statistics.getNotRun().doubleValue() / total * 100;
        final double grandTotalSuccessfulPercentage = grandTotalSuccessful / grandTotal * 100;
        final double grandTotalFailedPercentage = grandTotalFailed / grandTotal * 100;
        final double grandTotalAbortedPercentage = grandTotalAborted / grandTotal * 100;
        final double grandTotalDisabledPercentage = grandTotalDisabled / grandTotal * 100;
        final double grandTotalNotRunPercentage = statistics.getNotRun().doubleValue() / grandTotal * 100;

        percentages.setTests(total);
        percentages.setUnmappedTests(unmappedTestsTotal);
        percentages.setSuccessful(successfulPercentage);
        percentages.setFailed(failedPercentage);
        percentages.setAborted(abortedPercentage);
        percentages.setDisabled(disabledPercentage);
        percentages.setNotRun(notRunPercentage);
        percentages.setGrandTotalSuccessful(grandTotalSuccessfulPercentage);
        percentages.setGrandTotalFailed(grandTotalFailedPercentage);
        percentages.setGrandTotalAborted(grandTotalAbortedPercentage);
        percentages.setGrandTotalDisabled(grandTotalDisabledPercentage);
        percentages.setGrandTotalNotRun(grandTotalNotRunPercentage);

        log.debug("Percentages are: successful {}, failed {}, aborted {}, disabled {}, not run {}",
                successfulPercentage, failedPercentage, abortedPercentage, disabledPercentage, notRunPercentage);
        log.debug("Grand Total Percentages are: successful {}, failed {}, aborted {}, disabled {}, not run {}",
                grandTotalSuccessfulPercentage, grandTotalFailedPercentage, grandTotalAbortedPercentage, grandTotalDisabledPercentage, grandTotalNotRunPercentage);

        mapReplacements();
        updateQualityGateResult();

        reporters.forEach(reporter -> reporter.flush(this));
    }
}
