package io.github.giulong.spectrum.verify_macos;

import io.github.giulong.spectrum.verify_commons.FailsafeReportsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.MAC;

@DisplayName("It macOS Module Verifier")
@EnabledOnOs(MAC)
public class ItMacosVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();

    private static final int COMPLETED = 1;
    private static final int ERRORS = 0;
    private static final int FAILURES = 0;
    private static final int SKIPPED = 0;

    @Test
    @DisplayName("safari should have run with the correct results")
    public void verifySafari() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-macos", "safari", COMPLETED, ERRORS, FAILURES, SKIPPED), "SAFARI");
    }
}
