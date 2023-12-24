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
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.getOrComputeIfAbsent(EXTENT_REPORTS, e -> {
            log.debug("Resolving {}", EXTENT_REPORTS);

            final ExtentReports extentReports = extentReporter.getExtentReports();
            rootStore.put(EXTENT_REPORTS, extentReports);
            return extentReports;
        }, ExtentReports.class);
    }
}
