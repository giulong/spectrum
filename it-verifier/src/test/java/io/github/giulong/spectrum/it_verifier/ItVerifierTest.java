package io.github.giulong.spectrum.it_verifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("It Module Verifier")
public class ItVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();
    
    private static final int COMPLETED = 8;
    private static final int ERRORS = 1;
    private static final int FAILURES = 0;
    private static final int SKIPPED = 1;

    @Test
    @DisplayName("chrome should have run with the correct results")
    public void verifyChrome() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre(Path.of("it", "target", "failsafe-reports", "failsafe-chrome.xml"), COMPLETED, ERRORS, FAILURES, SKIPPED));
    }

    @Test
    @DisplayName("firefox should have run with the correct results")
    public void verifyFirefox() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre(Path.of("it", "target", "failsafe-reports", "failsafe-firefox.xml"), COMPLETED, ERRORS, FAILURES, SKIPPED));
    }

    @Test
    @DisplayName("edge should have run with the correct results")
    public void verifyEdge() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre(Path.of("it", "target", "failsafe-reports", "failsafe-edge.xml"), COMPLETED, ERRORS, FAILURES, SKIPPED));
    }
}
