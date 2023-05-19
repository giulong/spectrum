package com.giuliolongfils.spectrum.utils.testbook.parsers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @Type(value = TxtTestBookParser.class, name = "txt"),
        @Type(value = YamlTestBookParser.class, name = "yaml"),
})
@Getter
@Setter
public abstract class TestBookParser {

    protected String path;

    public abstract List<String> parse();

}
