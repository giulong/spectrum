package io.github.giulong.spectrum.it_testbook_verifier.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_testbook_verifier.data.Data;
import io.github.giulong.spectrum.it_testbook_verifier.pages.TestBookPage;
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
public class TestBookVerifierIT extends SpectrumTest<Data> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##", new DecimalFormatSymbols(US));

    private TestBookPage testBookPage;

    @Test
    @DisplayName("should check the testbook")
    public void testbook() {
        final Data.TestBook.Statistics statistics = data.getTestBook().getStatistics();

        webDriver.get(String.format("file:///%s/it-testbook/target/spectrum/testbook/testbook.html", Path.of(System.getProperty("user.dir")).getParent()));

        assertEquals("TestBook Results", testBookPage.getTitle().getText());

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

        assertEquals(mappedTests, testBookPage.getMappedTests().getText());
        assertEquals(unmappedTests, testBookPage.getUnmappedTests().getText());
        assertEquals(String.format("%s + %s = %s", mappedTests, unmappedTests, total), testBookPage.getGrandTotal().getText());
        assertEquals(totalWeighted, testBookPage.getTotalWeighted().getText());
        assertEquals(String.format("%s + %s = %s", totalWeighted, unmappedTests, grandTotalWeighted), testBookPage.getGrandTotalWeighted().getText());

        // MAPPED WEIGHTED
        final Data.TestBook.Statistics.Group mappedWeighted = statistics.getMappedWeighted();
        final String mappedWeightedSuccessful = mappedWeighted.getSuccessful();
        final String mappedWeightedFailed = mappedWeighted.getFailed();
        final String mappedWeightedAborted = mappedWeighted.getAborted();
        final String mappedWeightedDisabled = mappedWeighted.getDisabled();
        final String mappedWeightedNotRun = mappedWeighted.getNotRun();

        assertEquals(String.format("%s/%s", mappedWeightedSuccessful, totalWeighted), testBookPage.getWeightedSuccessful().getText());
        assertEquals(String.format("%s/%s", mappedWeightedFailed, totalWeighted), testBookPage.getWeightedFailed().getText());
        assertEquals(String.format("%s/%s", mappedWeightedAborted, totalWeighted), testBookPage.getWeightedAborted().getText());
        assertEquals(String.format("%s/%s", mappedWeightedDisabled, totalWeighted), testBookPage.getWeightedDisabled().getText());
        assertEquals(String.format("%s/%s", mappedWeightedNotRun, totalWeighted), testBookPage.getWeightedNotRun().getText());

        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedSuccessful) / totalWeightedInt * 100)), testBookPage.getWeightedSuccessfulPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedFailed) / totalWeightedInt * 100)), testBookPage.getWeightedFailedPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedAborted) / totalWeightedInt * 100)), testBookPage.getWeightedAbortedPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedDisabled) / totalWeightedInt * 100)), testBookPage.getWeightedDisabledPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedWeightedNotRun) / totalWeightedInt * 100)), testBookPage.getWeightedNotRunPercentage().getText().replace("%", ""));

        // GRAND TOTAL WEIGHTED
        final Data.TestBook.Statistics.Group grandTotalWeightedGroup = statistics.getGrandTotalWeighted();
        final String grandTotalWeightedSuccessful = grandTotalWeightedGroup.getSuccessful();
        final String grandTotalWeightedFailed = grandTotalWeightedGroup.getFailed();
        final String grandTotalWeightedAborted = grandTotalWeightedGroup.getAborted();
        final String grandTotalWeightedDisabled = grandTotalWeightedGroup.getDisabled();
        final String grandTotalWeightedNotRun = grandTotalWeightedGroup.getNotRun();

        assertEquals(String.format("%s/%s", grandTotalWeightedSuccessful, grandTotalWeighted), testBookPage.getGrandWeightedSuccessful().getText());
        assertEquals(String.format("%s/%s", grandTotalWeightedFailed, grandTotalWeighted), testBookPage.getGrandWeightedFailed().getText());
        assertEquals(String.format("%s/%s", grandTotalWeightedAborted, grandTotalWeighted), testBookPage.getGrandWeightedAborted().getText());
        assertEquals(String.format("%s/%s", grandTotalWeightedDisabled, grandTotalWeighted), testBookPage.getGrandWeightedDisabled().getText());
        assertEquals(String.format("%s/%s", grandTotalWeightedNotRun, grandTotalWeighted), testBookPage.getGrandWeightedNotRun().getText());

        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedSuccessful) / grandTotalWeightedInt * 100)), testBookPage.getGrandWeightedSuccessfulPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedFailed) / grandTotalWeightedInt * 100)), testBookPage.getGrandWeightedFailedPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedAborted) / grandTotalWeightedInt * 100)), testBookPage.getGrandWeightedAbortedPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedDisabled) / grandTotalWeightedInt * 100)), testBookPage.getGrandWeightedDisabledPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalWeightedNotRun) / grandTotalWeightedInt * 100)), testBookPage.getGrandWeightedNotRunPercentage().getText().replace("%", ""));

        // MAPPED
        final Data.TestBook.Statistics.Group mapped = statistics.getMapped();
        final String mappedSuccessful = mapped.getSuccessful();
        final String mappedFailed = mapped.getFailed();
        final String mappedAborted = mapped.getAborted();
        final String mappedDisabled = mapped.getDisabled();
        final String mappedNotRun = mapped.getNotRun();

        assertEquals(String.format("%s/%s", mappedSuccessful, mappedTests), testBookPage.getSuccessful().getText());
        assertEquals(String.format("%s/%s", mappedFailed, mappedTests), testBookPage.getFailed().getText());
        assertEquals(String.format("%s/%s", mappedAborted, mappedTests), testBookPage.getAborted().getText());
        assertEquals(String.format("%s/%s", mappedDisabled, mappedTests), testBookPage.getDisabled().getText());
        assertEquals(String.format("%s/%s", mappedNotRun, mappedTests), testBookPage.getNotRun().getText());

        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedSuccessful) / mappedTestsInt * 100)), testBookPage.getSuccessfulPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedFailed) / mappedTestsInt * 100)), testBookPage.getFailedPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedAborted) / mappedTestsInt * 100)), testBookPage.getAbortedPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedDisabled) / mappedTestsInt * 100)), testBookPage.getDisabledPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(mappedNotRun) / mappedTestsInt * 100)), testBookPage.getNotRunPercentage().getText().replace("%", ""));

        // GRAND TOTAL
        final Data.TestBook.Statistics.Group grandTotal = statistics.getGrandTotal();
        final String grandTotalSuccessful = grandTotal.getSuccessful();
        final String grandTotalFailed = grandTotal.getFailed();
        final String grandTotalAborted = grandTotal.getAborted();
        final String grandTotalDisabled = grandTotal.getDisabled();
        final String grandTotalNotRun = grandTotal.getNotRun();

        assertEquals(String.format("%s/%s", grandTotalSuccessful, total), testBookPage.getGrandSuccessful().getText());
        assertEquals(String.format("%s/%s", grandTotalFailed, total), testBookPage.getGrandFailed().getText());
        assertEquals(String.format("%s/%s", grandTotalAborted, total), testBookPage.getGrandAborted().getText());
        assertEquals(String.format("%s/%s", grandTotalDisabled, total), testBookPage.getGrandDisabled().getText());
        assertEquals(String.format("%s/%s", grandTotalNotRun, total), testBookPage.getGrandNotRun().getText());

        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalSuccessful) / totalInt * 100)), testBookPage.getGrandSuccessfulPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalFailed) / totalInt * 100)), testBookPage.getGrandFailedPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalAborted) / totalInt * 100)), testBookPage.getGrandAbortedPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalDisabled) / totalInt * 100)), testBookPage.getGrandDisabledPercentage().getText().replace("%", ""));
        assertEquals(String.valueOf(DECIMAL_FORMAT.format(parseDouble(grandTotalNotRun) / totalInt * 100)), testBookPage.getGrandNotRunPercentage().getText().replace("%", ""));

        // QUALITY GATE
        final Data.TestBook.Qg qg = data.getTestBook().getQg();
        assertTrue(hasClass(testBookPage.getQgStatus(), String.format("qg-status-%s", qg.getStatus())));
        assertEquals(qg.getCondition(), testBookPage.getCondition().getText());
        assertEquals(qg.getEvaluatedCondition(), testBookPage.getEvaluatedCondition().getText());
    }
}
