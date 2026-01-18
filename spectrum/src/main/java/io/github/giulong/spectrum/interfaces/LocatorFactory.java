package io.github.giulong.spectrum.interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.github.giulong.spectrum.element_locator_factories.AjaxLocatorFactory;
import io.github.giulong.spectrum.element_locator_factories.DefaultLocatorFactory;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultLocatorFactory.class, name = "default"),
        @JsonSubTypes.Type(value = AjaxLocatorFactory.class, name = "ajax")
})
public interface LocatorFactory {
    ElementLocatorFactory buildFor(final WebDriver driver);
}
