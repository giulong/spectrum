package io.github.giulong.spectrum.it_visual_regression.tests;

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
import io.github.giulong.spectrum.it_visual_regression.pages.CheckboxPage;
import io.github.giulong.spectrum.it_visual_regression.pages.LandingPage;
import io.github.giulong.spectrum.utils.FileUtils;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.WebElement;

@Slf4j
@TestMethodOrder(OrderAnnotation.class)
abstract class VisualRegressionBase extends SpectrumTest<Void> {

    protected static final String TEST_NAME = "alwaysTheSame";
    protected static final FileUtils FILE_UTILS = FileUtils.getInstance();

    protected LandingPage landingPage;
    protected CheckboxPage checkboxPage;

    protected void runActualTest() {
        driver.get(configuration.getApplication().getBaseUrl());
        assertEquals("Welcome to the-internet", landingPage.getTitle().getText());

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

        assertFalse(firstCheckbox.isSelected());
        assertTrue(secondCheckbox.isSelected());

        firstCheckbox.click();
        firstCheckbox.click();
        firstCheckbox.click();
        assertTrue(firstCheckbox.isSelected());

        screenshotInfo("After checking the first checkbox");
    }

    protected void replaceScreenshots(final Path folder, final String... names) throws IOException {
        final List<String> screenshotsToDelete = Arrays.stream(names).toList();

        try (Stream<Path> paths = Files.walk(folder)) {
            paths
                    .filter(p -> screenshotsToDelete.contains(p.getFileName().toString()))
                    .forEach(p -> FILE_UTILS.write(p, FILE_UTILS.read("no-video.png")));
        }
    }
}
