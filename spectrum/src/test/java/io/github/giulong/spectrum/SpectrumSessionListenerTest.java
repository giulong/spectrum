package io.github.giulong.spectrum;

import io.github.giulong.spectrum.types.ProjectProperties;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.environments.Environment;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.SpectrumSessionListener.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

class SpectrumSessionListenerTest {

    private MockedStatic<SLF4JBridgeHandler> slf4JBridgeHandlerMockedStatic;

    private String osName;

    @Mock
    private ExtentReporter extentReporter;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private YamlUtils yamlUtils;

    @Mock
    private Configuration configuration;

    @Mock
    private LauncherSession launcherSession;

    @Mock
    private Launcher launcher;

    @Mock
    private EventsDispatcher eventsDispatcher;

    @Mock
    private ProjectProperties projectProperties;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private TestBook testBook;

    @Mock
    private Summary summary;

    @Mock
    private Configuration.Runtime runtime;

    @Mock
    private Environment environment;

    @Mock
    private SummaryGeneratingListener summaryGeneratingListener;

    @Mock
    private MetadataManager metadataManager;

    @Mock
    private HtmlUtils htmlUtils;

    @InjectMocks
    private SpectrumSessionListener spectrumSessionListener;

    @BeforeEach
    void beforeEach() {
        osName = System.getProperty("os.name");
        slf4JBridgeHandlerMockedStatic = mockStatic(SLF4JBridgeHandler.class);

        Reflections.setField("yamlUtils", spectrumSessionListener, yamlUtils);
        Reflections.setField("fileUtils", spectrumSessionListener, fileUtils);
        Reflections.setField("freeMarkerWrapper", spectrumSessionListener, freeMarkerWrapper);
        Reflections.setField("extentReporter", spectrumSessionListener, extentReporter);
        Reflections.setField("configuration", spectrumSessionListener, configuration);
        Reflections.setField("eventsDispatcher", spectrumSessionListener, eventsDispatcher);
        Reflections.setField("metadataManager", spectrumSessionListener, metadataManager);
        Reflections.setField("htmlUtils", spectrumSessionListener, htmlUtils);
    }

    @AfterEach
    void afterEach() {
        slf4JBridgeHandlerMockedStatic.close();
        Vars.getInstance().clear();
        System.setProperty("os.name", osName);
    }

    @Test
    @DisplayName("launcherSessionOpened should log the banner and initialize Spectrum")
    void launcherSessionOpened() {
        final String profile = "profile";
        final String profileConfiguration = String.format("configuration-%s", profile);
        final String banner = "banner";
        final String interpolatedBanner = "interpolatedBanner";

        System.setProperty("os.name", "Win");

        when(fileUtils.read("banner.txt")).thenReturn(banner);
        when(yamlUtils.readInternal("properties.yaml", ProjectProperties.class)).thenReturn(projectProperties);
        when(freeMarkerWrapper.interpolate(banner, projectProperties)).thenReturn(interpolatedBanner);

        when(yamlUtils.readClientNode(PROFILE_NODE, CONFIGURATION, String.class)).thenReturn(profile);
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultProfile");
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        when(launcherSession.getLauncher()).thenReturn(launcher);
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getEnvironment()).thenReturn(environment);
        when(configuration.getTestBook()).thenReturn(testBook);
        when(configuration.getSummary()).thenReturn(summary);
        when(summary.getSummaryGeneratingListener()).thenReturn(summaryGeneratingListener);

        spectrumSessionListener.launcherSessionOpened(launcherSession);

