package io.github.giulong.spectrum.it_verifier.unit;

import io.github.giulong.spectrum.it_verifier.FailsafeReportsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("It Grid Module Verifier")
public class ItGridVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();

    private static final int COMPLETED = 3;
    private static final int ERRORS = 0;
    private static final int FAILURES = 0;
    private static final int SKIPPED = 0;

    @Test
    @DisplayName("chrome should have run with the correct results")
    public void verifyChrome() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre(Path.of("it-grid", "target", "failsafe-reports", "failsafe-chrome.xml"), COMPLETED, ERRORS, FAILURES, SKIPPED), "CHROME");
    }
}
