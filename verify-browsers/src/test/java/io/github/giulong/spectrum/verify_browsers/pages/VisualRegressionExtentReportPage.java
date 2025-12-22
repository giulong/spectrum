package io.github.giulong.spectrum.verify_browsers.pages;

import java.util.List;

import io.github.giulong.spectrum.SpectrumPage;

import lombok.Getter;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

@Getter
@SuppressWarnings("unused")
public class VisualRegressionExtentReportPage extends SpectrumPage<VisualRegressionExtentReportPage, Void> {

    @FindBys({
            @FindBy(className = "test-view"),
            @FindBy(className = "test-list"),
            @FindBy(className = "test-item"),
    })
    private List<WebElement> testViewTests;

    @FindBy(className = "screenshot-message")
    private List<WebElement> screenshotMessages;

    @FindBy(id = "video-visualregressionit-alwaysthesame")
    private List<WebElement> videosFailFast;

    @FindBy(id = "video-visualregressionfailatendit-alwaysthesame")
    private List<WebElement> videosNotFailFast;

    @FindBy(css = "textarea[class=code-block]")
    private List<WebElement> visualRegressionException;

    @FindBy(className = "visual-regression")
    private List<WebElement> visualRegressions;

    public String getTextOf(final List<WebElement> webElements) {
        // Extent triplicates elements and show/hide them when navigating the sections of the report
        return webElements.get(2).getText();
    }
}
