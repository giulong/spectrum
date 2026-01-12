package io.github.giulong.spectrum.verify_browsers.pages;

import java.util.List;

import io.github.giulong.spectrum.SpectrumPage;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

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

    @FindBy(id = "demoit-skipped-test-test-name")
    private WebElement skippedTestTestName;

    @FindBy(id = "filesit-upload")
    private WebElement upload;

    @FindBy(id = "filesit-upload-test-name")
    private WebElement uploadTestName;

    @FindBy(id = "demoit-sending-custom-events")
    private WebElement customEvents;

    @FindBy(id = "demoit-sending-custom-events-test-name")
    private WebElement customEventsTestName;

    @FindBy(id = "demoit-this-one-should-fail-for-demonstration-purposes")
    private WebElement fail;

    @FindBy(id = "demoit-this-one-should-fail-for-demonstration-purposes-test-name")
    private WebElement failTestName;

    @FindBy(id = "loginformit-leveraging-the-data.yaml-with-user-giulio-we-expect-login-to-be-successful-false")
    private WebElement loginFalse;

    @FindBy(id = "loginformit-leveraging-the-data.yaml-with-user-giulio-we-expect-login-to-be-successful-false-test-name")
    private WebElement loginFalseTestName;

    @FindBy(id = "loginformit-leveraging-the-data.yaml-with-user-tom-we-expect-login-to-be-successful-true")
    private WebElement loginTrue;

    @FindBy(id = "loginformit-leveraging-the-data.yaml-with-user-tom-we-expect-login-to-be-successful-true-test-name")
    private WebElement loginTrueTestName;

    @FindBy(id = "checkboxit-testwithnodisplayname()")
    private WebElement noDisplayName;

    @FindBy(id = "checkboxit-testwithnodisplayname()-test-name")
    private WebElement noDisplayNameTestName;

    @FindBy(id = "filesit-download")
    private WebElement download;

    @FindBy(id = "filesit-download-test-name")
    private WebElement downloadTestName;

    @FindBy(id = "fakerit-the-login-should-fail-leveraging-random-name-generated-by-faker")
    private WebElement fakerItTheLoginShouldFailLeveragingRandomNameGeneratedByFaker;

    @FindBy(id = "fakerit-the-login-should-fail-leveraging-random-name-generated-by-faker-test-name")
    private WebElement fakerItTheLoginShouldFailLeveragingRandomNameGeneratedByFakerTestName;

    @FindBy(id = "fakerit-expressions-should-generate-random-numbers-with-0-increments")
    private WebElement fakerItWith0Increments;

    @FindBy(id = "fakerit-expressions-should-generate-random-numbers-with-0-increments-test-name")
    private WebElement fakerItWith0IncrementsTestName;

    @FindBy(id = "fakerit-expressions-should-generate-random-numbers-with-2-increments")
    private WebElement fakerItWith2Increments;

    @FindBy(id = "fakerit-expressions-should-generate-random-numbers-with-2-increments-test-name")
    private WebElement fakerItWith2IncrementsTestName;

    @FindBy(id = "fakerit-expressions-should-generate-random-numbers-with-5-increments")
    private WebElement fakerItWith5Increments;

    @FindBy(id = "fakerit-expressions-should-generate-random-numbers-with-5-increments-test-name")
    private WebElement fakerItWith5IncrementsTestName;

    @FindBy(id = "dynamicit-navigation-to-prove-auto-wait-helps-a-lot")
    private WebElement dynamicItNavigationToProveAutoWaitHelpsALot;

    @FindBy(id = "dynamicit-navigation-to-prove-auto-wait-helps-a-lot-test-name")
    private WebElement dynamicItNavigationToProveAutoWaitHelpsALotTestName;

    @FindBy(id = "javascriptit-testwithnodisplayname()")
    private WebElement javascriptItTestWithNoDisplayName;

    @FindBy(id = "testfactoryit-dynamictestswithcontainers()")
    private WebElement testFactoryItDynamicTestsWithContainers;

    @FindBy(className = "screenshot-message")
    private List<WebElement> screenshotMessages;

    @FindBy(id = "video-javascriptit-testinputfieldactions()")
    private WebElement videoJavascriptItTestInputFieldActions;

    @FindBy(id = "video-javascriptit-testinputfieldactions()-test-name")
    private WebElement videoJavascriptItTestInputFieldActionsTestName;

    @FindBy(id = "video-javascriptit-testfindelementmethod()")
    private WebElement videoJavascriptItTestFindElementMethod;

    @FindBy(id = "video-javascriptit-testfindelementmethod()-test-name")
    private WebElement videoJavascriptItTestFindElementMethodTestName;

    @FindBy(id = "video-javascriptit-shadowdom()")
    private WebElement videoJavascriptItShadowDom;

    @FindBy(id = "video-javascriptit-shadowdom()-test-name")
    private WebElement videoJavascriptItShadowDomTestName;

    @FindBy(id = "video-javascriptit-testwebelementgetmethods()")
    private WebElement videoJavascriptItTestWebElementGetMethods;

    @FindBy(id = "video-javascriptit-testwebelementgetmethods()-test-name")
    private WebElement videoJavascriptItTestWebElementGetMethodsTestName;

    @FindBy(id = "video-javascriptit-testfindelementsmethod()")
    private WebElement videoJavascriptItTestFindElementsMethod;

    @FindBy(id = "video-javascriptit-testfindelementsmethod()-test-name")
    private WebElement videoJavascriptItTestFindElementsMethodTestName;

    @FindBy(id = "video-javascriptit-testwithnodisplayname()")
    private WebElement videoJavascriptItTestWithNoDisplayName;

    @FindBy(id = "video-javascriptit-testwithnodisplayname()-test-name")
    private WebElement videoJavascriptItTestWithNoDisplayNameTestName;

    @FindBy(id = "video-testfactoryit-dynamictestswithcontainers()")
    private WebElement videoTestFactoryItDynamicTestsWithContainers;

    @FindBy(id = "video-testfactoryit-dynamictestswithcontainers()-test-name")
    private WebElement videoTestFactoryItDynamicTestsWithContainersTestName;

    @FindBy(id = "video-navigationit-test-to-show-navigation-and-produced-video")
    private WebElement videoNavigationItTestToShowNavigationAndProducedVideo;

    @FindBy(id = "video-navigationit-test-to-show-navigation-and-produced-video-test-name")
    private WebElement videoNavigationItTestToShowNavigationAndProducedVideoTestName;

    @FindBy(id = "video-jswebelementit-checkingjswebelements()")
    private WebElement videoJsWebElementItCheckingJsWebElements;

    @FindBy(id = "video-jswebelementit-checkingjswebelements()-test-name")
    private WebElement videoJsWebElementItCheckingJsWebElementsTestName;

    @FindBy(id = "video-jswebelementit-testfindelementsmethod()")
    private WebElement videoJsWebElementItTestFindElementsMethod;

    @FindBy(id = "video-jswebelementit-testfindelementsmethod()-test-name")
    private WebElement videoJsWebElementItTestFindElementsMethodTestName;

    @FindBy(id = "video-jswebelementit-shadowdom()")
    private WebElement videoJsWebElementItShadowDom;

    @FindBy(id = "video-jswebelementit-shadowdom()-test-name")
    private WebElement videoJsWebElementItShadowDomTestName;

    @FindBy(id = "video-jswebelementit-testinputfieldactions()")
    private WebElement videoJsWebElementItTestInputFieldActions;

    @FindBy(id = "video-jswebelementit-testinputfieldactions()-test-name")
    private WebElement videoJsWebElementItTestInputFieldActionsTestName;

    @FindBy(id = "video-checkboxit-testwithnodisplayname()")
    private WebElement videoCheckboxItTestWithNoDisplayName;

    @FindBy(id = "video-checkboxit-testwithnodisplayname()-test-name")
    private WebElement videoCheckboxItTestWithNoDisplayNameTestName;

    @FindBy(id = "video-filesit-upload")
    private WebElement videoFilesItUpload;

    @FindBy(id = "video-filesit-upload-test-name")
    private WebElement videoFilesItUploadTestName;

    @FindBy(id = "video-demoit-sending-custom-events")
    private WebElement videoDemoItSendingCustomEvents;

    @FindBy(id = "video-demoit-sending-custom-events-test-name")
    private WebElement videoDemoItSendingCustomEventsTestName;

    @FindBy(id = "video-demoit-this-one-should-fail-for-demonstration-purposes")
    private WebElement videoDemoItThisOneShouldFailForDemonstrationPurposes;

    @FindBy(id = "video-demoit-this-one-should-fail-for-demonstration-purposes-test-name")
    private WebElement videoDemoItThisOneShouldFailForDemonstrationPurposesTestName;

    @FindBy(id = "video-loginformit-leveraging-the-data.yaml-with-user-giulio-we-expect-login-to-be-successful-false")
    private WebElement videoLoginFormItWithUserGiulioWeExpectLoginToBeSuccessfulFalse;

    @FindBy(id = "video-loginformit-leveraging-the-data.yaml-with-user-giulio-we-expect-login-to-be-successful-false-test-name")
    private WebElement videoLoginFormItWithUserGiulioWeExpectLoginToBeSuccessfulFalseTestName;

    @FindBy(id = "video-loginformit-leveraging-the-data.yaml-with-user-tom-we-expect-login-to-be-successful-true")
    private WebElement videoLoginFormItWithUserTomWeExpectLoginToBeSuccessfulTrue;

    @FindBy(id = "video-loginformit-leveraging-the-data.yaml-with-user-tom-we-expect-login-to-be-successful-true-test-name")
    private WebElement videoLoginFormItWithUserTomWeExpectLoginToBeSuccessfulTrueTestName;

    @FindBy(id = "video-filesit-download")
    private WebElement videoFilesItDownload;

    @FindBy(id = "video-filesit-download-test-name")
    private WebElement videoFilesItDownloadTestName;

    @FindBy(id = "video-fakerit-the-login-should-fail-leveraging-random-name-generated-by-faker")
    private WebElement videoFakerItTheLoginShouldFailLeveragingRandomNameGeneratedByFaker;

    @FindBy(id = "video-fakerit-the-login-should-fail-leveraging-random-name-generated-by-faker-test-name")
    private WebElement videoFakerItTheLoginShouldFailLeveragingRandomNameGeneratedByFakerTestName;

    @FindBy(id = "video-fakerit-expressions-should-generate-random-numbers-with-0-increments")
    private WebElement videoFakerItWith0Increments;

    @FindBy(id = "video-fakerit-expressions-should-generate-random-numbers-with-0-increments-test-name")
    private WebElement videoFakerItWith0IncrementsTestName;

    @FindBy(id = "video-fakerit-expressions-should-generate-random-numbers-with-2-increments")
    private WebElement videoFakerItWith2Increments;

    @FindBy(id = "video-fakerit-expressions-should-generate-random-numbers-with-2-increments-test-name")
    private WebElement videoFakerItWith2IncrementsTestName;

    @FindBy(id = "video-fakerit-expressions-should-generate-random-numbers-with-5-increments")
    private WebElement videoFakerItWith5Increments;

    @FindBy(id = "video-fakerit-expressions-should-generate-random-numbers-with-5-increments-test-name")
    private WebElement videoFakerItWith5IncrementsTestName;

    @FindBy(id = "video-dynamicit-navigation-to-prove-auto-wait-helps-a-lot")
    private WebElement videoDynamicItNavigationToProveAutoWaitHelpsALot;

    @FindBy(id = "video-dynamicit-navigation-to-prove-auto-wait-helps-a-lot-test-name")
    private WebElement videoDynamicItNavigationToProveAutoWaitHelpsALotTestName;

    @FindBy(className = "inline")
    private List<WebElement> inlineImages;

    @FindBy(css = "div.card-header")
    private List<WebElement> cardHeaders;

    @FindBy(css = "div[data-test-id='checkboxit-testwithnodisplayname()'][data-frame='0']")
    private List<WebElement> noDisplayNameFrame0;

    @FindBy(css = "div[data-test-id='checkboxit-testwithnodisplayname()'][data-frame='1']")
    private List<WebElement> noDisplayNameFrame1;

    @FindBy(css = "div[data-test-id='checkboxit-testwithnodisplayname()'][data-frame='2']")
    private List<WebElement> noDisplayNameFrame2;

    @FindBy(css = "div[data-test-id='checkboxit-testwithnodisplayname()'][data-frame='3']")
    private List<WebElement> noDisplayNameFrame3;

    @FindBy(css = "div[data-test-id='checkboxit-testwithnodisplayname()'][data-frame='4']")
    private List<WebElement> noDisplayNameFrame4;

    @FindBy(css = "div[data-test-id='checkboxit-testwithnodisplayname()'][data-frame='5']")
    private List<WebElement> noDisplayNameFrame5;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='0']")
    private List<WebElement> dynamicFrame0;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='1']")
    private List<WebElement> dynamicFrame1;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='2']")
    private List<WebElement> dynamicFrame2;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='3']")
    private List<WebElement> dynamicFrame3;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='4']")
    private List<WebElement> dynamicFrame4;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='5']")
    private List<WebElement> dynamicFrame5;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='6']")
    private List<WebElement> dynamicFrame6;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='7']")
    private List<WebElement> dynamicFrame7;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='8']")
    private List<WebElement> dynamicFrame8;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='9']")
    private List<WebElement> dynamicFrame9;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='10']")
    private List<WebElement> dynamicFrame10;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='11']")
    private List<WebElement> dynamicFrame11;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='12']")
    private List<WebElement> dynamicFrame12;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='13']")
    private List<WebElement> dynamicFrame13;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='14']")
    private List<WebElement> dynamicFrame14;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='15']")
    private List<WebElement> dynamicFrame15;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='16']")
    private List<WebElement> dynamicFrame16;

    @FindBy(css = "div[data-test-id='dynamicit-navigation-to-prove-auto-wait-helps-a-lot'][data-frame='17']")
    private List<WebElement> dynamicFrame17;

    @FindBy(css = "div[data-test-id='javascriptit-testwithnodisplayname()'][data-frame='0']")
    private List<WebElement> javascriptFrame0;

    @FindBy(css = "div[data-test-id='javascriptit-testwithnodisplayname()'][data-frame='1']")
    private List<WebElement> javascriptFrame1;

    @FindBy(css = "div[data-test-id='javascriptit-testwithnodisplayname()'][data-frame='2']")
    private List<WebElement> javascriptFrame2;

    @FindBy(css = "div[data-test-id='javascriptit-testwithnodisplayname()'][data-frame='3']")
    private List<WebElement> javascriptFrame3;

    @FindBy(css = "div[data-test-id='testfactoryit-dynamictestswithcontainers()'][data-frame='0']")
    private List<WebElement> dynamicContainersFrame0;

    @FindBy(css = "div[data-test-id='testfactoryit-dynamictestswithcontainers()'][data-frame='1']")
    private List<WebElement> dynamicContainersFrame1;

    @FindBy(css = "div[data-test-id='testfactoryit-dynamictestswithcontainers()'][data-frame='2']")
    private List<WebElement> dynamicContainersFrame2;

    public String getTextOf(final List<WebElement> webElements) {
        // Extent triplicates elements and show/hide them when navigating the sections of the report
        return webElements.get(1).getText();
    }

    public String getTextInFirstContainerOf(final List<WebElement> webElements) {
        // Extent triplicates elements and show/hide them when navigating the sections of the report
        return webElements.get(2).getText();
    }

    public String getTextInSecondContainerOf(final List<WebElement> webElements) {
        // Extent triplicates elements and show/hide them when navigating the sections of the report
        return webElements.get(3).getText();
    }

    public void clickFirstCardHeader() {
        cardHeaders
                .stream()
                .filter(WebElement::isDisplayed)
                .findFirst()
                .orElseThrow()
                .click();
    }
}
