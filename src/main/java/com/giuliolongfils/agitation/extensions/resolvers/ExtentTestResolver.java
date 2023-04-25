package com.giuliolongfils.agitation.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.giuliolongfils.agitation.util.AgitationUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.giuliolongfils.agitation.extensions.AgitationExtension.CLASS_NAME;
import static com.giuliolongfils.agitation.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentTestResolver extends TypeBasedParameterResolver<ExtentTest> implements BeforeEachCallback, TestWatcher {

    private final ExtentReports extentReports;
    private final AgitationUtil agitationUtil;
    private ExtentTest extentTest;

    public ExtentTestResolver(final ExtentReports extentReports, AgitationUtil agitationUtil) {
        this.extentReports = extentReports;
        this.agitationUtil = agitationUtil;
    }

    @Override
    public ExtentTest resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        return extentTest;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        extentTest = createExtentTestFrom(context).info(createLabel("START TEST", getColorOf(INFO)));
    }

    public ExtentTest createExtentTestFrom(ExtensionContext context) {
        log.debug("Creating Extent Test");
        return extentReports.createTest(String.format("<div>%s</div>%s", context.getStore(GLOBAL).get(CLASS_NAME), context.getDisplayName()));
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        createExtentTestFrom(context).skip(createLabel("Skipped: " + reason.orElse("no reason"), getColorOf(SKIP)));
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        logTestStatus(context, PASS);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable throwable) {
        logTestStatus(context, ERROR);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable exception) {
        extentTest.fail(exception);
        agitationUtil.addScreenshotToReport(context.getStore(GLOBAL).get(WEB_DRIVER, WebDriver.class), extentTest, createLabel("TEST FAILED", RED).getMarkup(), FAIL);
        logTestStatus(context, FAIL);
    }

    protected void logTestStatus(final ExtensionContext context, final Status status) {
        log.info(String.format("END execution of '%s -> %s': %s", context.getStore(GLOBAL).get(CLASS_NAME), context.getDisplayName(), status.name()));
        extentTest.log(status, createLabel("END TEST", getColorOf(status)));
    }

    protected ExtentColor getColorOf(final Status status) {
        return switch (status) {
            case FAIL, ERROR -> RED;
            case SKIP -> AMBER;
            default -> GREEN;
        };
    }
}
