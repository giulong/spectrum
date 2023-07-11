package io.github.giulong.spectrum;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.ExtentSparkReporterConfig;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.testbook.TestBookTest;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.YamlUtils;
import io.github.giulong.spectrum.utils.events.EventHandler;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import io.github.giulong.spectrum.utils.testbook.parsers.TestBookParser;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.launcher.LauncherSession;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.SpectrumSessionListener.*;
import static io.github.giulong.spectrum.enums.EventReason.AFTER;
import static io.github.giulong.spectrum.enums.EventReason.BEFORE;
import static io.github.giulong.spectrum.enums.EventTag.SUITE;
import static io.github.giulong.spectrum.enums.Result.NOT_RUN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpectrumSessionListener")
class SpectrumSessionListenerTest {

    private static MockedStatic<FileUtils> fileUtilsMockedStatic;
    private static MockedStatic<YamlUtils> yamlUtilsMockedStatic;
    private static MockedStatic<FreeMarkerWrapper> freeMarkerWrapperMockedStatic;
    private static MockedStatic<EventsDispatcher> eventsDispatcherMockedStatic;
    private static MockedStatic<SystemUtils> systemUtilsMockedStatic;

    private String osName;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private YamlUtils yamlUtils;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Extent extent;

    @Mock
    private ExtentSparkReporterConfig extentSparkReporterConfig;

    @Mock
    private TestBook testBook;

    @Mock
    private TestBookParser testBookParser;

    @Mock
    private TestBookTest test;

    @Mock
    private LauncherSession launcherSession;

    @Mock
    private ExtentReports extentReports;

    @Mock
    private EventsDispatcher.EventsDispatcherBuilder eventsDispatcherBuilder;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @Mock
    private Properties properties;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private List<EventHandler> handlers;

    @Mock
    private Configuration.FreeMarker freeMarker;

    @BeforeEach
    public void beforeEach() {
        osName = System.getProperty("os.name");
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        yamlUtilsMockedStatic = mockStatic(YamlUtils.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        eventsDispatcherMockedStatic = mockStatic(EventsDispatcher.class);
        systemUtilsMockedStatic = mockStatic(SystemUtils.class);
    }

    @AfterEach
    public void afterEach() {
        fileUtilsMockedStatic.close();
        yamlUtilsMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
        eventsDispatcherMockedStatic.close();
        systemUtilsMockedStatic.close();
        VARS.clear();
        System.setProperty("os.name", osName);
    }

    @Test
    @DisplayName("launcherSessionOpened should log the banner and initialize Spectrum")
    public void launcherSessionOpened() {
        final String env = "env";
        final String envConfiguration = String.format("configuration-%s.yaml", env);
        final String banner = "banner";
        final String version = "version";
        final Map<String, TestBookTest> mappedTests = new HashMap<>();
        final TestBookTest test1 = TestBookTest.builder()
                .className("test 1")
                .testName("one")
                .build();

        final TestBookTest test2 = TestBookTest.builder()
                .className("another test")
                .testName("another")
                .build();
        final List<TestBookTest> tests = List.of(test1, test2);

        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String reportName = "reportName";
        final String documentTitle = "documentTitle";
        final String theme = "DARK";
        final String timeStampFormat = "timeStampFormat";
        final String css = "css";

        System.setProperty("os.name", "Win");

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(extent.getReportName()).thenReturn(reportName);
        when(extent.getDocumentTitle()).thenReturn(documentTitle);
        when(extent.getTheme()).thenReturn(theme);
        when(extent.getTimeStampFormat()).thenReturn(timeStampFormat);
        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.read("/css/report.css")).thenReturn(css);
        when(fileUtils.interpolateTimestampFrom(fileName)).thenReturn(fileName);

        MockedConstruction<ExtentReports> extentReportsMockedConstruction = mockConstruction(ExtentReports.class);
        MockedConstruction<ExtentSparkReporter> extentSparkReporterMockedConstruction = mockConstruction(ExtentSparkReporter.class, (mock, context) -> {
            assertEquals(Path.of(reportFolder, fileName).toString().replace("\\", "/"), context.arguments().get(0));
            when(mock.config()).thenReturn(extentSparkReporterConfig);
        });

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.readProperties("/spectrum.properties")).thenReturn(properties);
        when(fileUtils.read("/banner.txt")).thenReturn(banner);
        when(properties.getProperty("version")).thenReturn(version);
        when(configuration.getTestBook()).thenReturn(testBook);
        when(testBook.getMappedTests()).thenReturn(mappedTests);
        when(testBook.getParser()).thenReturn(testBookParser);
        when(testBookParser.parse()).thenReturn(tests);

