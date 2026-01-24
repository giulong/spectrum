package io.github.giulong.spectrum.element_locator_factories;

import io.github.giulong.spectrum.interfaces.LocatorFactory;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

@Slf4j
public class DefaultLocatorFactory implements LocatorFactory {

    @Override
    public ElementLocatorFactory buildFor(final WebDriver driver) {
        log.debug("Configuring DefaultElementLocatorFactory");
        return new DefaultElementLocatorFactory(driver);
    }
}
