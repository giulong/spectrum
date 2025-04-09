package io.github.giulong.spectrum.verify_browsers.unit;

import io.github.giulong.spectrum.verify_commons.FailsafeReportsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("It Bidi Module Verifier")
public class ItBiDiVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();

    private static final int COMPLETED = 6;
    private static final int ERRORS = 0;
    private static final int FAILURES = 0;
    private static final int SKIPPED = 0;

    @Test
    @DisplayName("chrome should have run with the correct results")
    public void verifyChrome() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-bidi", "chrome", COMPLETED, ERRORS, FAILURES, SKIPPED), "CHROME");
    }

    @Test
    @DisplayName("firefox should have run with the correct results")
    public void verifyFirefox() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-bidi", "firefox", COMPLETED, ERRORS, FAILURES, SKIPPED), "FIREFOX");
    }

    @Test
    @DisplayName("edge should have run with the correct results")
    public void verifyEdge() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it-bidi", "edge", COMPLETED, ERRORS, FAILURES, SKIPPED), "EDGE");
    }
}
