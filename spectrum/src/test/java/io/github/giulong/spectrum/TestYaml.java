package io.github.giulong.spectrum;

import com.fasterxml.jackson.annotation.JsonView;

import io.github.giulong.spectrum.internals.jackson.views.Views.Internal;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@SuppressWarnings("unused")
public class TestYaml {

    private String key;
    private ObjectKey objectKey;
    private InternalKey internalKey;

    @Getter
    public static class ObjectKey {
        private String objectField;
    }

    @Getter
    @Builder
    @Jacksonized
    @JsonView(Internal.class)
    public static class InternalKey {
        private String field;
    }
}