        when(YamlUtils.getInstance()).thenReturn(yamlUtils);
        when(yamlUtils.readInternalNode(ENV_NODE, CONFIGURATION_YAML, String.class)).thenReturn(env);
        when(yamlUtils.readInternalNode(ENV_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultEnv");
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));
        when(yamlUtils.readInternal(DEFAULT_CONFIGURATION_YAML, Configuration.class)).thenReturn(configuration);

        when(FreeMarkerWrapper.getInstance()).thenReturn(freeMarkerWrapper);
        when(configuration.getFreeMarker()).thenReturn(freeMarker);

        when(configuration.getEventHandlers()).thenReturn(handlers);
        when(EventsDispatcher.builder()).thenReturn(eventsDispatcherBuilder);
        when(eventsDispatcherBuilder.handlers(handlers)).thenReturn(eventsDispatcherBuilder);
        when(eventsDispatcherBuilder.build()).thenReturn(eventsDispatcher);

        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        SpectrumSessionListener.configuration = configuration;
        SpectrumSessionListener.extentReports = extentReports;
        SpectrumSessionListener.eventsDispatcher = eventsDispatcher;
        spectrumSessionListener.launcherSessionOpened(launcherSession);

        assertEquals(configuration, SpectrumSessionListener.getConfiguration());
        assertEquals(2, mappedTests.size());
        mappedTests.values().stream().map(TestBookTest::getResult).forEach(result -> assertEquals(NOT_RUN, result));

        verify(yamlUtils).updateWithFile(configuration, CONFIGURATION_YAML);
        verify(yamlUtils).updateWithFile(configuration, envConfiguration);
        verify(freeMarkerWrapper).setupFrom(freeMarker);

        verify(extentSparkReporterConfig).setDocumentTitle(documentTitle);
        verify(extentSparkReporterConfig).setReportName(reportName);
        verify(extentSparkReporterConfig).setTheme(Theme.DARK);
        verify(extentSparkReporterConfig).setTimeStampFormat(timeStampFormat);
        verify(extentSparkReporterConfig).setCss(css);

        final ExtentReports extentReports = extentReportsMockedConstruction.constructed().get(0);
        verify(extentReports).attachReporter(extentSparkReporterMockedConstruction.constructed().toArray(new ExtentSparkReporter[0]));
        assertEquals(extentReports, SpectrumSessionListener.getExtentReports());

        assertEquals(eventsDispatcher, SpectrumSessionListener.getEventsDispatcher());

        extentSparkReporterMockedConstruction.close();
        extentReportsMockedConstruction.close();

