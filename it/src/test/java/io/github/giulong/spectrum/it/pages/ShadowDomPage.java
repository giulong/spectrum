package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@Endpoint("shadowdom")
@SuppressWarnings("unused")
public class ShadowDomPage extends SpectrumPage<ShadowDomPage, Void> {

    @FindBy(tagName = "my-paragraph")
    private WebElement myParagraph;

    @FindBy(css = "span[slot=\"my-text\"]")
    private WebElement span;
}
