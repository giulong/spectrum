package io.github.giulong.spectrum.it_verifier.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;

@Getter
@SuppressWarnings("unused")
public class ExtentReportPage extends SpectrumPage<ExtentReportPage, Void> {

    @FindBys({
            @FindBy(className = "test-view"),
            @FindBy(className = "test-list"),
            @FindBy(className = "test-item")
    })
    private List<WebElement> testViewTests;

    @FindBy(id = "demoit-skipped-test")
    private WebElement skippedTest;

    @FindBy(id = "filesit-upload")
    private WebElement upload;

    @FindBy(id = "demoit-sending-custom-events")
    private WebElement customEvents;

    @FindBy(id = "demoit-this-one-should-fail-for-demonstration-purposes")
    private WebElement fail;

    @FindBy(id = "loginformit-with-user-giulio-we-expect-login-to-be-successful:-false")
    private WebElement loginFalse;

    @FindBy(id = "loginformit-with-user-tom-we-expect-login-to-be-successful:-true")
    private WebElement loginTrue;

    @FindBy(id = "checkboxit-testwithnodisplayname()")
    private WebElement noDisplayName;

    @FindBy(id = "filesit-download")
    private WebElement download;

    @FindBy(className = "screenshot-container")
    private List<WebElement> screenshotContainers;
}
