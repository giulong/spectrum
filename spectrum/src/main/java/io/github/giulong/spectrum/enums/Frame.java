package io.github.giulong.spectrum.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Frame {

    AUTO("auto"),
    AUTO_BEFORE("autoBefore"),
    AUTO_AFTER("autoAfter"),
    MANUAL("manual"),
    VISUAL_REGRESSION_MANUAL("visualRegressionManual");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
