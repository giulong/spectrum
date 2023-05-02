package com.giuliolongfils.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.giuliolongfils.spectrum.util.SpectrumUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.*;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.giuliolongfils.spectrum.extensions.SpectrumExtension.CLASS_NAME;
import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentTestResolver extends TypeBasedParameterResolver<ExtentTest> implements BeforeEachCallback, TestWatcher {

    public static final String EXTENT_TEST = "extentTest";
    private final ExtentReports extentReports;
    private final SpectrumUtil spectrumUtil;
    private ExtentTest extentTest;

    public ExtentTestResolver(final ExtentReports extentReports, SpectrumUtil spectrumUtil) {
        this.extentReports = extentReports;
        this.spectrumUtil = spectrumUtil;
    }

    @Override
    public ExtentTest resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        return extentTest;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        extentTest = createExtentTestFrom(context).info(createLabel("START TEST", getColorOf(INFO)));
        context.getRoot().getStore(GLOBAL).put(EXTENT_TEST, extentTest);
    }

    public ExtentTest createExtentTestFrom(ExtensionContext context) {
        log.debug("Creating Extent Test");
        return extentReports.createTest(String.format("<div>%s</div>%s", context.getRoot().getStore(GLOBAL).get(CLASS_NAME), context.getDisplayName()));
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
        logTestStatus(context, FAIL);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable exception) {
        extentTest.fail(exception);
        spectrumUtil.addScreenshotToReport(context.getRoot().getStore(GLOBAL).get(WEB_DRIVER, WebDriver.class), extentTest, createLabel("TEST FAILED", RED).getMarkup(), FAIL);
        logTestStatus(context, FAIL);
    }

    protected void logTestStatus(final ExtensionContext context, final Status status) {
        log.info(String.format("END execution of '%s -> %s': %s", context.getRoot().getStore(GLOBAL).get(CLASS_NAME), context.getDisplayName(), status.name()));
        extentTest.log(status, createLabel("END TEST", getColorOf(status)));
    }

    protected ExtentColor getColorOf(final Status status) {
        return switch (status) {
            case FAIL -> RED;
            case SKIP -> AMBER;
            default -> GREEN;
        };
    }
}
