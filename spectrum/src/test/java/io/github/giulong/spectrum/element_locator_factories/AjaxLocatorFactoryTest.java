package io.github.giulong.spectrum.element_locator_factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

class AjaxLocatorFactoryTest {

    @Test
    @DisplayName("buildFor should return a new instance of AjaxElementLocatorFactory")
    void testBuildFor() throws NoSuchFieldException, IllegalAccessException {
        final AjaxLocatorFactory ajaxLocatorFactory = new AjaxLocatorFactory();
        final WebDriver driver = mock(WebDriver.class);
        final int timeoutSeconds = 10;
        final Duration timeout = Duration.ofSeconds(timeoutSeconds);

        Field timeoutField = AjaxLocatorFactory.class.getDeclaredField("timeout");
        timeoutField.setAccessible(true);
        timeoutField.set(ajaxLocatorFactory, timeout);

        ElementLocatorFactory actualFactory = ajaxLocatorFactory.buildFor(driver);

        assertInstanceOf(AjaxElementLocatorFactory.class, actualFactory);

        assertEquals(timeout, ajaxLocatorFactory.getTimeout());
    }
}
