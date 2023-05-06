package com.giuliolongfils.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.giuliolongfils.spectrum.extensions.SpectrumExtension.CLASS_NAME;
import static com.giuliolongfils.spectrum.extensions.resolvers.ExtentReportsResolver.EXTENT_REPORTS;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentTestResolver extends TypeBasedParameterResolver<ExtentTest> implements TestWatcher {

    public static final String EXTENT_TEST = "extentTest";

    public static ExtentTest createExtentTestFrom(final ExtensionContext context) {
        return context.getRoot().getStore(GLOBAL)
                .get(EXTENT_REPORTS, ExtentReports.class)
                .createTest(String.format("<div>%s</div>%s", context.getStore(GLOBAL).get(CLASS_NAME), context.getDisplayName()));
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
        log.debug("Resolving Extent Test");
        final ExtentTest extentTest = createExtentTestFrom(context).info(createLabel("START TEST", getColorOf(INFO)));
        context.getStore(GLOBAL).put(EXTENT_TEST, extentTest);

        return extentTest;
    }
}
