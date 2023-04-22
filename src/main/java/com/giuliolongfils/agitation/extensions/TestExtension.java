package com.giuliolongfils.agitation.extensions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.giuliolongfils.agitation.client.Data;
import com.giuliolongfils.agitation.config.FileReader;
import com.giuliolongfils.agitation.config.YamlParser;
import com.giuliolongfils.agitation.internal.ContextManager;
import com.giuliolongfils.agitation.internal.Util;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import com.giuliolongfils.agitation.util.AgitationTest;
import com.giuliolongfils.agitation.util.AgitationUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Properties;

import static com.aventstack.extentreports.markuputils.ExtentColor.GREEN;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static com.giuliolongfils.agitation.extensions.resolvers.AgitationUtilResolver.AGITATION_UTIL;
import static com.giuliolongfils.agitation.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static com.giuliolongfils.agitation.extensions.resolvers.DataResolver.DATA;
import static com.giuliolongfils.agitation.extensions.resolvers.SystemPropertiesResolver.SYSTEM_PROPERTIES;

@Slf4j
@Getter
public class TestExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

    public static final String EXTENT_REPORTS = "extentReports";
    public static final String USER_REPORT_CSS = "/css/report.css";

    private final ObjectWriter writer = new YAMLMapper().configure(FAIL_ON_EMPTY_BEANS, false).writerWithDefaultPrettyPrinter();
    private final YamlParser yamlParser = YamlParser.builder().build();
    private final FileReader fileReader = FileReader.builder().build();
    private final AgitationUtil agitationUtil;
    private final ExtentReports extentReports;
    private final Configuration configuration;
    private final Data data;
    private final SystemProperties systemProperties;
    private final ContextManager contextManager = ContextManager.getInstance();

    @SneakyThrows
    public TestExtension() {
        Properties agitationProperties = fileReader.readProperties("/agitation.properties");
        log.info(String.format(fileReader.read("/banner.txt"), agitationProperties.getProperty("version")));

        log.debug("Building SystemProperties");
        systemProperties = yamlParser.readInternal("yaml/system-properties.default.yaml", SystemProperties.class);
        yamlParser.update(systemProperties, writer.writeValueAsString(System.getProperties()));

        log.debug("Building AgitationUtil");
        agitationUtil = AgitationUtil.builder().systemProperties(systemProperties).build();
        agitationUtil.deleteDownloadsFolder();

        log.debug("Parsing Configuration");
        String envConfiguration = String.format("configuration-%s.yaml", systemProperties.getEnv());
        configuration = yamlParser.readInternal("yaml/configuration.default.yaml", Configuration.class);
        yamlParser.updateWithFile(configuration, "configuration.yaml");
        yamlParser.updateWithFile(configuration, envConfiguration);

        log.debug("Parsing Data");
        data = yamlParser.read("data/data.yaml", Data.class);

        log.debug("Init extent report");
        extentReports = initExtentReport();

        log.trace("System properties:\n{}", writer.writeValueAsString(systemProperties));
        log.trace("Configuration:\n{}", writer.writeValueAsString(configuration));
        log.trace("Data:\n{}", writer.writeValueAsString(data));
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        contextManager.store(SYSTEM_PROPERTIES, systemProperties, context);
        contextManager.store(AGITATION_UTIL, agitationUtil, context);
        contextManager.store(CONFIGURATION, configuration, context);
        contextManager.store(EXTENT_REPORTS, extentReports, context);
        contextManager.store(DATA, data, context);

        log.info("START execution of tests in class {}", contextManager.getClassName(context));
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        contextManager.createExtentTest(context, extentReports).info(createLabel("START TEST", GREEN));
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        log.info("END execution of tests in class {}", contextManager.getClassName(context));
        extentReports.flush();

        if (Util.hasSuperclass(contextManager.getTestClass(context), AgitationTest.class)) {
            contextManager.getWebDriver(context).quit();
        }
    }

    protected ExtentReports initExtentReport() {
        final Configuration.Extent extent = configuration.getExtent();
        final ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(Util.getReportPath(systemProperties));

        htmlReporter.config().setDocumentTitle(extent.getDocumentTitle());
        htmlReporter.config().setReportName(extent.getReportName());
        htmlReporter.config().setTheme(Theme.valueOf(extent.getTheme()));
        htmlReporter.config().setTimeStampFormat(extent.getTimeStampFormat());
        htmlReporter.config().setCSS(fileReader.read(USER_REPORT_CSS));

        final ExtentReports reports = new ExtentReports();
        reports.attachReporter(htmlReporter);

        return reports;
    }
}
