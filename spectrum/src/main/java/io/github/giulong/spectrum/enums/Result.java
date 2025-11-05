package io.github.giulong.spectrum.enums;

import static com.aventstack.extentreports.Status.*;

import com.aventstack.extentreports.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Result {

    NOT_RUN("Not Run", SKIP),
    SUCCESSFUL("Successful", PASS),
    FAILED("Failed", FAIL),
    ABORTED("Aborted", FAIL),
    DISABLED("Disabled", SKIP);

    private final String value;
    private final Status status;
}
