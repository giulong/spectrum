package io.github.giulong.spectrum.utils.summary.reporters;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class LogSummaryReporter extends SummaryReporter {

    @SuppressWarnings("FieldMayBeFinal")
    private String template = "summary/template.txt";

    @Override
    public void doOutputFrom(final String interpolatedTemplate) {
        log.info("\n{}", interpolatedTemplate);
    }

    @Override
    public void cleanupOldReports() {
        log.debug("NoOp cleanupOldReports");
    }
}
