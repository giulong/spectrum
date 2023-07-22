package io.github.giulong.spectrum.it_verifier;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("It Module Verifier")
public class ItVerifierTest {

    private static final FailsafeReportsVerifier FAILSAFE_REPORTS_VERIFIER = FailsafeReportsVerifier.getInstance();
    private static final Path BASE_DIR = Path.of(System.getProperty("user.dir")).getParent();
    
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

    @Test
    @DisplayName("log file should contain the debug saying some consumer tried to consume the custom events")
    public void logFile() throws FileNotFoundException {
        final String logFile = new Scanner(BASE_DIR.resolve(Path.of("it-testbook", "target", "spectrum", "logs", "spectrum.log")).toFile()).useDelimiter("\\Z").next();

        // we indirectly check that the slack handler tried to consume the event with a regex in the primaryId
        assertTrue(logFile.contains("SlackConsumer is consuming Event(primaryId=primaryId, secondaryId=null, tags=null, reason=custom-event, result=null, context=null)"));

        // we indirectly check that the slack handler tried to consume the event with a regex in the reason
        assertTrue(logFile.contains("SlackConsumer is consuming Event(primaryId=primaryId, secondaryId=null, tags=null, reason=secondReason, result=null, context=null)"));
    }
}
