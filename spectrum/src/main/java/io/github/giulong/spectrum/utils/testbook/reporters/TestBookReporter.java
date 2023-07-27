package io.github.giulong.spectrum.utils.testbook.reporters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LogTestBookReporter.class, name = "log"),
        @JsonSubTypes.Type(value = TxtTestBookReporter.class, name = "txt"),
        @JsonSubTypes.Type(value = HtmlTestBookReporter.class, name = "html"),
})
@Getter
public abstract class TestBookReporter {

    protected static final FileUtils FILE_UTILS = FileUtils.getInstance();

    protected static final FreeMarkerWrapper FREE_MARKER_WRAPPER = FreeMarkerWrapper.getInstance();

    public abstract String getTemplate();

    public abstract void doOutputFrom(String interpolatedTemplate);

    public static void evaluateQualityGateStatusFrom(final TestBook testBook) {
        testBook.getVars().put("qgStatus", FREE_MARKER_WRAPPER.interpolate("qgStatus", testBook.getQualityGate().getCondition(), testBook.getVars()));
    }

    public void flush(final TestBook testBook) {
        final String template = getTemplate();
        doOutputFrom(FREE_MARKER_WRAPPER.interpolate(template, FILE_UTILS.read(String.format("/%s", template)), testBook.getVars()));
    }
}
