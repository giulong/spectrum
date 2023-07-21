package io.github.giulong.spectrum;

import com.fasterxml.jackson.annotation.JsonView;
import io.github.giulong.spectrum.internals.jackson.views.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonView(Views.Internal.class)
    public static class InternalKey {
        private String field;
    }
}
