package com.giuliolongfils.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.giuliolongfils.spectrum.util.SpectrumUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.giuliolongfils.spectrum.extensions.SpectrumExtension.CLASS_NAME;
import static com.giuliolongfils.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static com.giuliolongfils.spectrum.extensions.resolvers.SpectrumUtilResolver.SPECTRUM_UTIL;
import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentTestResolver extends TypeBasedParameterResolver<ExtentTest> implements TestWatcher {

    public static final String EXTENT_TEST = "extentTest";

    @Override
    public ExtentTest resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        final ExtentTest extentTest = createExtentTestFrom(context).info(createLabel("START TEST", getColorOf(INFO)));
        context.getStore(GLOBAL).put(EXTENT_TEST, extentTest);

        return extentTest;
    }

    public ExtentTest createExtentTestFrom(final ExtensionContext context) {
        log.debug("Creating Extent Test");
        return context.getRoot().getStore(GLOBAL)
                .get(EXTENT_REPORTS, ExtentReports.class)
                .createTest(String.format("<div>%s</div>%s", context.getStore(GLOBAL).get(CLASS_NAME), context.getDisplayName()));
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
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final ExtentTest extentTest = store.get(EXTENT_TEST, ExtentTest.class);
        final SpectrumUtil spectrumUtil = store.get(SPECTRUM_UTIL, SpectrumUtil.class);
        extentTest.fail(exception);
        spectrumUtil.addScreenshotToReport(store.get(WEB_DRIVER, WebDriver.class), extentTest, createLabel("TEST FAILED", RED).getMarkup(), FAIL);
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
