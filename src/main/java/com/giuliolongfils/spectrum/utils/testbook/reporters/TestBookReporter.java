package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.giuliolongfils.spectrum.pojos.testbook.TestBookResult;
import com.giuliolongfils.spectrum.utils.FileReader;
import com.giuliolongfils.spectrum.utils.testbook.TestBook;
import lombok.Getter;

import java.util.Map;

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

    public static final FileReader FILE_READER = FileReader.getInstance();

    public abstract String getTemplate();

    public abstract Map<String, String> getSpecificReplacementsFor(Map<String, TestBookResult> tests, Map<String, TestBookResult> unmappedTests);

    public abstract void doOutputFrom(String interpolatedTemplate);

    public void flush(final TestBook testBook) {
        testBook.getReplacements().putAll(getSpecificReplacementsFor(testBook.getTests(), testBook.getUnmappedTests()));
        doOutputFrom(FILE_READER.interpolate(getTemplate(), testBook.getReplacements()));
    }
}
