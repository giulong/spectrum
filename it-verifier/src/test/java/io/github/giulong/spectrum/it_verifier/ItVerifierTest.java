package io.github.giulong.spectrum.it_verifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("It Module Verifier")
public class ItVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();
    private static final String BASE_DIR = Path.of(System.getProperty("user.dir")).getParent().toString();

    @Test
    @DisplayName("chrome should have run with the correct results")
    public void verifyChrome() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre(Path.of(BASE_DIR, "it", "target", "failsafe-reports", "failsafe-chrome.xml"), 8, 1, 0, 1));
    }

    @Test
    @DisplayName("firefox should have run with the correct results")
    public void verifyFirefox() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre(Path.of(BASE_DIR, "it", "target", "failsafe-reports", "failsafe-firefox.xml"), 8, 1, 0, 1));
    }

    @Test
    @DisplayName("edge should have run with the correct results")
    public void verifyEdge() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre(Path.of(BASE_DIR, "it", "target", "failsafe-reports", "failsafe-edge.xml"), 8, 1, 0, 1));
    }
}
