package com.giuliolongfils.agitation.util;

import com.aventstack.extentreports.ExtentTest;
import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import com.giuliolongfils.agitation.pojos.WebDriverWaits;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
public abstract class AgitationTestParallel extends BaseAgitationTest {

    @Getter
    protected WebDriver webDriver;
    protected WebDriverWaits webDriverWaits;

    @BeforeAll
    public void agitationTestParallelBeforeAll(final AgitationUtil nu, final Configuration c, final Data d, final SystemProperties sp) {
        agitationUtil = nu;
        configuration = c;
        data = d;
        systemProperties = sp;

        initPages();
    }

    @BeforeEach
    public void agitationTestParallelBeforeEach(final WebDriver wd, final WebDriverWaits wdw, final ExtentTest et, final Actions a) {
        this.webDriver = wd;
        this.webDriverWaits = wdw;
        this.extentTest = et;
        this.actions = a;

        agitationPages.forEach(agitationPage -> {
            agitationPage.webDriver = webDriver;
            agitationPage.webDriverWaits = webDriverWaits;
            agitationPage.extentTest = extentTest;
            agitationPage.eventListener = eventListener;
            agitationPage.actions = actions;

            PageFactory.initElements(agitationPage.webDriver, agitationPage);
        });
    }
}
