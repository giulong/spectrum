package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@SuppressWarnings("unused")
public class SuccessfulDownloadPage extends SpectrumPage<SuccessfulDownloadPage, Void> {

    @FindBy(id = "downloadButton")
    private WebElement downloadButton;
}
