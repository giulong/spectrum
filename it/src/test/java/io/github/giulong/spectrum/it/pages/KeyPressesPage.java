package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import io.github.giulong.spectrum.interfaces.Endpoint;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


import static org.openqa.selenium.support.ui.ExpectedConditions.*;

@Getter
@Endpoint("key_presses")
public class KeyPressesPage extends SpectrumPage<KeyPressesPage, Void> {

    @FindBy(id = "target")
    private WebElement inputField;

    @Override
    public KeyPressesPage waitForPageLoading() {
        pageLoadWait.until(and(
                urlToBe("https://the-internet.herokuapp.com/key_presses"),
                visibilityOf(inputField)));

        return this;
    }
}
