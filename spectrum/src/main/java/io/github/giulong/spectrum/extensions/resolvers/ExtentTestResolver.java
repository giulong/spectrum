package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import io.github.giulong.spectrum.SpectrumSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentTestResolver extends TypeBasedParameterResolver<ExtentTest> {

    public static final String EXTENT_TEST = "extentTest";

    public static ExtentTest createExtentTestFrom(final ExtensionContext context) {
        return SpectrumSessionListener.getExtentReports()
                .createTest(String.format("<div>%s</div>%s", context.getParent().orElseThrow().getDisplayName(), context.getDisplayName()));
    }

    public static ExtentColor getColorOf(final Status status) {
        return switch (status) {
            case FAIL -> RED;
            case SKIP -> AMBER;
            default -> GREEN;
        };
    }

    @Override
    public ExtentTest resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", EXTENT_TEST);
        final ExtentTest extentTest = createExtentTestFrom(context).info(createLabel("START TEST", getColorOf(INFO)));
        context.getStore(GLOBAL).put(EXTENT_TEST, extentTest);

        return extentTest;
    }
}
