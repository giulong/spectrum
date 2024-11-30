package io.github.giulong.spectrum.verify_browsers.unit;

import io.github.giulong.spectrum.SpectrumTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ItTestStepsVerifierTest extends SpectrumTest<Void> {

    @Test
    @DisplayName("should check the produced test steps reports")
    public void testSteps() throws IOException {
        final Path projectRoot = Path.of(System.getProperty("user.dir")).getParent();
        final Path reportsDir = Path.of(String.format("%s/it/target/spectrum/tests-steps", projectRoot));

        try (Stream<Path> stream = Files.walk(reportsDir)) {
            final List<File> files = stream
                    .map(Path::toFile)
                    .peek(f -> log.info("Found '{}'", f))
                    .toList();
            assertEquals(19, files.size());
        }
    }
}
