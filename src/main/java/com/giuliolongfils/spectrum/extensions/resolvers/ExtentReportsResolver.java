package com.giuliolongfils.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.giuliolongfils.spectrum.utils.FileReader;
import com.giuliolongfils.spectrum.pojos.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentReportsResolver extends TypeBasedParameterResolver<ExtentReports> {

    public static final String EXTENT_REPORTS = "extentReports";
    public static final String DEFAULT_PATTERN = "dd-MM-yyyy_HH-mm-ss";

    @Override
    public ExtentReports resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.getOrComputeIfAbsent(EXTENT_REPORTS, e -> {
            log.debug("Resolving {}", EXTENT_REPORTS);

            final Configuration.Extent extent = rootStore.get(CONFIGURATION, Configuration.class).getExtent();
            final String reportsPath = getReportsPathFrom(extent.getReportFolder(), extent.getFileName());
            final String reportsName = extent.getReportName();
            final ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportsPath);

            sparkReporter.config().setDocumentTitle(extent.getDocumentTitle());
            sparkReporter.config().setReportName(reportsName);
            sparkReporter.config().setTheme(Theme.valueOf(extent.getTheme()));
            sparkReporter.config().setTimeStampFormat(extent.getTimeStampFormat());
            sparkReporter.config().setCss(FileReader.getInstance().read("/css/report.css"));

            final ExtentReports extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);

            rootStore.put(EXTENT_REPORTS, extentReports);
            log.info("After the execution, you'll find the '{}' report at file:///{}", reportsName, reportsPath);
            return extentReports;
        }, ExtentReports.class);
    }

    protected static String getReportsPathFrom(final String reportFolder, final String fileName) {
        final String timestamp = "\\{.*:?(?<pattern>.*)}";
        final Matcher matcher = Pattern.compile(timestamp).matcher(fileName);
        final String pattern = matcher.matches() ? matcher.group("pattern") : DEFAULT_PATTERN;
        final String resolvedFileName = fileName.replaceAll(timestamp, LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)));

        return Paths.get(System.getProperty("user.dir"), reportFolder, resolvedFileName).toString().replaceAll("\\\\", "/");
    }
}
