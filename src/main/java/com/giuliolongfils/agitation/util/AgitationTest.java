package com.giuliolongfils.agitation.util;

import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import com.giuliolongfils.agitation.pojos.WebDriverWaits;
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
public abstract class AgitationTest<Data> extends BaseAgitationTest<Data> {

    protected static WebDriver webDriver;
    protected static WebDriverWaits webDriverWaits;

    @Override
    public WebDriver getWebDriver() {
        return webDriver;
    }

    @BeforeAll
    public void agitationTestBeforeAll(final WebDriver wd, final WebDriverWaits wdw, final AgitationUtil su,
                                       final Configuration c, final Data d, final SystemProperties sp, final Actions a) {
        webDriver = wd;
        webDriverWaits = wdw;
        agitationUtil = su;
        configuration = c;
        data = d;
        systemProperties = sp;
        actions = a;

        initPages();

        agitationPages.forEach(agitationPage -> {
            agitationPage.webDriver = webDriver;
            agitationPage.webDriverWaits = webDriverWaits;
            agitationPage.actions = actions;

            PageFactory.initElements(agitationPage.webDriver, agitationPage);
        });
    }

    @BeforeEach
    public void agitationTestBeforeEach(final ExtentTest et) {
        this.extentTest = et;

        agitationPages.forEach(agitationPage -> {
            agitationPage.extentTest = extentTest;
            agitationPage.eventsListener = eventsListener;
        });
    }

    @AfterAll
    public void agitationTestAfterAll() {
        log.debug("Quitting webDriver");
        webDriver.quit();
    }
}
