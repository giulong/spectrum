package io.github.giulong.spectrum.it_visual_regression.tests;

import java.io.IOException;
import java.nio.file.Path;

import io.github.giulong.spectrum.utils.Reflections;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Slf4j
class VisualRegressionFailAtEndIT extends VisualRegressionBase {

    private static final Path SNAPSHOTS_FOLDER = Path.of(
            configuration.getVisualRegression().getSnapshots().getFolder(),
            VisualRegressionFailAtEndIT.class.getSimpleName(),
            TEST_NAME)
            .toAbsolutePath();

    @BeforeAll
    static void beforeAll() {
        FILE_UTILS.deleteContentOf(SNAPSHOTS_FOLDER);

        // trick to avoid having a dedicated module with a corresponding configuration.yaml
        Reflections.setField("failFast", configuration.getVisualRegression(), false);
    }

    @Test
    @Order(1)
    @DisplayName(TEST_NAME)
    void testReferenceCreation() {
        extentTest.info("Creating references");
        this.runActualTest();
    }

    @Test
    @Order(2)
    @DisplayName(TEST_NAME)
    void testSuccessfulChecks() {
        extentTest.info("Successful checks");
        this.runActualTest();
    }

    @Test
    @Order(3)
    @DisplayName(TEST_NAME)
    void testFailedChecks() throws IOException {
        extentTest.info("Failed checks");
        replaceScreenshots(SNAPSHOTS_FOLDER, "screenshot-2.png", "screenshot-5.png", "screenshot-10.png");

        log.error("THIS IS EXPECTED TO FAIL");
        runActualTest();
    }
}
