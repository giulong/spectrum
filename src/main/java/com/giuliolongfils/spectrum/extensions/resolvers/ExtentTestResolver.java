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

    public ExtentTestResolver(final ExtentReports extentReports, final SpectrumUtil spectrumUtil) {
        this.extentReports = extentReports;
        this.spectrumUtil = spectrumUtil;
    }

    @Override
    public ExtentTest resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        return context.getStore(GLOBAL).get(EXTENT_TEST, ExtentTest.class);
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        context.getStore(GLOBAL).put(EXTENT_TEST, createExtentTestFrom(context).info(createLabel("START TEST", getColorOf(INFO))));
    }

    public ExtentTest createExtentTestFrom(final ExtensionContext context) {
        log.debug("Creating Extent Test");
        return extentReports.createTest(String.format("<div>%s</div>%s", context.getStore(GLOBAL).get(CLASS_NAME), context.getDisplayName()));
    }

    @Override
    public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
        createExtentTestFrom(context).skip(createLabel("Skipped: " + reason.orElse("no reason"), getColorOf(SKIP)));
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {
        logTestStatus(context, PASS);
    }

    @Override
    public void testAborted(final ExtensionContext context, final Throwable throwable) {
        logTestStatus(context, FAIL);
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable exception) {
        final ExtentTest extentTest = context.getStore(GLOBAL).get(EXTENT_TEST, ExtentTest.class);
        extentTest.fail(exception);
        spectrumUtil.addScreenshotToReport(context.getStore(GLOBAL).get(WEB_DRIVER, WebDriver.class), extentTest, createLabel("TEST FAILED", RED).getMarkup(), FAIL);
        logTestStatus(context, FAIL);
    }

    protected void logTestStatus(final ExtensionContext context, final Status status) {
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        log.info(String.format("END execution of '%s -> %s': %s", store.get(CLASS_NAME), context.getDisplayName(), status.name()));
        store.get(EXTENT_TEST, ExtentTest.class).log(status, createLabel("END TEST", getColorOf(status)));
    }

    protected ExtentColor getColorOf(final Status status) {
        return switch (status) {
            case FAIL -> RED;
            case SKIP -> AMBER;
            default -> GREEN;
        };
    }
}
