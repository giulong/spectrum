package io.github.giulong.spectrum.verify_browsers.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.verify_browsers.data.Data;
import io.github.giulong.spectrum.verify_browsers.pages.SummaryPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("It Summary Module Verifier")
@SuppressWarnings("unused")
public class SummaryVerifierIT extends SpectrumTest<Data> {

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4} \\w{3} \\d{1,2} \\d{2}:\\d{2}:\\d{2}");
    private static final Pattern DURATION_PATTERN = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");

    private SummaryPage summaryPage;

    @Test
    @DisplayName("should check the testbook")
    public void testbook() {
        final Data.Summary summary = data.getSummary();

        webDriver.get(String.format("file:///%s/it-testbook/target/spectrum/summary/summary.html", Path.of(System.getProperty("user.dir")).getParent()));

        assertEquals(summary.getTitle(), summaryPage.getTitle().getText());
        assertEquals(summary.getSuccessfulCount(), summaryPage.getSuccessfulCount().getText());
        assertEquals(summary.getSuccessfulPercentage(), summaryPage.getSuccessfulPercentage().getText());
        assertEquals(summary.getFailedCount(), summaryPage.getFailedCount().getText());
        assertEquals(summary.getFailedPercentage(), summaryPage.getFailedPercentage().getText());
        assertEquals(summary.getAbortedCount(), summaryPage.getAbortedCount().getText());
        assertEquals(summary.getAbortedPercentage(), summaryPage.getAbortedPercentage().getText());
        assertEquals(summary.getDisabledCount(), summaryPage.getDisabledCount().getText());
        assertEquals(summary.getDisabledPercentage(), summaryPage.getDisabledPercentage().getText());

        assertThat(summaryPage.getStartedAt().getText(), matchesPattern("Started at:\\s*" + DATE_PATTERN));
        assertThat(summaryPage.getEndedAt().getText(), matchesPattern("Ended at:\\s*" + DATE_PATTERN));
        assertThat(summaryPage.getDuration().getText(), matchesPattern("Duration:\\s*" + DURATION_PATTERN));

        assertEquals(summary.getCondition(), summaryPage.getCondition().getText());
        assertEquals(summary.getInterpolatedCondition(), summaryPage.getInterpolatedCondition().getText());
        assertEquals(summary.getConditionStatus(), summaryPage.getConditionStatus().getText());
    }
}
