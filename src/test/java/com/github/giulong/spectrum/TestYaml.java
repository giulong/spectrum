package com.github.giulong.spectrum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestYaml {

    private String key;
    private ObjectKey objectKey;

    @Getter
    public static class ObjectKey {
        private String objectField;
    }
}
