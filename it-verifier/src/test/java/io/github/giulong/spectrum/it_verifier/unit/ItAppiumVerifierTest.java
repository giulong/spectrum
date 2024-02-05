package io.github.giulong.spectrum.it_verifier.unit;

import io.github.giulong.spectrum.it_verifier.FailsafeReportsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("It Appium Module Verifier")
@EnabledIfSystemProperty(named = "appiumTests", matches = "true", disabledReason = "Not running since 'appiumTests' is not active")
public class ItAppiumVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();

    private static final int COMPLETED = 2;
    private static final int ERRORS = 0;
    private static final int FAILURES = 0;
    private static final int SKIPPED = 0;

    @Test
    @DisplayName("android should have run with the correct results")
    public void verifyAndroid() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-appium", "android", COMPLETED, ERRORS, FAILURES, SKIPPED), "ANDROID");
    }
}
