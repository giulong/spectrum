package io.github.giulong.spectrum.utils.tests_comparators;

import com.aventstack.extentreports.model.Test;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Comparator;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NoOpComparator.class, name = "noOp"),
        @JsonSubTypes.Type(value = NameComparator.class, name = "name"),
        @JsonSubTypes.Type(value = StatusComparator.class, name = "status"),
})
public interface TestsComparator extends Comparator<Test> {
}
