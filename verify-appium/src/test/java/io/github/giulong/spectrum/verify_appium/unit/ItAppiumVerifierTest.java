package io.github.giulong.spectrum.verify_appium.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.giulong.spectrum.verify_commons.FailsafeReportsVerifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("It Appium Module Verifier")
public class ItAppiumVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();

    private static final int COMPLETED = 2;
    private static final int ERRORS = 0;
    private static final int FAILURES = 0;
    private static final int SKIPPED = 0;

    @Test
    @DisplayName("android should have run with the correct results")
    public void verifyAndroid() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-appium", "uiAutomator2", COMPLETED, ERRORS, FAILURES, SKIPPED), "ANDROID");
    }
}
