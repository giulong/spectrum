package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@SuppressWarnings("unused")
public class SuccessfulDownloadPage extends SpectrumPage<SuccessfulDownloadPage, Void> {

    @FindBy(css = "button[aria-label='Do not consent']")
    private WebElement doNotConsent;

    @FindBy(id = "textbox")
    private WebElement textbox;

    @FindBy(id = "createTxt")
    private WebElement createTxt;

    @FindBy(id = "link-to-download")
    private WebElement downloadLink;

    public void createAndDownloadFileWithText(final String text) {
        clearAndSendKeys(textbox, text);
        createTxt.click();
        downloadLink.click();
    }
}
