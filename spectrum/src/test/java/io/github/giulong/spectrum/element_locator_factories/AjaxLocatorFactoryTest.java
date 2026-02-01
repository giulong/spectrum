package io.github.giulong.spectrum.element_locator_factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;

import java.time.Duration;

import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

class AjaxLocatorFactoryTest {

    @Mock
    private WebDriver driver;

    @InjectMocks
    private AjaxLocatorFactory locatorFactory;

    @Test
    @DisplayName("buildFor should return a new instance of AjaxElementLocatorFactory")
    void testBuildFor() {
        final Duration timeout = Duration.ofSeconds(10);
        Reflections.setField("timeout", locatorFactory, timeout);

        try (MockedConstruction<AjaxElementLocatorFactory> construction = mockConstruction()) {
            final ElementLocatorFactory actual = locatorFactory.buildFor(driver);
            final ElementLocatorFactory expected = construction.constructed().getFirst();

            assertEquals(expected, actual);
            assertEquals(timeout, locatorFactory.getTimeout());
        }
    }
}