        slf4JBridgeHandlerMockedStatic.verify(SLF4JBridgeHandler::removeHandlersForRootLogger);
        slf4JBridgeHandlerMockedStatic.verify(SLF4JBridgeHandler::install);

        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);
        verify(yamlUtils).updateWithClientFile(configuration, CONFIGURATION);
        verify(yamlUtils).updateWithClientFile(configuration, profileConfiguration);

        verify(launcher).registerTestExecutionListeners(summaryGeneratingListener);
        verify(environment).sessionOpened();
        verify(testBook).sessionOpened();
        verify(summary).sessionOpened();
        verify(extentReporter).sessionOpened();
        verify(freeMarkerWrapper).sessionOpened();
        verify(eventsDispatcher).sessionOpened();
        verify(htmlUtils).sessionOpened();
    }

    @Test
    @DisplayName("launcherSessionClosed should flush the testbook and the extent report")
    void launcherSessionClosed() {
        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getEnvironment()).thenReturn(environment);
        when(configuration.getTestBook()).thenReturn(testBook);
        when(configuration.getSummary()).thenReturn(summary);

        final InOrder inOrder = inOrder(environment, testBook, summary, metadataManager, extentReporter, eventsDispatcher);

        spectrumSessionListener.launcherSessionClosed(launcherSession);

        inOrder.verify(environment).sessionClosed();
        inOrder.verify(testBook).sessionClosed();
        inOrder.verify(summary).sessionClosed();
        inOrder.verify(metadataManager).sessionClosed();
        inOrder.verify(extentReporter).sessionClosed();
        inOrder.verify(eventsDispatcher).sessionClosed();
    }

    @Test
    @DisplayName("parseConfiguration should parse all the configurations considering no active profile")
    void parseConfigurationNoProfile() {
        System.setProperty("os.name", "Win");

        // parseProfile
        when(yamlUtils.readClientNode(PROFILE_NODE, CONFIGURATION, String.class)).thenReturn("");
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultProfile");

        // parseVars
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        spectrumSessionListener.parseConfiguration();

        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);
        verify(yamlUtils).updateWithClientFile(configuration, CONFIGURATION);
    }

    @Test
    @DisplayName("parseConfiguration should parse all the configurations considering the active profile")
    void parseConfiguration() {
        final String profile = "profile";
        final String profileConfiguration = String.format("configuration-%s", profile);

        System.setProperty("os.name", "Win");

        // parseProfile
        when(yamlUtils.readClientNode(PROFILE_NODE, CONFIGURATION, String.class)).thenReturn(profile);
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultProfile");

        // parseVars
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        spectrumSessionListener.parseConfiguration();

        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);
        verify(yamlUtils).updateWithClientFile(configuration, CONFIGURATION);
        verify(yamlUtils).updateWithClientFile(configuration, profileConfiguration);
    }

    @Test
    @DisplayName("parseConfiguration should parse all the configurations considering also the internal configuration.default.unix.yaml")
    void parseConfigurationUnix() {
        final String profile = "profile";
        final String profileConfiguration = String.format("configuration-%s", profile);

        System.setProperty("os.name", "nix");

        // parseProfile
        when(yamlUtils.readClientNode(PROFILE_NODE, CONFIGURATION, String.class)).thenReturn(profile);
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultProfile");

        // parseVars
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        spectrumSessionListener.parseConfiguration();

        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);
        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_UNIX_YAML);
        verify(yamlUtils).updateWithClientFile(configuration, CONFIGURATION);
        verify(yamlUtils).updateWithClientFile(configuration, profileConfiguration);
    }

    @DisplayName("parseProfiles should parse the profile node from both the internal configuration.yaml and the base configuration.yaml and return the merged value")
    @ParameterizedTest(name = "with profile {0} and default profile {1} we expect {2}")
    @MethodSource("profilesValuesProvider")
    void parseProfiles(final String profile, final String defaultProfile, final List<String> expected) {
        when(yamlUtils.readClientNode(PROFILE_NODE, CONFIGURATION, String.class)).thenReturn(profile);
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn(defaultProfile);

        assertEquals(expected, spectrumSessionListener.parseProfiles());
    }

    static Stream<Arguments> profilesValuesProvider() {
        return Stream.of(
                arguments("overridden-profile", "default-profile,first", List.of("overridden-profile")),
                arguments("overridden-profile,, ", "default-profile,first", List.of("overridden-profile")),
                arguments("overridden-profile,second", "default-profile", List.of("overridden-profile", "second")),
                arguments(null, "default-profile", List.of("default-profile")),
                arguments(null, "default-profile,another", List.of("default-profile", "another")),
                arguments(null, " ,,default-profile,another", List.of("default-profile", "another"))
        );
    }

    @DisplayName("parseVars should put in the VARS map all the variables read from the configuration yaml files")
    @ParameterizedTest
    @MethodSource("varsValuesProvider")
    void parseVars(final Map<String, String> defaultVars, final Map<String, String> vars, final Map<String, String> envVars, final Map<String, String> expected) {
        final String profileConfiguration = "profileConfiguration";

        System.setProperty("os.name", "Win");

        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readClientNode(VARS_NODE, CONFIGURATION, Map.class)).thenReturn(vars);
        when(yamlUtils.readClientNode(VARS_NODE, profileConfiguration, Map.class)).thenReturn(envVars);

        spectrumSessionListener.parseVars(profileConfiguration);
        assertEquals(expected, Vars.getInstance());
    }

    @DisplayName("parseVars should put in the VARS map also those read from the internal configuration.default.unix.yaml")
    @ParameterizedTest
    @MethodSource("varsValuesProvider")
    void parseVarsUnix(final Map<String, String> defaultVars, final Map<String, String> vars, final Map<String, String> envVars, final Map<String, String> expected) {
        final String profileConfiguration = "profileConfiguration";

        System.setProperty("os.name", "nix");

        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readClientNode(VARS_NODE, CONFIGURATION, Map.class)).thenReturn(vars);
        when(yamlUtils.readClientNode(VARS_NODE, profileConfiguration, Map.class)).thenReturn(envVars);

        spectrumSessionListener.parseVars(profileConfiguration);
        assertEquals(expected, Vars.getInstance());
    }

    static Stream<Arguments> varsValuesProvider() {
        return Stream.of(
                arguments(Map.of("one", "one"), null, null, Map.of("one", "one")),
                arguments(Map.of("one", "one"), Map.of("two", "two"), null, Map.of("one", "one", "two", "two")),
                arguments(Map.of("one", "one"), null, Map.of("three", "three"), Map.of("one", "one", "three", "three")),
                arguments(Map.of("one", "one"), Map.of("two", "two"), Map.of("three", "three"), Map.of("one", "one", "two", "two", "three", "three"))
        );
    }

    @DisplayName("isUnix should check the OS")
    @ParameterizedTest(name = "with OS {0} we expect {1}")
    @MethodSource("isUnixValuesProvider")
    void isUnix(final String localOsName, final boolean expected) {
        System.setProperty("os.name", localOsName);

        assertEquals(expected, spectrumSessionListener.isUnix());
    }

    static Stream<Arguments> isUnixValuesProvider() {
        return Stream.of(
                arguments("nix", true),
                arguments("blah", true),
                arguments("Win", false),
                arguments("WiN", false)
        );
    }
}
