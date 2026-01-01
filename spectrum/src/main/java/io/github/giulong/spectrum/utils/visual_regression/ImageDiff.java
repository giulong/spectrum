package io.github.giulong.spectrum.utils.visual_regression;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.nio.file.Path;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@JsonTypeInfo(use = NAME, include = WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(value = NoOpDiff.class, name = "noOp"),
        @JsonSubTypes.Type(value = HighlightDiff.class, name = "highlight"),
        @JsonSubTypes.Type(value = OutlineDiff.class, name = "outline"),
})
public abstract class ImageDiff {

    public abstract Result buildBetween(Path reference, Path regression, Path destination, String diffName);

    @Getter
    @Builder
    @EqualsAndHashCode
    public static class Result {

        private Path path;

        @Builder.Default
        private boolean regressionConfirmed = true;
    }
}
