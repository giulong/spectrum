package com.giuliolongfils.spectrum.util;

import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.SystemProperties;
import com.giuliolongfils.spectrum.pojos.WebDriverWaits;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@Slf4j
public abstract class SpectrumTest<Data> extends BaseSpectrumTest<Data> {

    protected static WebDriver webDriver;
    protected static WebDriverWaits webDriverWaits;

    @Override
    public WebDriver getWebDriver() {
        return webDriver;
    }

    @BeforeAll
    public void spectrumTestBeforeAll(final WebDriver wd, final WebDriverWaits wdw, final SpectrumUtil su,
                                       final Configuration c, final Data d, final SystemProperties sp, final Actions a) {
        webDriver = wd;
        webDriverWaits = wdw;
        spectrumUtil = su;
        configuration = c;
        data = d;
        systemProperties = sp;
        actions = a;

        initPages();

        spectrumPages.forEach(spectrumPage -> {
            spectrumPage.webDriver = webDriver;
            spectrumPage.webDriverWaits = webDriverWaits;
            spectrumPage.actions = actions;

            PageFactory.initElements(spectrumPage.webDriver, spectrumPage);
        });
    }

    @BeforeEach
    public void spectrumTestBeforeEach(final ExtentTest et) {
        this.extentTest = et;

        spectrumPages.forEach(spectrumPage -> {
            spectrumPage.extentTest = extentTest;
            spectrumPage.eventsListener = eventsListener;
        });
    }

    @AfterAll
    public void spectrumTestAfterAll() {
        webDriver.quit();
    }
}
