package io.github.giulong.spectrum.it_verifier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;

import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.assertTrue;

@NoArgsConstructor(access = PRIVATE)
public class FailsafeReportsVerifier {

    private static final FailsafeReportsVerifier INSTANCE = new FailsafeReportsVerifier();

    private final ObjectMapper mapper = new XmlMapper();

    @SneakyThrows
    public Report read(final String filePath) {
        return mapper.readValue(new File(filePath), Report.class);
    }

    public boolean verifyCompletedAre(final Report report, final int completed) {
        return report.completed == completed;
    }

    public boolean verifyErrorsAre(final Report report, final int errors) {
        return report.errors == errors;
    }

    public boolean verifyFailuresAre(final Report report, final int failures) {
        return report.failures == failures;
    }

    public boolean verifySkippedAre(final Report report, final int skipped) {
        return report.skipped == skipped;
    }

    public boolean verifyResultsAre(final String filePath, final int completed, final int errors, final int failures, final int skipped) {
        final Report report = read(filePath);

        return verifyCompletedAre(report, completed) &&
                verifyErrorsAre(report, errors) &&
                verifyFailuresAre(report, failures) &&
                verifySkippedAre(report, skipped);
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    @SuppressWarnings("unused")
    private static class Report {
        private int completed;
        private int errors;
        private int failures;
        private int skipped;
    }

    public static void main(String... args) {
        assertTrue(FailsafeReportsVerifier.INSTANCE.verifyResultsAre("D:\\Giulio\\workspace\\spectrum\\it\\target\\failsafe-reports\\failsafe-chrome.xml", 8, 1, 0, 1));
    }
}
