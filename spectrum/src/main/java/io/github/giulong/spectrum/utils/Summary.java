package io.github.giulong.spectrum.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.interfaces.reports.CanReportSummary;
import io.github.giulong.spectrum.interfaces.reports.Reportable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Getter
@Slf4j
@SuppressWarnings("unused")
public class Summary implements SessionHook, Reportable {

    @JsonIgnore
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();

    @JsonIgnore
    private final FileUtils fileUtils = FileUtils.getInstance();

    @JsonIgnore
    private final SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();

    @JsonPropertyDescription("List of reporters that will produce the summary in specific formats")
    private List<CanReportSummary> reporters;

    @JsonIgnore
    private final Map<String, Object> vars = new HashMap<>();

    @Override
    public void sessionClosed() {
        log.debug("Session closed hook");

        final TestExecutionSummary summary = summaryGeneratingListener.getSummary();
        final long total = summary.getTestsFoundCount();
        final long succeeded = summary.getTestsSucceededCount();
        final long failed = summary.getTestsFailedCount();
        final long aborted = summary.getTestsAbortedCount();
        final long disabled = summary.getTestsSkippedCount();
        final long duration = summary.getTimeFinished() - summary.getTimeStarted();

        final long hours = MILLISECONDS.toHours(duration);
        final long minutes = MILLISECONDS.toMinutes(duration) % 60;
        final long seconds = MILLISECONDS.toSeconds(duration) % 60;

        vars.put("summary", summary);
        vars.put("duration", String.format("%02d:%02d:%02d", hours, minutes, seconds));
        vars.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        vars.put("total", total);
        vars.put("successfulPercentage", (double) succeeded / total * 100);
        vars.put("failedPercentage", (double) failed / total * 100);
        vars.put("abortedPercentage", (double) aborted / total * 100);
        vars.put("disabledPercentage", (double) disabled / total * 100);

        reporters.forEach(reporter -> reporter.flush(this));
    }
}
