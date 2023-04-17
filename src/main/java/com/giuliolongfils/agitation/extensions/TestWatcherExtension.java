package com.giuliolongfils.agitation.extensions;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.giuliolongfils.agitation.internal.ContextManager;
import com.giuliolongfils.agitation.internal.Util;
import com.giuliolongfils.agitation.util.AgitationTestParallel;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.util.AgitationUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;

@Slf4j
public class TestWatcherExtension implements TestWatcher {

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        logTestStatus(context, SKIP);
        ContextManager.getInstance().getOrCreateExtentTest(context).skip(createLabel("Skipped: " + reason.orElse("no reason"), CYAN));
        finalizeTest(context, SKIP);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        logTestStatus(context, PASS);
        finalizeTest(context, PASS);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable throwable) {
        logTestStatus(context, ERROR);
        finalizeTest(context, ERROR);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable exception) {
        logTestStatus(context, FAIL);

        final ContextManager contextManager = ContextManager.getInstance();
        final ExtentTest extentTest = contextManager.getOrCreateExtentTest(context);
        final AgitationUtil agitationUtil = contextManager.getAgitationUtil(context);

        extentTest.fail(exception);
        agitationUtil.addScreenshotToReport(contextManager.getWebDriver(context), extentTest, createLabel("TEST FAILED", RED).getMarkup(), FAIL);

        finalizeTest(context, FAIL);
    }

    protected String logTestStatus(final ExtensionContext context, final Status status) {
        final ContextManager contextManager = ContextManager.getInstance();
        final String msg = String.format("END execution of '%s.%s': %s",
                contextManager.getClassName(context), contextManager.getTestName(context), status.name());
        log.info(msg);

        return msg;
    }

    protected void finalizeTest(final ExtensionContext context, final Status status) {
        final ContextManager contextManager = ContextManager.getInstance();
        final WebDriver webDriver = contextManager.getWebDriver(context);
        final Configuration configuration = contextManager.getConfiguration(context);
        contextManager.getOrCreateExtentTest(context).log(status, createLabel("END TEST", getColorOf(status)));

        if (webDriver != null) {
            if (Util.hasSuperclass(contextManager.getTestClass(context), AgitationTestParallel.class)) {
                webDriver.quit();
            } else if (configuration.getWebDriver().isDeleteCookies()) {
                webDriver.manage().deleteAllCookies();
            }
        }
    }

    protected ExtentColor getColorOf(final Status status) {
        return switch (status) {
            case FAIL, ERROR -> RED;
            case SKIP -> CYAN;
            default -> GREEN;
        };
    }
}
