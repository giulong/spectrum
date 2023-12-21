package io.github.giulong.spectrum.utils.testbook.parsers;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.github.giulong.spectrum.pojos.testbook.TestBookTest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @Type(value = TxtTestBookParser.class, name = "txt"),
        @Type(value = YamlTestBookParser.class, name = "yaml"),
        @Type(value = CsvTestBookParser.class, name = "csv"),
})
@Getter
@Setter
public abstract class TestBookParser {

    @JsonPropertyDescription("Path of the testBook")
    protected String path;

    public abstract List<TestBookTest> parse();
}
