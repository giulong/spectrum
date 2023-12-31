package io.github.giulong.spectrum.utils.summary.reporters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.summary.Summary;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LogSummaryReporter.class, name = "log"),
})
@Getter
public abstract class SummaryReporter {

    protected static final FileUtils FILE_UTILS = FileUtils.getInstance();

    protected static final FreeMarkerWrapper FREE_MARKER_WRAPPER = FreeMarkerWrapper.getInstance();

    public abstract String getTemplate();

    public abstract void doOutputFrom(String interpolatedTemplate);

    public abstract void cleanupOldReports();

    public void flush(final Summary summary) {
        final String template = getTemplate();
        doOutputFrom(FREE_MARKER_WRAPPER.interpolate(template, FILE_UTILS.read(String.format("/%s", template)), summary.getVars()));
        cleanupOldReports();
    }
}
