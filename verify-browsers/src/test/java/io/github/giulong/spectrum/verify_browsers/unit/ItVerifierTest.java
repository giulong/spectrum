package io.github.giulong.spectrum.verify_browsers.unit;

import io.github.giulong.spectrum.verify_commons.FailsafeReportsVerifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ItVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();
    private static final Path BASE_DIR = Path.of(System.getProperty("user.dir")).getParent();

    private static final int COMPLETED = 29;
    private static final int ERRORS = 2;
    private static final int FAILURES = 0;
    private static final int SKIPPED = 1;

    @Test
    @DisplayName("chrome should have run with the correct results")
    public void verifyChrome() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it", "chrome", COMPLETED, ERRORS, FAILURES, SKIPPED), "CHROME");
    }

    @Test
    @DisplayName("firefox should have run with the correct results")
    public void verifyFirefox() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it", "firefox", COMPLETED, ERRORS, FAILURES, SKIPPED), "FIREFOX");
    }

    @Test
    @DisplayName("edge should have run with the correct results")
    public void verifyEdge() {
        assertTrue(FAILSAFE_REPORTS_VERIFIER.verifyResultsAre("it", "edge", COMPLETED, ERRORS, FAILURES, SKIPPED), "EDGE");
    }

    @Test
    @DisplayName("log file should contain the debug saying some consumer tried to consume the custom events")
    public void logFile() throws FileNotFoundException {
        final String logFile = new Scanner(BASE_DIR.resolve(Path.of("it-testbook", "target", "spectrum", "logs", "spectrum.log")).toFile()).useDelimiter("\\Z").next();

        // we indirectly check that the log consumer accepted the event with a regex in the primaryId
        assertTrue(logFile.contains("""
                Class Name: ---
                Test Name : ---
                Reason    : custom-event
                Result    : ---
                Tags      : ---"""));

        // we indirectly check that the log consumer accepted the event with a regex in the reason
        assertTrue(logFile.contains("""
                Class Name: ---
                Test Name : ---
                Reason    : secondReason
                Result    : ---
                Tags      : ---"""));

        // we check values sent to @Secured web elements are masked
        assertTrue(logFile.contains("Sending keys [***] to id: password"));
    }
}
