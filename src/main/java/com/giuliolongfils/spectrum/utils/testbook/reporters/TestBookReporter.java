package com.giuliolongfils.spectrum.utils.testbook.reporters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.giuliolongfils.spectrum.utils.FileReader;
import com.giuliolongfils.spectrum.utils.FreeMarkerConfiguration;
import com.giuliolongfils.spectrum.utils.testbook.TestBook;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

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

    public static final Configuration CONFIGURATION = FreeMarkerConfiguration.getInstance().getConfiguration();

    public abstract String getTemplate();

    public abstract void doOutputFrom(String interpolatedTemplate);

    @SneakyThrows
    public void flush(final TestBook testBook) {
        final Template qgStatus = new Template("qgStatus", new StringReader(testBook.getQualityGate().getCondition()), CONFIGURATION);
        final Writer qgStatusWriter = new StringWriter();
        qgStatus.process(testBook.getVars(), qgStatusWriter);
        testBook.getVars().put("qgStatus", qgStatusWriter.toString());

        final Template template = new Template(getTemplate(), new StringReader(FILE_READER.read(getTemplate())), CONFIGURATION);
        final Writer templateWriter = new StringWriter();
        template.process(testBook.getVars(), templateWriter);
        doOutputFrom(templateWriter.toString());
    }
}
