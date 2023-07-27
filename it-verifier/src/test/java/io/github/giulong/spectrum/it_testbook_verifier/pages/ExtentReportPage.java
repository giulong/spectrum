package io.github.giulong.spectrum.it_testbook_verifier.pages;

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

    @FindBy(id = "demo-test-skipped-test")
    private WebElement skippedTest;

    @FindBy(id = "files-test-upload")
    private WebElement upload;

    @FindBy(id = "demo-test-sending-custom-events")
    private WebElement customEvents;

    @FindBy(id = "demo-test-this-one-should-fail-for-demonstration-purposes")
    private WebElement fail;

    @FindBy(id = "login-form-leveraging-the-data.yaml-with-user-giulio-we-expect-login-to-be-successful:-false")
    private WebElement loginFalse;

    @FindBy(id = "login-form-leveraging-the-data.yaml-with-user-tom-we-expect-login-to-be-successful:-true")
    private WebElement loginTrue;

    @FindBy(id = "checkbox-page-testwithnodisplayname()")
    private WebElement noDisplayName;

    @FindBy(id = "files-test-download")
    private WebElement download;

    @FindBy(className = "screenshot-container")
    private List<WebElement> screenshotContainers;
}
