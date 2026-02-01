package io.github.giulong.spectrum.element_locator_factories;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.giulong.spectrum.interfaces.JsonSchemaTypes;
import io.github.giulong.spectrum.interfaces.LocatorFactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

@Slf4j
@Getter
@SuppressWarnings("unused")
public class AjaxLocatorFactory implements LocatorFactory {

    @JsonPropertyDescription("Timeout in seconds")
    @JsonSchemaTypes(double.class)
    private Duration timeout;

    @Override
    public ElementLocatorFactory buildFor(final WebDriver driver) {
        log.debug("Configuring AjaxElementLocatorFactory with a timeout of {}", timeout);
        return new AjaxElementLocatorFactory(driver, (int) timeout.toSeconds());
    }
}
