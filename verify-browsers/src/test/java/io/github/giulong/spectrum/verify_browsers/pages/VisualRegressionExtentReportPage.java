package io.github.giulong.spectrum.verify_browsers.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;

import java.util.List;

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

    @FindBy(className = "visual-regression")
    private List<WebElement> visualRegressions;

    @FindBy(css = "textarea[class=code-block]")
    private List<WebElement> visualRegressionException;

    public String getTextOf(final List<WebElement> webElements) {
        // Extent triplicates elements and show/hide them when navigating the sections of the report
        return webElements.get(1).getText();
    }
}
