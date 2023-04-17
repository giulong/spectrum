package com.giuliolongfils.agitation.util;

import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import com.giuliolongfils.agitation.pojos.WebDriverWaits;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public abstract class AgitationTest extends BaseAgitationTest {

    protected static WebDriver webDriver;
    protected static WebDriverWaits webDriverWaits;

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
            agitationPage.eventListener = eventListener;
        });
    }

    @Override
    public WebDriver getWebDriver() {
        return webDriver;
    }
}
