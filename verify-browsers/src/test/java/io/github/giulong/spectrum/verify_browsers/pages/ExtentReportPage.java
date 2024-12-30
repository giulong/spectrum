package io.github.giulong.spectrum.verify_browsers.pages;

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
            @FindBy(className = "test-item"),
    })
    private List<WebElement> testViewTests;

    @FindBys({
            @FindBy(className = "test-detail"),
            @FindBy(tagName = "div"),
    })
    private List<WebElement> testViewTestsDetails;

    @FindBy(id = "demoit-skipped-test")
    private WebElement skippedTest;

    @FindBy(id = "filesit-upload")
    private WebElement upload;

    @FindBy(id = "demoit-sending-custom-events")
    private WebElement customEvents;

    @FindBy(id = "demoit-this-one-should-fail-for-demonstration-purposes")
    private WebElement fail;

    @FindBy(id = "loginformit-with-user-giulio-we-expect-login-to-be-successful-false")
    private WebElement loginFalse;

    @FindBy(id = "loginformit-with-user-tom-we-expect-login-to-be-successful-true")
    private WebElement loginTrue;

    @FindBy(id = "checkboxit-testwithnodisplayname()")
    private WebElement noDisplayName;

    @FindBy(id = "filesit-download")
    private WebElement download;

    @FindBy(id = "filesit-successful-download")
    private WebElement successfulDownload;

    @FindBy(className = "screenshot-container")
    private List<WebElement> screenshotContainers;

    @FindBy(id = "video-javascriptit-testinputfieldactions()")
    private WebElement videoJavascriptItTestInputFieldActions;

    @FindBy(id = "video-javascriptit-testfindelementmethod()")
    private WebElement videoJavascriptItTestFindElementMethod;

    @FindBy(id = "video-javascriptit-shadowdom()")
    private WebElement videoJavascriptItShadowDom;

    @FindBy(id = "video-javascriptit-testwebelementgetmethods()")
    private WebElement videoJavascriptItTestWebElementGetMethods;

    @FindBy(id = "video-javascriptit-testfindelementsmethod()")
    private WebElement videoJavascriptItTestFindElementsMethod;

    @FindBy(id = "video-javascriptit-testwithnodisplayname()")
    private WebElement videoJavascriptItTestWithNoDisplayName;

    @FindBy(id = "video-testfactoryit-dynamictestswithcontainers()")
    private WebElement videoTestFactoryItDynamicTestsWithContainers;

    @FindBy(id = "video-navigationit-test-to-show-navigation-and-produced-video")
    private WebElement videoNavigationItTestToShowNavigationAndProducedVideo;

    @FindBy(id = "video-jswebelementit-checkingjswebelements()")
    private WebElement videoJsWebElementItCheckingJsWebElements;

    @FindBy(id = "video-jswebelementit-testfindelementsmethod()")
    private WebElement videoJsWebElementItTestFindElementsMethod;

    @FindBy(id = "video-jswebelementit-shadowdom()")
    private WebElement videoJsWebElementItShadowDom;

    @FindBy(id = "video-jswebelementit-testinputfieldactions()")
    private WebElement videoJsWebElementItTestInputFieldActions;

    @FindBy(id = "video-checkboxit-testwithnodisplayname()")
    private WebElement videoCheckboxItTestWithNoDisplayName;

    @FindBy(id = "video-filesit-upload")
    private WebElement videoFilesItUpload;

    @FindBy(id = "video-demoit-sending-custom-events")
    private WebElement videoDemoItSendingCustomEvents;

    @FindBy(id = "video-demoit-this-one-should-fail-for-demonstration-purposes")
    private WebElement videoDemoItThisOneShouldFailForDemonstrationPurposes;

    @FindBy(id = "video-loginformit-with-user-giulio-we-expect-login-to-be-successful-false")
    private WebElement videoLoginFormItWithUserGiulioWeExpectLoginToBeSuccessfulFalse;

    @FindBy(id = "video-loginformit-with-user-tom-we-expect-login-to-be-successful-true")
    private WebElement videoLoginFormItWithUserTomWeExpectLoginToBeSuccessfulTrue;

    @FindBy(id = "video-filesit-download")
    private WebElement videoFilesItDownload;

    @FindBy(id = "video-filesit-successful-download")
    private WebElement videoFilesItSuccessfulDownload;

    @FindBy(className = "inline")
    private List<WebElement> inlineImages;
}
