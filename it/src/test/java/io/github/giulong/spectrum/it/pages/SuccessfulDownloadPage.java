package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

@Getter
@SuppressWarnings("unused")
public class SuccessfulDownloadPage extends SpectrumPage<SuccessfulDownloadPage, Void> {

    @FindBy(tagName = "iframe")
    private List<WebElement> iframes;

    @FindBy(id = "RightSide_Advertisement")
    private WebElement rightAdvertisement;

    @FindBy(id = "downloadButton")
    private WebElement downloadButton;

    public void downloadFile() {
        final String script = "arguments[0].hidden = true";
        final JavascriptExecutor js = (JavascriptExecutor) driver;

        iframes.forEach(iframe -> js.executeScript(script, iframe));
        js.executeScript(script, rightAdvertisement);
        downloadButton.click();
    }
}
