package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;

@Getter
@Endpoint("checkboxes")
@SuppressWarnings("unused")
public class JsCheckboxPage extends SpectrumPage<JsCheckboxPage, Void> {

    @FindBys({
            @FindBy(id = "checkboxes"),
            @FindBy(tagName = "input")
    })
    @JsWebElement
    private List<WebElement> checkboxes;
}
