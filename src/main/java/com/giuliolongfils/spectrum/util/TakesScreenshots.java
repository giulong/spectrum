package com.giuliolongfils.spectrum.util;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityModelProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static com.aventstack.extentreports.Status.*;

public abstract class TakesScreenshots {

    protected SpectrumUtil spectrumUtil;
    protected ExtentTest extentTest;
    protected Actions actions;

    public abstract WebDriver getWebDriver();

    public MediaEntityModelProvider infoWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(getWebDriver(), extentTest, msg, INFO);
    }

    public MediaEntityModelProvider debugWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(getWebDriver(), extentTest, msg, DEBUG);
    }

    public MediaEntityModelProvider errorWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(getWebDriver(), extentTest, msg, ERROR);
    }

    public MediaEntityModelProvider failWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(getWebDriver(), extentTest, msg, FAIL);
    }

    public void hover(final WebElement webElement) {
        actions.moveToElement(webElement).perform();
    }
}