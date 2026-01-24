package io.github.giulong.spectrum.element_locator_factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

class DefaultLocatorFactoryTest {

    @Mock
    private WebDriver driver;

    @InjectMocks
    private DefaultLocatorFactory locatorFactory;

    @Test
    @DisplayName("buildFor should return a new instance of DefaultElementLocatorFactory")
    void testBuildFor() {
        try (MockedConstruction<DefaultElementLocatorFactory> construction = mockConstruction()) {
            final ElementLocatorFactory actual = locatorFactory.buildFor(driver);
            final ElementLocatorFactory expected = construction.constructed().getFirst();

            assertEquals(expected, actual);
        }
    }
}
