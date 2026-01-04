package io.github.giulong.spectrum.it_visual_regression.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.util.stream.Stream;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_visual_regression.pages.CheckboxPage;
import io.github.giulong.spectrum.it_visual_regression.pages.LandingPage;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;

@DisplayName("Test Factory")
class TestFactoryIT extends SpectrumTest<Void> {

    // You just need to declare your pages here: Spectrum will take care of instantiating them
    // and will inject all the needed fields like the driver
    private LandingPage landingPage;

    private CheckboxPage checkboxPage;

    @TestFactory
    Stream<DynamicNode> dynamicTestsWithContainers() {
        return Stream
                .of(1, 2)
                .map(number -> dynamicTest("Dynamic test " + number, () -> {
                    // Open the base url of the application under test
                    driver.get(configuration.getApplication().getBaseUrl());
                    assertEquals("Welcome to the-internet", landingPage.getTitle().getText());
                    landingPage.getCheckboxLink().click();

                    screenshotInfo("Before checking the checkbox number " + number);

                    extentTest.info("Custom log calling directly extent test");
                    checkboxPage.getCheckboxes().get(number).click();

                    // Take a screenshot with a custom message
                    screenshotInfo("After checking the checkbox number " + number);
                }));
    }
}
