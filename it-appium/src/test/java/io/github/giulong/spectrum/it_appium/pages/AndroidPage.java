package io.github.giulong.spectrum.it_appium.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@SuppressWarnings("unused")
public class AndroidPage extends SpectrumPage<AndroidPage, Void> {

    @FindBy(xpath = "//android.widget.TextView")
    private WebElement textView;
}
