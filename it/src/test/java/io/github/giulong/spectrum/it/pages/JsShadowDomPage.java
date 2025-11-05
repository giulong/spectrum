package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import io.github.giulong.spectrum.interfaces.JsWebElement;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@Endpoint("shadowdom")
@SuppressWarnings("unused")
public class JsShadowDomPage extends SpectrumPage<JsShadowDomPage, Void> {

    @FindBy(tagName = "my-paragraph")
    @JsWebElement
    private WebElement myParagraph;

    @FindBy(css = "span[slot=\"my-text\"]")
    @JsWebElement
    private WebElement span;
}
