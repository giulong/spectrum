package io.github.giulong.spectrum.exceptions;

import static com.aventstack.extentreports.markuputils.ExtentColor.RED;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;

import io.github.giulong.spectrum.SpectrumTest;

import org.junit.jupiter.api.extension.ExtensionContext;

public class TestFailedException extends RuntimeException {

    public TestFailedException(final String message) {
        super(message);
    }

    public void showInReportFrom(final ExtensionContext context) {
        ((SpectrumTest<?>) context.getRequiredTestInstance()).screenshotFail(getReportDetails());
    }

    String getReportDetails() {
        return createLabel("TEST FAILED", RED).getMarkup();
    }
}
