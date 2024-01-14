package io.github.giulong.spectrum.utils.reporters;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.interfaces.reports.CanReportSummary;
import io.github.giulong.spectrum.interfaces.reports.CanReportTestBook;
import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@SuppressWarnings("unused")
public abstract class LogReporter extends Reporter {

    @JsonPropertyDescription("Path to the template to be used, relative to src/test/resources")
    private String template;

    @Override
    public void doOutputFrom(final String interpolatedTemplate) {
        log.info("\n{}", interpolatedTemplate);
    }

    @Override
    public void cleanupOldReports() {
        log.debug("NoOp cleanupOldReports");
    }

    @Generated
    @SuppressWarnings("checkstyle:WhitespaceAround")
    public static class LogTestBookReporter extends LogReporter implements CanReportTestBook {}

    @Generated
    @SuppressWarnings("checkstyle:WhitespaceAround")
    public static class LogSummaryReporter extends LogReporter implements CanReportSummary {}
}
