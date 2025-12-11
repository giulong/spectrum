package io.github.giulong.spectrum.it_visual_regression.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_visual_regression.pages.CheckboxPage;
import io.github.giulong.spectrum.it_visual_regression.pages.LandingPage;
import io.github.giulong.spectrum.utils.FileUtils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.openqa.selenium.WebElement;

@TestMethodOrder(OrderAnnotation.class)
class VisualRegressionIT extends SpectrumTest<Void> {

    private static final Path SNAPSHOTS_FOLDER = Path.of(configuration.getVisualRegression().getSnapshots().getFolder());
    private static final FileUtils FILE_UTILS = FileUtils.getInstance();

    private LandingPage landingPage;
    private CheckboxPage checkboxPage;

    @BeforeAll
    static void beforeAll() {
        FILE_UTILS.deleteContentOf(SNAPSHOTS_FOLDER);
    }

    @Test
    @Order(1)
    @DisplayName("alwaysTheSame")
    void testReferenceCreation() {
        this.runActualTest();
    }

    @Test
    @Order(2)
    @DisplayName("alwaysTheSame")
    void testSuccessfulChecks() {
        this.runActualTest();
    }

    @Test
    @Order(3)
    @DisplayName("alwaysTheSame")
    void testFailedChecks() throws IOException {
        try (InputStream inputStream = VisualRegressionIT.class.getResourceAsStream("/no-video.png");
                Stream<Path> stream = Files.walk(SNAPSHOTS_FOLDER)) {
            final List<File> files = stream
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .toList();

            final Path secondScreenshot = files.get(1).toPath();

            FILE_UTILS.delete(secondScreenshot);
            FILE_UTILS.write(secondScreenshot, Objects.requireNonNull(inputStream).readAllBytes());
        }

        this.runActualTest();
    }

    private void runActualTest() {
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

        landingPage.getAbTestLink().click();
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

        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        firstCheckbox.click();
        firstCheckbox.click();
        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        screenshotInfo("After checking the first checkbox");
    }
}
