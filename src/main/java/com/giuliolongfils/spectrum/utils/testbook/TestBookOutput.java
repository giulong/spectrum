package com.giuliolongfils.spectrum.utils.testbook;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.giuliolongfils.spectrum.pojos.TestBook;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LogTestBookOutput.class, name = "log")
})
@Getter
public abstract class TestBookOutput {

    public abstract void updateWith(TestBook testBook);

}
