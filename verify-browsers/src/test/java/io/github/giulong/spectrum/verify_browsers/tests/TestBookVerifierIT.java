package io.github.giulong.spectrum.verify_browsers.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.TestBookPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("It TestBook Module Verifier")
@SuppressWarnings("unused")
class TestBookVerifierIT extends SpectrumTest<Data> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##", new DecimalFormatSymbols(US));

    private TestBookPage testBookPage;

    @Test
    @DisplayName("should check the testbook")
    void testbook() {
        final Data.TestBook.Statistics statistics = data.getTestBook().getStatistics();

        driver.get(String.format("file:///%s/it-testbook/target/spectrum/testbook/testbook.html", Path.of(System.getProperty("user.dir")).getParent()));

        assertEquals(data.getTestBook().getTitle(), testBookPage.getTitle().getText());

        // STATISTICS
        final Data.TestBook.Statistics.Generic generic = statistics.getGeneric();
        final String mappedTests = generic.getMappedTests();
        final int mappedTestsInt = parseInt(generic.getMappedTests());
        final String unmappedTests = generic.getUnmappedTests();
        final String totalWeighted = generic.getTotalWeighted();
        final int totalWeightedInt = parseInt(totalWeighted);
        final int totalInt = mappedTestsInt + parseInt(unmappedTests);
        final String total = String.valueOf(mappedTestsInt + parseInt(unmappedTests));
        final int grandTotalWeightedInt = totalWeightedInt + parseInt(unmappedTests);
        final String grandTotalWeighted = String.valueOf(grandTotalWeightedInt);

        assertEquals(mappedTests, testBookPage.getMappedTests().getText(), "mapped tests");
        assertEquals(unmappedTests, testBookPage.getUnmappedTests().getText(), "unmapped tests");
        assertEquals(String.format("%s + %s = %s", mappedTests, unmappedTests, total), testBookPage.getGrandTotal().getText(), "grand total");
        assertEquals(totalWeighted, testBookPage.getTotalWeighted().getText(), "total weighted");
        assertEquals(String.format("%s + %s = %s", totalWeighted, unmappedTests, grandTotalWeighted), testBookPage.getGrandTotalWeighted().getText(), "grand total weighted");

        // MAPPED WEIGHTED
        final Data.TestBook.Statistics.Group mappedWeighted = statistics.getMappedWeighted();
        final String mappedWeightedSuccessful = mappedWeighted.getSuccessful();
        final String mappedWeightedFailed = mappedWeighted.getFailed();
        final String mappedWeightedAborted = mappedWeighted.getAborted();
        final String mappedWeightedDisabled = mappedWeighted.getDisabled();
        final String mappedWeightedNotRun = mappedWeighted.getNotRun();

        assertEquals(String.format("%s/%s", mappedWeightedSuccessful, totalWeighted), testBookPage.getWeightedSuccessful().getText(), "weighted successful");
        assertEquals(String.format("%s/%s", mappedWeightedFailed, totalWeighted), testBookPage.getWeightedFailed().getText(), "weighted failed");
        assertEquals(String.format("%s/%s", mappedWeightedAborted, totalWeighted), testBookPage.getWeightedAborted().getText(), "weighted aborted");
        assertEquals(String.format("%s/%s", mappedWeightedDisabled, totalWeighted), testBookPage.getWeightedDisabled().getText(), "weighted disabled");
        assertEquals(String.format("%s/%s", mappedWeightedNotRun, totalWeighted), testBookPage.getWeightedNotRun().getText(), "weighted not run");

        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedSuccessful) / totalWeightedInt * 100)),
                testBookPage.getWeightedSuccessfulPercentage().getText().replace("%", ""), "weighted successful percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedFailed) / totalWeightedInt * 100)),
                testBookPage.getWeightedFailedPercentage().getText().replace("%", ""), "weighted failed percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedAborted) / totalWeightedInt * 100)),
                testBookPage.getWeightedAbortedPercentage().getText().replace("%", ""), "weighted aborted percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedDisabled) / totalWeightedInt * 100)),
                testBookPage.getWeightedDisabledPercentage().getText().replace("%", ""), "weighted disabled percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedNotRun) / totalWeightedInt * 100)),
                testBookPage.getWeightedNotRunPercentage().getText().replace("%", ""), "weighted not run percentage");

        // GRAND TOTAL WEIGHTED
        final Data.TestBook.Statistics.Group grandTotalWeightedGroup = statistics.getGrandTotalWeighted();
        final String grandTotalWeightedSuccessful = grandTotalWeightedGroup.getSuccessful();
        final String grandTotalWeightedFailed = grandTotalWeightedGroup.getFailed();
        final String grandTotalWeightedAborted = grandTotalWeightedGroup.getAborted();
        final String grandTotalWeightedDisabled = grandTotalWeightedGroup.getDisabled();
        final String grandTotalWeightedNotRun = grandTotalWeightedGroup.getNotRun();

        assertEquals(String.format("%s/%s", grandTotalWeightedSuccessful, grandTotalWeighted),
                testBookPage.getGrandWeightedSuccessful().getText(), "grand weighted successful");
        assertEquals(String.format("%s/%s", grandTotalWeightedFailed, grandTotalWeighted),
                testBookPage.getGrandWeightedFailed().getText(), "grand weighted failed");
        assertEquals(String.format("%s/%s", grandTotalWeightedAborted, grandTotalWeighted),
                testBookPage.getGrandWeightedAborted().getText(), "grand weighted aborted");
        assertEquals(String.format("%s/%s", grandTotalWeightedDisabled, grandTotalWeighted),
                testBookPage.getGrandWeightedDisabled().getText(), "grand weighted disabled");
        assertEquals(String.format("%s/%s", grandTotalWeightedNotRun, grandTotalWeighted),
                testBookPage.getGrandWeightedNotRun().getText(), "grand weighted not run");

        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedSuccessful) / grandTotalWeightedInt * 100)),
                testBookPage.getGrandWeightedSuccessfulPercentage().getText().replace("%", ""), "grand weighted successful percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedFailed) / grandTotalWeightedInt * 100)),
                testBookPage.getGrandWeightedFailedPercentage().getText().replace("%", ""), "grand weighted failed percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedAborted) / grandTotalWeightedInt * 100)),
                testBookPage.getGrandWeightedAbortedPercentage().getText().replace("%", ""), "grand weighted aborted percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedDisabled) / grandTotalWeightedInt * 100)),
                testBookPage.getGrandWeightedDisabledPercentage().getText().replace("%", ""), "grand weighted disabled percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedNotRun) / grandTotalWeightedInt * 100)),
                testBookPage.getGrandWeightedNotRunPercentage().getText().replace("%", ""), "grand weighted not run percentage");

        // MAPPED
        final Data.TestBook.Statistics.Group mapped = statistics.getMapped();
        final String mappedSuccessful = mapped.getSuccessful();
        final String mappedFailed = mapped.getFailed();
        final String mappedAborted = mapped.getAborted();
        final String mappedDisabled = mapped.getDisabled();
        final String mappedNotRun = mapped.getNotRun();

        assertEquals(String.format("%s/%s", mappedSuccessful, mappedTests), testBookPage.getSuccessful().getText(), "mapped successful");
        assertEquals(String.format("%s/%s", mappedFailed, mappedTests), testBookPage.getFailed().getText(), "mapped failed");
        assertEquals(String.format("%s/%s", mappedAborted, mappedTests), testBookPage.getAborted().getText(), "mapped aborted");
        assertEquals(String.format("%s/%s", mappedDisabled, mappedTests), testBookPage.getDisabled().getText(), "mapped disabled");
        assertEquals(String.format("%s/%s", mappedNotRun, mappedTests), testBookPage.getNotRun().getText(), "mapped not run");

        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedSuccessful) / mappedTestsInt * 100)),
                testBookPage.getSuccessfulPercentage().getText().replace("%", ""), "successful percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedFailed) / mappedTestsInt * 100)),
                testBookPage.getFailedPercentage().getText().replace("%", ""), "failed percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedAborted) / mappedTestsInt * 100)),
                testBookPage.getAbortedPercentage().getText().replace("%", ""), "aborted percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedDisabled) / mappedTestsInt * 100)),
                testBookPage.getDisabledPercentage().getText().replace("%", ""), "disabled percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedNotRun) / mappedTestsInt * 100)),
                testBookPage.getNotRunPercentage().getText().replace("%", ""), "not run percentage");

        // GRAND TOTAL
        final Data.TestBook.Statistics.Group grandTotal = statistics.getGrandTotal();
        final String grandTotalSuccessful = grandTotal.getSuccessful();
        final String grandTotalFailed = grandTotal.getFailed();
        final String grandTotalAborted = grandTotal.getAborted();
        final String grandTotalDisabled = grandTotal.getDisabled();
        final String grandTotalNotRun = grandTotal.getNotRun();

        assertEquals(String.format("%s/%s", grandTotalSuccessful, total), testBookPage.getGrandSuccessful().getText(), "grand successful");
        assertEquals(String.format("%s/%s", grandTotalFailed, total), testBookPage.getGrandFailed().getText(), "grand failed");
        assertEquals(String.format("%s/%s", grandTotalAborted, total), testBookPage.getGrandAborted().getText(), "grand aborted");
        assertEquals(String.format("%s/%s", grandTotalDisabled, total), testBookPage.getGrandDisabled().getText(), "grand disabled");
        assertEquals(String.format("%s/%s", grandTotalNotRun, total), testBookPage.getGrandNotRun().getText(), "grand not run");

        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalSuccessful) / totalInt * 100)),
                testBookPage.getGrandSuccessfulPercentage().getText().replace("%", ""), "grande successful percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalFailed) / totalInt * 100)),
                testBookPage.getGrandFailedPercentage().getText().replace("%", ""), "grande failed percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalAborted) / totalInt * 100)),
                testBookPage.getGrandAbortedPercentage().getText().replace("%", ""), "grande aborted percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalDisabled) / totalInt * 100)),
                testBookPage.getGrandDisabledPercentage().getText().replace("%", ""), "grande disabled percentage");
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalNotRun) / totalInt * 100)),
                testBookPage.getGrandNotRunPercentage().getText().replace("%", ""), "grande not run percentage");

        // QUALITY GATE
        final Data.TestBook.Qg qg = data.getTestBook().getQg();
        assertTrue(hasClass(testBookPage.getQgStatus(), String.format("qg-status-%s", qg.getStatus())), "QG status");
        assertEquals(qg.getCondition(), testBookPage.getCondition().getText(), "QG condition");
        assertEquals(qg.getEvaluatedCondition(), testBookPage.getEvaluatedCondition().getText(), "QG evaluated condition");
    }
}
