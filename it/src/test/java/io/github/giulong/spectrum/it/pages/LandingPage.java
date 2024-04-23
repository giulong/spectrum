package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@SuppressWarnings("unused")
public class LandingPage extends SpectrumPage<LandingPage, Void> {

    @FindBy(id = "login")
    private WebElement form;

    @FindBy(tagName = "h1")
    private WebElement title;

    @FindBy(linkText = "Checkboxes")
    private WebElement checkboxLink;

    @FindBy(linkText = "Form Authentication")
    private WebElement FormLoginLink;
}
