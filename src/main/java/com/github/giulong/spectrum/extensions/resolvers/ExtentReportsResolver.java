package com.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.nio.file.Path;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentReportsResolver extends TypeBasedParameterResolver<ExtentReports> {

    public static final String EXTENT_REPORTS = "extentReports";

    @Override
    public ExtentReports resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.getOrComputeIfAbsent(EXTENT_REPORTS, e -> {
            log.debug("Resolving {}", EXTENT_REPORTS);

            final Configuration.Extent extent = rootStore.get(ConfigurationResolver.CONFIGURATION, Configuration.class).getExtent();
            final String reportPath = getReportsPathFrom(extent.getReportFolder(), extent.getFileName());
            final String reportName = extent.getReportName();
            final ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);

            sparkReporter.config().setDocumentTitle(extent.getDocumentTitle());
            sparkReporter.config().setReportName(reportName);
            sparkReporter.config().setTheme(Theme.valueOf(extent.getTheme()));
            sparkReporter.config().setTimeStampFormat(extent.getTimeStampFormat());
            sparkReporter.config().setCss(FileUtils.getInstance().read("/css/report.css"));

            final ExtentReports extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);

            rootStore.put(EXTENT_REPORTS, extentReports);
            log.info("After the execution, you'll find the '{}' report at file:///{}", reportName, reportPath);
            return extentReports;
        }, ExtentReports.class);
    }

    protected static String getReportsPathFrom(final String reportFolder, final String fileName) {
        final String resolvedFileName = FileUtils.getInstance().interpolateTimestampFrom(fileName);
        return Path.of(System.getProperty("user.dir"), reportFolder, resolvedFileName).toString().replaceAll("\\\\", "/");
    }
}
