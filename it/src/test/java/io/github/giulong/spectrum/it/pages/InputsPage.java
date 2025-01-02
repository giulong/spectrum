package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@Endpoint("inputs")
@SuppressWarnings("unused")
public class InputsPage extends SpectrumPage<InputsPage, Void> {

    @FindBy(css = "input[type=number]")
    private WebElement input;
}
