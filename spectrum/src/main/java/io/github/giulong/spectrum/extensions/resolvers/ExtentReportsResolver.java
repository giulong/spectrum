package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import io.github.giulong.spectrum.utils.ExtentReporter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentReportsResolver extends TypeBasedParameterResolver<ExtentReports> {

    public static final String EXTENT_REPORTS = "extentReports";

    private final ExtentReporter extentReporter = ExtentReporter.getInstance();

    @Override
    public ExtentReports resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        return context.getRoot().getStore(GLOBAL).getOrComputeIfAbsent(EXTENT_REPORTS, e -> {
            log.debug("Resolving {}", EXTENT_REPORTS);

            return extentReporter.getExtentReports();
        }, ExtentReports.class);
    }
}
