package io.github.giulong.spectrum.it_visual_regression_fae.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_visual_regression_fae.pages.CheckboxPage;
import io.github.giulong.spectrum.it_visual_regression_fae.pages.LandingPage;
import io.github.giulong.spectrum.utils.FileUtils;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.openqa.selenium.WebElement;

@Slf4j
@TestMethodOrder(OrderAnnotation.class)
class VisualRegressionFailAtEndIT extends SpectrumTest<Void> {

    private static final String TEST_NAME = "alwaysTheSame";
    private static final FileUtils FILE_UTILS = FileUtils.getInstance();

    @SuppressWarnings("unused")
    private LandingPage landingPage;

    @SuppressWarnings("unused")
    private CheckboxPage checkboxPage;

    private static final Path SNAPSHOTS_FOLDER = Path.of(
            configuration.getVisualRegression().getSnapshots().getFolder(),
            VisualRegressionFailAtEndIT.class.getSimpleName(),
            TEST_NAME)
            .toAbsolutePath();

    @BeforeAll
    static void beforeAll() {
        FILE_UTILS.deleteContentOf(SNAPSHOTS_FOLDER);
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
        replaceScreenshots("screenshot-2.png", "screenshot-5.png", "screenshot-13.png");

        log.error("THIS IS EXPECTED TO FAIL");
        runActualTest();
    }

    private void runActualTest() {
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        screenshot(landingPage.getCheckboxLink());
        landingPage.getCheckboxLink().click();
        driver.navigate().back();
        screenshot();

        landingPage.getAddRemoveElementsLink().click();
        driver.navigate().back();
        screenshot();

        landingPage.getBrokenImagesLink().click();
        driver.navigate().back();
        screenshot();

        landingPage.getCheckboxLink().click();

        final WebElement firstCheckbox = checkboxPage.getCheckboxes().getFirst();
        final WebElement secondCheckbox = checkboxPage.getCheckboxes().get(1);

        screenshot();
        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());
        screenshot();

        firstCheckbox.click();
        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        screenshotInfo("After checking the first checkbox");
    }

    private void replaceScreenshots(final String... names) throws IOException {
        final List<String> screenshotsToDelete = Arrays.stream(names).toList();

        try (Stream<Path> paths = Files.walk(SNAPSHOTS_FOLDER)) {
            paths
                    .filter(p -> screenshotsToDelete.contains(p.getFileName().toString()))
                    .forEach(p -> FILE_UTILS.write(p, FILE_UTILS.readBytesOf("failed-screenshot.png")));
        }
    }
}
