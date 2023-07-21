package io.github.giulong.spectrum.it.pages;

import io.github.giulong.spectrum.SpectrumPage;
import lombok.Getter;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Getter
@SuppressWarnings("unused")
public class TestBookPage extends SpectrumPage<TestBookPage, Void> {

    @FindBy(className = "title")
    private WebElement title;

    @FindBy(id = "mapped-tests")
    private WebElement mappedTests;

    @FindBy(id = "unmapped-tests")
    private WebElement unmappedTests;

    @FindBy(id = "grand-total")
    private WebElement grandTotal;

    @FindBy(id = "total-weighted")
    private WebElement totalWeighted;

    @FindBy(id = "grand-total-weighted")
    private WebElement grandTotalWeighted;

    @FindBy(id = "weighted-successful")
    private WebElement weightedSuccessful;

    @FindBy(id = "weighted-successful-percentage")
    private WebElement weightedSuccessfulPercentage;

    @FindBy(id = "weighted-failed")
    private WebElement weightedFailed;

    @FindBy(id = "weighted-failed-percentage")
    private WebElement weightedFailedPercentage;

    @FindBy(id = "weighted-aborted")
    private WebElement weightedAborted;

    @FindBy(id = "weighted-aborted-percentage")
    private WebElement weightedAbortedPercentage;

    @FindBy(id = "weighted-disabled")
    private WebElement weightedDisabled;

    @FindBy(id = "weighted-disabled-percentage")
    private WebElement weightedDisabledPercentage;

    @FindBy(id = "weighted-not-run")
    private WebElement weightedNotRun;

    @FindBy(id = "weighted-not-run-percentage")
    private WebElement weightedNotRunPercentage;

    @FindBy(id = "grand-weighted-successful")
    private WebElement grandWeightedSuccessful;

    @FindBy(id = "grand-weighted-successful-percentage")
    private WebElement grandWeightedSuccessfulPercentage;

    @FindBy(id = "grand-weighted-failed")
    private WebElement grandWeightedFailed;

    @FindBy(id = "grand-weighted-failed-percentage")
    private WebElement grandWeightedFailedPercentage;

    @FindBy(id = "grand-weighted-aborted")
    private WebElement grandWeightedAborted;

    @FindBy(id = "grand-weighted-aborted-percentage")
    private WebElement grandWeightedAbortedPercentage;

    @FindBy(id = "grand-weighted-disabled")
    private WebElement grandWeightedDisabled;

    @FindBy(id = "grand-weighted-disabled-percentage")
    private WebElement grandWeightedDisabledPercentage;

    @FindBy(id = "grand-weighted-not-run")
    private WebElement grandWeightedNotRun;

    @FindBy(id = "grand-weighted-not-run-percentage")
    private WebElement grandWeightedNotRunPercentage;

    @FindBy(id = "successful")
    private WebElement successful;

    @FindBy(id = "successful-percentage")
    private WebElement successfulPercentage;

    @FindBy(id = "failed")
    private WebElement failed;

    @FindBy(id = "failed-percentage")
    private WebElement failedPercentage;

    @FindBy(id = "aborted")
    private WebElement aborted;

    @FindBy(id = "aborted-percentage")
    private WebElement abortedPercentage;

    @FindBy(id = "disabled")
    private WebElement disabled;

    @FindBy(id = "disabled-percentage")
    private WebElement disabledPercentage;

    @FindBy(id = "not-run")
    private WebElement notRun;

    @FindBy(id = "not-run-percentage")
    private WebElement notRunPercentage;

    @FindBy(id = "grand-successful")
    private WebElement grandSuccessful;

    @FindBy(id = "grand-successful-percentage")
    private WebElement grandSuccessfulPercentage;

    @FindBy(id = "grand-failed")
    private WebElement grandFailed;

    @FindBy(id = "grand-failed-percentage")
    private WebElement grandFailedPercentage;

    @FindBy(id = "grand-aborted")
    private WebElement grandAborted;

    @FindBy(id = "grand-aborted-percentage")
    private WebElement grandAbortedPercentage;

    @FindBy(id = "grand-disabled")
    private WebElement grandDisabled;

    @FindBy(id = "grand-disabled-percentage")
    private WebElement grandDisabledPercentage;

    @FindBy(id = "grand-not-run")
    private WebElement grandNotRun;

    @FindBy(id = "grand-not-run-percentage")
    private WebElement grandNotRunPercentage;
}
