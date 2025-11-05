package io.github.giulong.spectrum.extensions.resolvers;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import com.aventstack.extentreports.ExtentReports;

import io.github.giulong.spectrum.utils.ExtentReporter;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class ExtentReportsResolver extends TypeBasedParameterResolver<ExtentReports> {

    public static final String EXTENT_REPORTS = "extentReports";

    private final ExtentReporter extentReporter = ExtentReporter.getInstance();

    @Override
    public ExtentReports resolveParameter(@NonNull final ParameterContext parameterContext, final ExtensionContext context) {
        return context.getRoot().getStore(GLOBAL).computeIfAbsent(EXTENT_REPORTS, e -> {
            log.debug("Resolving {}", EXTENT_REPORTS);

            return extentReporter.getExtentReports();
        }, ExtentReports.class);
    }
}
