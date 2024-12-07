package io.github.giulong.spectrum.utils.reporters;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.interfaces.reports.CanReportSummary;
import io.github.giulong.spectrum.interfaces.reports.CanReportTestBook;
import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class LogReporter extends Reporter {

    @JsonPropertyDescription("Path to the template to be used, relative to src/test/resources")
    @SuppressWarnings("unused")
    private String template;

    @Override
    public void doOutputFrom(final String interpolatedTemplate) {
        log.info("\n{}", interpolatedTemplate);
    }

    @Generated
    public static class LogTestBookReporter extends LogReporter implements CanReportTestBook {
    }

    @Generated
    public static class LogSummaryReporter extends LogReporter implements CanReportSummary {
    }
}
