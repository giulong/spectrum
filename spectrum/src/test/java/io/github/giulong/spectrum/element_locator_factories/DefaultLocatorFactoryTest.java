package io.github.giulong.spectrum.element_locator_factories;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

class DefaultLocatorFactoryTest {

    @Test
    @DisplayName("buildFor should return a new instance of DefaultElementLocatorFactory")
    void testBuildFor() {
        final DefaultLocatorFactory defaultLocatorFactory = new DefaultLocatorFactory();
        final WebDriver driver = mock(WebDriver.class);
        ElementLocatorFactory actualFactory = defaultLocatorFactory.buildFor(driver);

        assertInstanceOf(DefaultElementLocatorFactory.class, actualFactory);
    }
}
