package com.giuliolongfils.spectrum.extensions.watchers;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.giuliolongfils.spectrum.SpectrumTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.RED;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.giuliolongfils.spectrum.extensions.resolvers.ExtentTestResolver.*;
import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentReportsWatcher implements TestWatcher {

    @Override
    public void testDisabled(final ExtensionContext context, final Optional<String> reason) {
        createExtentTestFrom(context).skip(createLabel("Skipped: " + reason.orElse("no reason"), getColorOf(SKIP)));
        finalizeTest(context, SKIP);
    }

    @Override
    public void testSuccessful(final ExtensionContext context) {
        finalizeTest(context, PASS);
    }

    @Override
    public void testAborted(final ExtensionContext context, final Throwable throwable) {
        finalizeTest(context, FAIL);
    }

    @Override
    public void testFailed(final ExtensionContext context, final Throwable exception) {
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final SpectrumTest<?> spectrumTest = (SpectrumTest<?>) context.getRequiredTestInstance();

        store.get(EXTENT_TEST, ExtentTest.class).fail(exception);
        spectrumTest.addScreenshotToReport(createLabel("TEST FAILED", RED).getMarkup(), FAIL);
        finalizeTest(context, FAIL);
    }

    protected void finalizeTest(final ExtensionContext context, final Status status) {
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        store.get(WEB_DRIVER, WebDriver.class).quit();
        log.info(String.format("END execution of '%s -> %s': %s", context.getParent().orElseThrow().getDisplayName(), context.getDisplayName(), status.name()));
        store.get(EXTENT_TEST, ExtentTest.class).log(status, createLabel("END TEST", getColorOf(status)));
    }
}
