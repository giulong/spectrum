package com.giuliolongfils.agitation.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.giuliolongfils.agitation.config.FileReader;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.nio.file.Paths;

@Slf4j
public class ExtentReportsResolver extends TypeBasedParameterResolver<ExtentReports> implements AfterAllCallback {

    @Getter
    private final ExtentReports extentReports;
    private final String reportPath;
    private final String reportName;

    public ExtentReportsResolver(final SystemProperties systemProperties, final Configuration.Extent extent) {
        log.debug("Init Extent Reports");
        final FileReader fileReader = FileReader.getInstance();
        final String reportPath = getReportsPathFrom(systemProperties);
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

    protected static String getReportsPathFrom(final SystemProperties systemProperties) {
        return Paths.get(
                        System.getProperty("user.dir"),
                        systemProperties.getReportsFolder(),
                        systemProperties.getReportName())
                .toString()
                .replaceAll("\\\\", "/");
    }
}
