package io.github.giulong.spectrum.verify_browsers.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.giulong.spectrum.verify_commons.FailsafeReportsVerifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("It Grid Module Verifier")
public class ItGridVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();

    private static final int COMPLETED = 4;
    private static final int ERRORS = 0;
    private static final int FAILURES = 0;
    private static final int SKIPPED = 0;

    @Test
    @DisplayName("chrome should have run with the correct results")
    public void verifyChrome() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-grid", "chrome", COMPLETED, ERRORS, FAILURES, SKIPPED), "CHROME");
    }

    @Test
    @DisplayName("firefox should have run with the correct results")
    public void verifyFirefox() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-grid", "firefox", COMPLETED, ERRORS, FAILURES, SKIPPED), "FIREFOX");
    }

    @Test
    @DisplayName("edge should have run with the correct results")
    public void verifyEdge() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-grid", "edge", COMPLETED, ERRORS, FAILURES, SKIPPED), "EDGE");
    }
}
