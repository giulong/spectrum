package io.github.giulong.spectrum.it_verifier.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@SuppressWarnings("unused")
public class SummaryPage extends SpectrumPage<SummaryPage, Void> {

    @FindBy(className = "title")
    private WebElement title;

    @FindBy(id = "successful-count")
    private WebElement successfulCount;

    @FindBy(id = "successful-percentage")
    private WebElement successfulPercentage;

    @FindBy(id = "failed-count")
    private WebElement failedCount;

    @FindBy(id = "failed-percentage")
    private WebElement failedPercentage;

    @FindBy(id = "aborted-count")
    private WebElement abortedCount;

    @FindBy(id = "aborted-percentage")
    private WebElement abortedPercentage;

    @FindBy(id = "disabled-count")
    private WebElement disabledCount;

    @FindBy(id = "disabled-percentage")
    private WebElement disabledPercentage;

    @FindBy(id = "started-at")
    private WebElement startedAt;

    @FindBy(id = "ended-at")
    private WebElement endedAt;

    @FindBy(id = "duration")
    private WebElement duration;
}
