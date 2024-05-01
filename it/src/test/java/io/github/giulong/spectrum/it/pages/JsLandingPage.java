package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.JsWebElement;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@SuppressWarnings("unused")
public class JsLandingPage extends SpectrumPage<JsLandingPage, Void> {

    @FindBy(tagName = "h1")
    @JsWebElement
    private WebElement title;

    @FindBy(linkText = "Checkboxes")
    @JsWebElement
    private WebElement checkboxLink;
}
