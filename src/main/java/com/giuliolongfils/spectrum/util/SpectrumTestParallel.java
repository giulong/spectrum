package com.giuliolongfils.spectrum.util;

import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.spectrum.pojos.Configuration;
import com.giuliolongfils.spectrum.pojos.SystemProperties;
import com.giuliolongfils.spectrum.pojos.WebDriverWaits;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@Slf4j
public abstract class SpectrumTestParallel<Data> extends BaseSpectrumTest<Data> {

    @Getter
    protected WebDriver webDriver;
    protected WebDriverWaits webDriverWaits;

    @BeforeAll
    public void spectrumTestParallelBeforeAll(final SpectrumUtil nu, final Configuration c, final Data d, final SystemProperties sp) {
        spectrumUtil = nu;
        configuration = c;
        data = d;
        systemProperties = sp;

        initPages();
    }

    @BeforeEach
    public void spectrumTestParallelBeforeEach(final WebDriver wd, final WebDriverWaits wdw, final ExtentTest et, final Actions a) {
        this.webDriver = wd;
        this.webDriverWaits = wdw;
        this.extentTest = et;
        this.actions = a;

        spectrumPages.forEach(spectrumPage -> {
            spectrumPage.webDriver = webDriver;
            spectrumPage.webDriverWaits = webDriverWaits;
            spectrumPage.extentTest = extentTest;
            spectrumPage.eventsListener = eventsListener;
            spectrumPage.actions = actions;

            PageFactory.initElements(spectrumPage.webDriver, spectrumPage);
        });
    }

    @AfterEach
    public void spectrumTestParallelAfterEach() {
        log.debug("Quitting webDriver");
        webDriver.quit();
    }
}
