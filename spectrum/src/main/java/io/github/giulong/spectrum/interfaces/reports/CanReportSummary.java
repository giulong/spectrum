package io.github.giulong.spectrum.interfaces.reports;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.github.giulong.spectrum.utils.reporters.FileReporter;
import io.github.giulong.spectrum.utils.reporters.LogReporter;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LogReporter.LogSummaryReporter.class, name = "log"),
        @JsonSubTypes.Type(value = FileReporter.TxtSummaryReporter.class, name = "txt"),
        @JsonSubTypes.Type(value = FileReporter.HtmlSummaryReporter.class, name = "html"),
})
public interface CanReportSummary extends CanReport {
}
