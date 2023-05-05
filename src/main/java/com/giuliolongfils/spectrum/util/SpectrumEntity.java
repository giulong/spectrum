package com.giuliolongfils.spectrum.util;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Media;
import com.giuliolongfils.spectrum.internal.EventsListener;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.WebDriverWaits;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static com.aventstack.extentreports.Status.*;

public abstract class SpectrumEntity<Data> {

    protected static Configuration configuration;
    protected static SpectrumUtil spectrumUtil;
    protected static ExtentReports extentReports;

    protected ExtentTest extentTest;
    protected Actions actions;
    protected EventsListener eventsListener;
    protected WebDriver webDriver;
    protected WebDriverWaits webDriverWaits;
    protected Data data;

    public Media infoWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(webDriver, extentTest, msg, INFO);
    }

    public Media warningWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(webDriver, extentTest, msg, WARNING);
    }

    public Media failWithScreenshot(final String msg) {
        return spectrumUtil.addScreenshotToReport(webDriver, extentTest, msg, FAIL);
    }

    public void hover(final WebElement webElement) {
        actions.moveToElement(webElement).perform();
    }
}
