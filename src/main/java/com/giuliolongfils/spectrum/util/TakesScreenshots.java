package com.giuliolongfils.spectrum.util;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Media;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static com.aventstack.extentreports.Status.*;

public abstract class TakesScreenshots {

    protected static SpectrumUtil spectrumUtil;
    protected ExtentTest extentTest;
    protected Actions actions;

    public abstract WebDriver getWebDriver();

    public Media infoWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(getWebDriver(), extentTest, msg, INFO);
    }

    public Media warningWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(getWebDriver(), extentTest, msg, WARNING);
    }

    public Media failWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(getWebDriver(), extentTest, msg, FAIL);
    }

    public void hover(final WebElement webElement) {
        actions.moveToElement(webElement).perform();
    }
}
