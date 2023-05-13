package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.giuliolongfils.spectrum.utils.FileReader;
import com.giuliolongfils.spectrum.utils.testbook.TestBook;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LogTestBookReporter.class, name = "log"),
        @JsonSubTypes.Type(value = HtmlTestBookReporter.class, name = "html")
})
@Getter
public abstract class TestBookReporter {

    public static final FileReader FILE_READER = FileReader.getInstance();

    public abstract void updateWith(TestBook testBook);

    public abstract String getTestsReplacementFrom(TestBook testBook);

    public abstract String getUnmappedTestsReplacementFrom(TestBook testBook);

    public String parse(final String template, final TestBook testBook) {
        return FILE_READER
                .read(template)
                .replace("{{timestamp}}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .replace("{{tests}}", getTestsReplacementFrom(testBook))
                .replace("{{unmapped-tests}}", getUnmappedTestsReplacementFrom(testBook));
    }
}
