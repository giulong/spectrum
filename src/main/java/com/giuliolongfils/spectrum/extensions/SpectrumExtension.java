package com.giuliolongfils.spectrum.extensions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.giuliolongfils.spectrum.utils.FileReader;
import com.giuliolongfils.spectrum.SpectrumTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.RED;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.giuliolongfils.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static com.giuliolongfils.spectrum.extensions.resolvers.ExtentTestResolver.*;
import static com.giuliolongfils.spectrum.extensions.resolvers.WebDriverResolver.WEB_DRIVER;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class SpectrumExtension implements TestWatcher, BeforeAllCallback, AfterAllCallback {

    public static final String CLASS_NAME = "className";

    public SpectrumExtension() {
        log.debug("Building SpectrumExtension");
        final FileReader fileReader = FileReader.getInstance();
        final Properties spectrumProperties = fileReader.readProperties("/spectrum.properties");
        log.info(String.format(Objects.requireNonNull(fileReader.read("/banner.txt")), spectrumProperties.getProperty("version")));
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        final String className = context.getDisplayName();
        log.info("START execution of tests in class {}", className);
        context.getStore(GLOBAL).put(CLASS_NAME, className);
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        log.info("END execution of tests in class {}", context.getStore(GLOBAL).get(CLASS_NAME));
        context.getRoot().getStore(GLOBAL).get(EXTENT_REPORTS, ExtentReports.class).flush();
    }

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
        final ExtentTest extentTest = store.get(EXTENT_TEST, ExtentTest.class);
        final SpectrumTest<?> spectrumTest = (SpectrumTest<?>) context.getRequiredTestInstance();

        extentTest.fail(exception);
        spectrumTest.addScreenshotToReport(store.get(WEB_DRIVER, WebDriver.class), extentTest, createLabel("TEST FAILED", RED).getMarkup(), FAIL);
        finalizeTest(context, FAIL);
    }

    protected void finalizeTest(final ExtensionContext context, final Status status) {
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        store.get(WEB_DRIVER, WebDriver.class).quit();
        log.info(String.format("END execution of '%s -> %s': %s", store.get(CLASS_NAME), context.getDisplayName(), status.name()));
        store.get(EXTENT_TEST, ExtentTest.class).log(status, createLabel("END TEST", getColorOf(status)));
    }
}
