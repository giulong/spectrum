package com.giuliolongfils.agitation.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.giuliolongfils.agitation.config.FileReader;
import com.giuliolongfils.agitation.pojos.Configuration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ExtentReportsResolver extends TypeBasedParameterResolver<ExtentReports> implements AfterAllCallback {

    public static final String DEFAULT_PATTERN = "dd-MM-yyyy_HH-mm-ss";

    @Getter
    private final ExtentReports extentReports;
    private final String reportPath;
    private final String reportName;

    public ExtentReportsResolver(final Configuration.Extent extent) {
        log.debug("Init Extent Reports");
        final FileReader fileReader = FileReader.getInstance();
        final String reportPath = getReportsPathFrom(extent.getReportFolder(), extent.getFileName());
        final String reportName = extent.getReportName();
        final ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(reportPath);

        htmlReporter.config().setDocumentTitle(extent.getDocumentTitle());
        htmlReporter.config().setReportName(reportName);
        htmlReporter.config().setTheme(Theme.valueOf(extent.getTheme()));
        htmlReporter.config().setTimeStampFormat(extent.getTimeStampFormat());
        htmlReporter.config().setCSS(fileReader.read("/css/report.css"));

        final ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);

        this.extentReports = extentReports;
        this.reportPath = reportPath;
        this.reportName = reportName;
    }

    @Override
    public ExtentReports resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        return extentReports;
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        log.info("After the execution, you'll find the '{}' report at file:///{}", reportName, reportPath);
        extentReports.flush();
    }

    protected static String getReportsPathFrom(final String reportFolder, final String fileName) {
        final String timestamp = "\\{.*:?(?<pattern>.*)}";
        final Matcher matcher = Pattern.compile(timestamp).matcher(fileName);
        final String pattern = matcher.matches() ? matcher.group("pattern") : DEFAULT_PATTERN;
        final String resolvedFileName = fileName.replaceAll(timestamp, LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)));

        return Paths.get(System.getProperty("user.dir"), reportFolder, resolvedFileName).toString().replaceAll("\\\\", "/");
    }
}