        verify(eventsDispatcher).dispatch(BEFORE, Set.of(SUITE));
    }

    @Test
    @DisplayName("launcherSessionClosed should flush the testbook and the extent report")
    public void launcherSessionClosed() {
        when(configuration.getTestBook()).thenReturn(testBook);

        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        SpectrumSessionListener.configuration = configuration;
        SpectrumSessionListener.extentReports = extentReports;
        SpectrumSessionListener.eventsDispatcher = eventsDispatcher;
        spectrumSessionListener.launcherSessionClosed(launcherSession);

        verify(testBook).flush();
        verify(extentReports).flush();
        verify(eventsDispatcher).dispatch(AFTER, Set.of(SUITE));
    }

    @Test
    @DisplayName("launcherSessionClosed should check if the testbook is not null")
    public void launcherSessionClosedTestBookNull() {
        when(configuration.getTestBook()).thenReturn(null);

        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        SpectrumSessionListener.configuration = configuration;
        SpectrumSessionListener.extentReports = extentReports;
        SpectrumSessionListener.eventsDispatcher = eventsDispatcher;
        spectrumSessionListener.launcherSessionClosed(launcherSession);

        verify(testBook, never()).flush();
        verify(extentReports).flush();
        verify(eventsDispatcher).dispatch(AFTER, Set.of(SUITE));
    }

    @Test
    @DisplayName("parseConfiguration should parse all the configurations considering the active env")
    public void parseConfiguration() {
        final String env = "env";
        final String envConfiguration = String.format("configuration-%s.yaml", env);

        System.setProperty("os.name", "Win");

        when(YamlUtils.getInstance()).thenReturn(yamlUtils);

        // parseEnv
        when(yamlUtils.readInternalNode(ENV_NODE, CONFIGURATION_YAML, String.class)).thenReturn(env);
        when(yamlUtils.readInternalNode(ENV_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultEnv");

        // parseVars
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        when(yamlUtils.readInternal(DEFAULT_CONFIGURATION_YAML, Configuration.class)).thenReturn(configuration);

        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        spectrumSessionListener.parseConfiguration();

        verify(yamlUtils).updateWithFile(configuration, CONFIGURATION_YAML);
        verify(yamlUtils).updateWithFile(configuration, envConfiguration);

        assertEquals(configuration, SpectrumSessionListener.getConfiguration());
    }

    @Test
    @DisplayName("parseConfiguration should parse all the configurations considering also the internal configuration.default.unix.yaml")
    public void parseConfigurationUnix() {
        final String env = "env";
        final String envConfiguration = String.format("configuration-%s.yaml", env);

        System.setProperty("os.name", "nix");

        when(YamlUtils.getInstance()).thenReturn(yamlUtils);

        // parseEnv
        when(yamlUtils.readInternalNode(ENV_NODE, CONFIGURATION_YAML, String.class)).thenReturn(env);
        when(yamlUtils.readInternalNode(ENV_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultEnv");

        // parseVars
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        when(yamlUtils.readInternal(DEFAULT_CONFIGURATION_YAML, Configuration.class)).thenReturn(configuration);

        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        spectrumSessionListener.parseConfiguration();

        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_UNIX_YAML);
        verify(yamlUtils).updateWithFile(configuration, CONFIGURATION_YAML);
        verify(yamlUtils).updateWithFile(configuration, envConfiguration);

        assertEquals(configuration, SpectrumSessionListener.getConfiguration());
    }

    @DisplayName("parseEnv should parse the env node from both the default configuration.yaml and the environment-specific and return the merged value")
    @ParameterizedTest(name = "with env {0} and default env {1} we expect {2}")
    @MethodSource("envValuesProvider")
    public void parseEnv(final String env, final String defaultEnv, final String expected) {
        when(YamlUtils.getInstance()).thenReturn(yamlUtils);
        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();

        when(yamlUtils.readInternalNode(ENV_NODE, CONFIGURATION_YAML, String.class)).thenReturn(env);
        when(yamlUtils.readInternalNode(ENV_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn(defaultEnv);

        assertEquals(expected, spectrumSessionListener.parseEnv());
    }

    public static Stream<Arguments> envValuesProvider() {
        return Stream.of(
                arguments("overridden-env", "default-env", "overridden-env"),
                arguments(null, "default-env", "default-env")
        );
    }

    @DisplayName("parseVars should put in the VARS map all the variables read from the configuration yaml files")
    @ParameterizedTest
    @MethodSource("varsValuesProvider")
    public void parseVars(final Map<String, String> defaultVars, final Map<String, String> vars, final Map<String, String> envVars, final Map<String, String> expected) {
        final String envConfiguration = "envConfiguration";

        System.setProperty("os.name", "Win");
        when(YamlUtils.getInstance()).thenReturn(yamlUtils);
        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();

        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readNode(VARS_NODE, CONFIGURATION_YAML, Map.class)).thenReturn(vars);
        when(yamlUtils.readNode(VARS_NODE, envConfiguration, Map.class)).thenReturn(envVars);

        spectrumSessionListener.parseVars(envConfiguration);
        assertEquals(expected, VARS);
    }

    @DisplayName("parseVars should put in the VARS map also those read from the internal configuration.default.unix.yaml")
    @ParameterizedTest
    @MethodSource("varsValuesProvider")
    public void parseVarsUnix(final Map<String, String> defaultVars, final Map<String, String> vars, final Map<String, String> envVars, final Map<String, String> expected) {
        final String envConfiguration = "envConfiguration";

        System.setProperty("os.name", "nix");
        when(YamlUtils.getInstance()).thenReturn(yamlUtils);
        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();

        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readNode(VARS_NODE, CONFIGURATION_YAML, Map.class)).thenReturn(vars);
        when(yamlUtils.readNode(VARS_NODE, envConfiguration, Map.class)).thenReturn(envVars);

        spectrumSessionListener.parseVars(envConfiguration);
        assertEquals(expected, VARS);
    }

    public static Stream<Arguments> varsValuesProvider() {
        return Stream.of(
                arguments(Map.of("one", "one"), null, null, Map.of("one", "one")),
                arguments(Map.of("one", "one"), Map.of("two", "two"), null, Map.of("one", "one", "two", "two")),
                arguments(Map.of("one", "one"), null, Map.of("three", "three"), Map.of("one", "one", "three", "three")),
                arguments(Map.of("one", "one"), Map.of("two", "two"), Map.of("three", "three"), Map.of("one", "one", "two", "two", "three", "three"))
        );
    }

    @Test
    @DisplayName("parseTestBook should initialise the testbook")
    public void parseTestBook() {
        final Map<String, TestBookTest> mappedTests = new HashMap<>();
        final TestBookTest test1 = TestBookTest.builder()
                .className("test 1")
                .testName("one")
                .build();

        final TestBookTest test2 = TestBookTest.builder()
                .className("another test")
                .testName("another")
                .build();
        final List<TestBookTest> tests = List.of(test1, test2);

        when(configuration.getTestBook()).thenReturn(testBook);
        when(testBook.getMappedTests()).thenReturn(mappedTests);
        when(testBook.getParser()).thenReturn(testBookParser);
        when(testBookParser.parse()).thenReturn(tests);

        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        SpectrumSessionListener.configuration = configuration;
        spectrumSessionListener.parseTestBook();

        assertEquals(2, mappedTests.size());
        mappedTests.values().stream().map(TestBookTest::getResult).forEach(result -> assertEquals(NOT_RUN, result));
    }

    @Test
    @DisplayName("parseTestBook should do nothing if not provided in the configuration.yaml")
    public void parseTestBookNull() {
        when(configuration.getTestBook()).thenReturn(null);

        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        SpectrumSessionListener.configuration = configuration;
        spectrumSessionListener.parseTestBook();
    }

    @DisplayName("updateGroupedTests should add the provided test to the provided map of grouped tests")
    @ParameterizedTest(name = "with className {0} and grouped tests {1}")
    @MethodSource("valuesProvider")
    public void updateGroupedTests(final String className, final Map<String, Set<TestBookTest>> groupedTests) {
        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        spectrumSessionListener.updateGroupedTests(groupedTests, className, test);

        assertTrue(groupedTests.get(className).contains(test));
    }

    public static Stream<Arguments> valuesProvider() throws IOException {
        return Stream.of(
                arguments("className", new HashMap<>()),
                arguments("className", new HashMap<>() {{
                    put("className", new HashSet<>());
                }})
        );
    }

    @Test
    @DisplayName("initExtentReports should initialize and return the ExtentReports by reading it config")
    public void initExtentReports() {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String reportName = "reportName";
        final String documentTitle = "documentTitle";
        final String theme = "DARK";
        final String timeStampFormat = "timeStampFormat";
        final String css = "css";

        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(reportFolder);
        when(extent.getFileName()).thenReturn(fileName);
        when(extent.getReportName()).thenReturn(reportName);
        when(extent.getDocumentTitle()).thenReturn(documentTitle);
        when(extent.getTheme()).thenReturn(theme);
        when(extent.getTimeStampFormat()).thenReturn(timeStampFormat);
        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.read("/css/report.css")).thenReturn(css);
        when(fileUtils.interpolateTimestampFrom(fileName)).thenReturn(fileName);

        MockedConstruction<ExtentReports> extentReportsMockedConstruction = mockConstruction(ExtentReports.class);
        MockedConstruction<ExtentSparkReporter> extentSparkReporterMockedConstruction = mockConstruction(ExtentSparkReporter.class, (mock, context) -> {
            assertEquals(Path.of(reportFolder, fileName).toString().replace("\\", "/"), context.arguments().get(0));
            when(mock.config()).thenReturn(extentSparkReporterConfig);
        });

        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();
        SpectrumSessionListener.configuration = configuration;
        spectrumSessionListener.initExtentReports();

        verify(extentSparkReporterConfig).setDocumentTitle(documentTitle);
        verify(extentSparkReporterConfig).setReportName(reportName);
        verify(extentSparkReporterConfig).setTheme(Theme.DARK);
        verify(extentSparkReporterConfig).setTimeStampFormat(timeStampFormat);
        verify(extentSparkReporterConfig).setCss(css);

        final ExtentReports extentReports = extentReportsMockedConstruction.constructed().get(0);
        verify(extentReports).attachReporter(extentSparkReporterMockedConstruction.constructed().toArray(new ExtentSparkReporter[0]));
        assertEquals(extentReports, SpectrumSessionListener.getExtentReports());

        extentSparkReporterMockedConstruction.close();
        extentReportsMockedConstruction.close();
    }

    @Test
    @DisplayName("getReportsPathFrom should return the full path of the report")
    public void getReportsPathFrom() {
        final String reportFolder = "reportFolder";
        final String fileName = "fileName";
        final String expected = reportFolder + "/" + fileName;

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();

        when(fileUtils.interpolateTimestampFrom(fileName)).thenReturn(fileName);

        final String actual = spectrumSessionListener.getReportsPathFrom(reportFolder, fileName);
        assertTrue(actual.matches(Path.of(expected).toString().replace("\\", "/")));
    }

    @Test
    @DisplayName("initEventsDispatcher should build the events dispatcher with all the event handlers configured")
    public void initEventsDispatcher() {
        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();

        when(configuration.getEventHandlers()).thenReturn(handlers);
        when(EventsDispatcher.builder()).thenReturn(eventsDispatcherBuilder);
        when(eventsDispatcherBuilder.handlers(handlers)).thenReturn(eventsDispatcherBuilder);
        when(eventsDispatcherBuilder.build()).thenReturn(eventsDispatcher);

        SpectrumSessionListener.configuration = configuration;
        spectrumSessionListener.initEventsDispatcher();

        assertEquals(eventsDispatcher, SpectrumSessionListener.getEventsDispatcher());
    }

    @DisplayName("isUnix should check the OS")
    @ParameterizedTest(name = "with OS {0} we expect {1}")
    @MethodSource("isUnixValuesProvider")
    public void isUnix(final String osName, final boolean expected) {
        final SpectrumSessionListener spectrumSessionListener = new SpectrumSessionListener();

        System.setProperty("os.name", osName);

        assertEquals(expected, spectrumSessionListener.isUnix());
    }

    public static Stream<Arguments> isUnixValuesProvider() {
        return Stream.of(
                arguments("nix", true),
                arguments("blah", true),
                arguments("Win", false),
                arguments("WiN", false)
        );
    }
}
