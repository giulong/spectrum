package io.github.giulong.spectrum.it_testbook.pages;

import io.github.giulong.spectrum.SpectrumPage;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@SuppressWarnings("unused")
public class LandingPage extends SpectrumPage<LandingPage, Void> {

    @FindBy(tagName = "h1")
    private WebElement title;

    @FindBy(linkText = "Checkboxes")
    private WebElement checkboxLink;
}
