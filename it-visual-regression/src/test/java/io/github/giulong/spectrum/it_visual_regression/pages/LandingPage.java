package io.github.giulong.spectrum.it_visual_regression.pages;

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

    @FindBy(linkText = "A/B Testing")
    private WebElement abTestLink;

    @FindBy(linkText = "Add/Remove Elements")
    private WebElement addRemoveElementsLink;

    @FindBy(linkText = "Broken Images")
    private WebElement brokenImagesLink;

    @FindBy(linkText = "Checkboxes")
    private WebElement checkboxLink;

    @FindBy(linkText = "Form Authentication")
    private WebElement formLoginLink;
}
