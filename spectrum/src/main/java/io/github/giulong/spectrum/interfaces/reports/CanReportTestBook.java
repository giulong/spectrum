package io.github.giulong.spectrum.interfaces.reports;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.giulong.spectrum.utils.reporters.FileReporter;
import io.github.giulong.spectrum.utils.reporters.LogReporter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LogReporter.LogTestBookReporter.class, name = "log"),
        @JsonSubTypes.Type(value = FileReporter.TxtTestBookReporter.class, name = "txt"),
        @JsonSubTypes.Type(value = FileReporter.HtmlTestBookReporter.class, name = "html"),
})
public interface CanReportTestBook extends CanReport {
}
