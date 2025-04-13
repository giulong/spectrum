package io.github.giulong.spectrum.verify_browsers.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;

@Getter
@SuppressWarnings("unused")
public class BiDiExtentReportPage extends SpectrumPage<BiDiExtentReportPage, Void> {

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

    @FindBy(id = "bidicheckboxit-testwithnodisplayname()")
    private WebElement noDisplayName;

    @FindBy(id = "bidicheckboxit-testwithnodisplayname()-test-name")
    private WebElement noDisplayNameTestName;

    @FindBy(id = "video-bidicheckboxit-testwithnodisplayname()")
    private WebElement videoCheckboxItTestWithNoDisplayName;

    @FindBy(css = "div[data-test-id='bidicheckboxit-testwithnodisplayname()'][data-frame='0']")
    private List<WebElement> noDisplayNameFrame0;

    @FindBy(css = "div[data-test-id='bidicheckboxit-testwithnodisplayname()'][data-frame='1']")
    private List<WebElement> noDisplayNameFrame1;

    @FindBy(css = "div[data-test-id='bidicheckboxit-testwithnodisplayname()'][data-frame='2']")
    private List<WebElement> noDisplayNameFrame2;

    @FindBy(css = "div[data-test-id='bidicheckboxit-testwithnodisplayname()'][data-frame='3']")
    private List<WebElement> noDisplayNameFrame3;

    @FindBy(css = "div[data-test-id='bidicheckboxit-testwithnodisplayname()'][data-frame='4']")
    private List<WebElement> noDisplayNameFrame4;

    public String getTextOf(final List<WebElement> webElements) {
        // Extent triplicates elements and show/hide them when navigating the sections of the report
        return webElements.get(1).getText();
    }
}
