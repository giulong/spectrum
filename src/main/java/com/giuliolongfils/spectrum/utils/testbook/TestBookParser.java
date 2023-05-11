package com.giuliolongfils.spectrum.utils.testbook;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.giuliolongfils.spectrum.pojos.TestBook;
import com.giuliolongfils.spectrum.pojos.TestBookResult;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @Type(value = TxtTestBookParser.class, name = "txt")
})
@Getter
public abstract class TestBookParser {

    @JsonIgnore
    protected final TestBook testBook = new TestBook();

    @JsonIgnore
    private List<String> testNames;

    protected String path;

    protected List<TestBookOutput> output;

    public abstract List<String> parse();

    public abstract void setStatus(final String className, final String testName, final TestBookResult.Status status);

    public void parseTests() {
        testBook.getTests().putAll(parse().stream().collect(Collectors.toMap(Function.identity(), testName -> new TestBookResult())));
    }
}
