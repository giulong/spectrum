package io.github.giulong.spectrum.utils.events;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.pojos.events.Event;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.aventstack.extentreports.Status.FAIL;
import static com.aventstack.extentreports.Status.SKIP;
import static com.aventstack.extentreports.markuputils.ExtentColor.RED;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static io.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentTestHandler extends EventHandler {

    public void handle(final Event event) {
        final ExtensionContext context = event.getContext();
        final Status status = event.getResult().getStatus();
        final ExtentTest extentTest = context.getStore(GLOBAL).getOrComputeIfAbsent(EXTENT_TEST, e -> createExtentTestFrom(context), ExtentTest.class);

        switch (status) {
            case SKIP -> {
                final String disabledValue = context.getRequiredTestMethod().getAnnotation(Disabled.class).value();
                final String reason = "".equals(disabledValue) ? "no reason" : disabledValue;
                extentTest.skip(createLabel("Skipped: " + reason, getColorOf(SKIP)));
            }
            case FAIL -> {
                final SpectrumTest<?> spectrumTest = (SpectrumTest<?>) context.getRequiredTestInstance();
                extentTest.fail(context.getExecutionException().orElse(new RuntimeException("Test Failed with no exception")));
                spectrumTest.addScreenshotToReport(createLabel("TEST FAILED", RED).getMarkup(), FAIL);
            }
            default -> extentTest.log(status, createLabel("END TEST", getColorOf(status)));
        }

        log.info(String.format("END execution of '%s -> %s': %s", event.getPrimaryId(), event.getSecondaryId(), status.name()));
    }
}
