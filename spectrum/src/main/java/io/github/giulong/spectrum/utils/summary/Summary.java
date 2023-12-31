package io.github.giulong.spectrum.utils.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.summary.reporters.SummaryReporter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
@SuppressWarnings("unused")
public class Summary implements SessionHook {

    @JsonIgnore
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();

    @JsonIgnore
    private final FileUtils fileUtils = FileUtils.getInstance();

    @JsonIgnore
    private final SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();

    @JsonPropertyDescription("List of reporters that will produce the summary in specific formats")
    private List<SummaryReporter> reporters;

    @JsonIgnore
    private final Map<String, Object> vars = new HashMap<>();

    @Override
    public void sessionClosed() {
        log.debug("Session closed hook");

        vars.put("summary", summaryGeneratingListener.getSummary());
        reporters.forEach(reporter -> reporter.flush(this));
    }
}
