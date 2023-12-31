package io.github.giulong.spectrum;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.pojos.SpectrumProperties;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import io.github.giulong.spectrum.utils.summary.Summary;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.github.giulong.spectrum.SpectrumSessionListener.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SpectrumSessionListener")
class SpectrumSessionListenerTest {

    private static MockedStatic<Configuration> configurationMockedStatic;
    private static MockedStatic<ExtentReporter> extentReportsWrapperMockedStatic;
    private static MockedStatic<SLF4JBridgeHandler> slf4JBridgeHandlerMockedStatic;
    private static MockedStatic<FileUtils> fileUtilsMockedStatic;
    private static MockedStatic<YamlUtils> yamlUtilsMockedStatic;
    private static MockedStatic<FreeMarkerWrapper> freeMarkerWrapperMockedStatic;
    private static MockedStatic<EventsDispatcher> eventsDispatcherMockedStatic;

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
    private SpectrumProperties spectrumProperties;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private Summary summary;

    @Mock
    private SummaryGeneratingListener summaryGeneratingListener;

    @Mock
    private Metadata metadata;

    @InjectMocks
    private SpectrumSessionListener spectrumSessionListener;

    @BeforeEach
    public void beforeEach() {
        osName = System.getProperty("os.name");
        ReflectionUtils.setField("yamlUtils", spectrumSessionListener, yamlUtils);
        ReflectionUtils.setField("fileUtils", spectrumSessionListener, fileUtils);
        ReflectionUtils.setField("freeMarkerWrapper", spectrumSessionListener, freeMarkerWrapper);
        ReflectionUtils.setField("extentReporter", spectrumSessionListener, extentReporter);
        ReflectionUtils.setField("configuration", spectrumSessionListener, configuration);
        ReflectionUtils.setField("eventsDispatcher", spectrumSessionListener, eventsDispatcher);
        ReflectionUtils.setField("metadata", spectrumSessionListener, metadata);

        configurationMockedStatic = mockStatic(Configuration.class);
        extentReportsWrapperMockedStatic = mockStatic(ExtentReporter.class);
        slf4JBridgeHandlerMockedStatic = mockStatic(SLF4JBridgeHandler.class);
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        yamlUtilsMockedStatic = mockStatic(YamlUtils.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        eventsDispatcherMockedStatic = mockStatic(EventsDispatcher.class);
    }

    @AfterEach
    public void afterEach() {
        configurationMockedStatic.close();
        extentReportsWrapperMockedStatic.close();
        slf4JBridgeHandlerMockedStatic.close();
        fileUtilsMockedStatic.close();
        yamlUtilsMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
        eventsDispatcherMockedStatic.close();
        VARS.clear();
        System.setProperty("os.name", osName);
    }

    @Test
    @DisplayName("launcherSessionOpened should log the banner and initialize Spectrum")
    public void launcherSessionOpened() {
        final String profile = "profile";
        final String profileConfiguration = String.format("configuration-%s.yaml", profile);
        final String banner = "banner";
        final String version = "version";

        System.setProperty("os.name", "Win");

        when(fileUtils.read("/banner.txt")).thenReturn(banner);
        when(spectrumProperties.getVersion()).thenReturn(version);

        when(YamlUtils.getInstance()).thenReturn(yamlUtils);
        when(yamlUtils.readProperties("spectrum.properties", SpectrumProperties.class)).thenReturn(spectrumProperties);
        when(yamlUtils.readInternalNode(PROFILE_NODE, CONFIGURATION_YAML, String.class)).thenReturn(profile);
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultProfile");
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        when(FreeMarkerWrapper.getInstance()).thenReturn(freeMarkerWrapper);
        when(EventsDispatcher.getInstance()).thenReturn(eventsDispatcher);

        when(launcherSession.getLauncher()).thenReturn(launcher);
        when(configuration.getSummary()).thenReturn(summary);
        when(summary.getSummaryGeneratingListener()).thenReturn(summaryGeneratingListener);

        spectrumSessionListener.launcherSessionOpened(launcherSession);

        slf4JBridgeHandlerMockedStatic.verify(SLF4JBridgeHandler::removeHandlersForRootLogger);
        slf4JBridgeHandlerMockedStatic.verify(SLF4JBridgeHandler::install);

        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);
        verify(yamlUtils).updateWithFile(configuration, CONFIGURATION_YAML);
        verify(yamlUtils).updateWithFile(configuration, profileConfiguration);

        verify(launcher).registerTestExecutionListeners(summaryGeneratingListener);
        verify(configuration).sessionOpened();
        verify(extentReporter).sessionOpenedFrom(configuration);
        verify(freeMarkerWrapper).sessionOpenedFrom(configuration);
        verify(eventsDispatcher).sessionOpenedFrom(configuration);
    }

    @Test
    @DisplayName("launcherSessionClosed should flush the testbook and the extent report")
    public void launcherSessionClosed() {
        spectrumSessionListener.launcherSessionClosed(launcherSession);

        verify(configuration).sessionClosed();
        verify(extentReporter).sessionClosedFrom(configuration);
        verify(eventsDispatcher).sessionClosed();
        verify(metadata).sessionClosedFrom(configuration);
    }

    @DisplayName("buildVersionLine should build the fixed-length line with the version to put in the logged banner")
    @ParameterizedTest(name = "with version {0} we expect {1}")
    @MethodSource("buildVersionLineValuesProvider")
    public void buildVersionLine(final String version, final String expected) {
        when(YamlUtils.getInstance()).thenReturn(yamlUtils);
        when(yamlUtils.readProperties("spectrum.properties", SpectrumProperties.class)).thenReturn(spectrumProperties);
        when(spectrumProperties.getVersion()).thenReturn(version);

        assertEquals(expected, spectrumSessionListener.buildVersionLine());
    }

    public static Stream<Arguments> buildVersionLineValuesProvider() {
        return Stream.of(
                arguments("version", "#                                   Version: version                                   #"),
                arguments("0.0.1", "#                                    Version: 0.0.1                                    #"),
                arguments("0.0.1-SNAPSHOT", "#                               Version: 0.0.1-SNAPSHOT                                #")
        );
    }

    @Test
    @DisplayName("parseConfiguration should parse all the configurations considering the active profile")
    public void parseConfiguration() {
        final String profile = "profile";
        final String profileConfiguration = String.format("configuration-%s.yaml", profile);

        System.setProperty("os.name", "Win");

        when(YamlUtils.getInstance()).thenReturn(yamlUtils);

        // parseProfile
        when(yamlUtils.readInternalNode(PROFILE_NODE, CONFIGURATION_YAML, String.class)).thenReturn(profile);
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultProfile");

        // parseVars
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        spectrumSessionListener.parseConfiguration();

        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);
        verify(yamlUtils).updateWithFile(configuration, CONFIGURATION_YAML);
        verify(yamlUtils).updateWithFile(configuration, profileConfiguration);
    }

    @Test
    @DisplayName("parseConfiguration should parse all the configurations considering also the internal configuration.default.unix.yaml")
    public void parseConfigurationUnix() {
        final String profile = "profile";
        final String profileConfiguration = String.format("configuration-%s.yaml", profile);

        System.setProperty("os.name", "nix");

        when(YamlUtils.getInstance()).thenReturn(yamlUtils);

        // parseProfile
        when(yamlUtils.readInternalNode(PROFILE_NODE, CONFIGURATION_YAML, String.class)).thenReturn(profile);
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn("defaultProfile");

        // parseVars
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(Map.of("one", "one"));
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML, Map.class)).thenReturn(Map.of("one", "one"));

        spectrumSessionListener.parseConfiguration();

        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);
        verify(yamlUtils).updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_UNIX_YAML);
        verify(yamlUtils).updateWithFile(configuration, CONFIGURATION_YAML);
        verify(yamlUtils).updateWithFile(configuration, profileConfiguration);
    }

    @DisplayName("parseProfiles should parse the profile node from both the internal configuration.yaml and the base configuration.yaml and return the merged value")
    @ParameterizedTest(name = "with profile {0} and default profile {1} we expect {2}")
    @MethodSource("profilesValuesProvider")
    public void parseProfiles(final String profile, final String defaultProfile, final List<String> expected) {
        when(YamlUtils.getInstance()).thenReturn(yamlUtils);

        when(yamlUtils.readInternalNode(PROFILE_NODE, CONFIGURATION_YAML, String.class)).thenReturn(profile);
        when(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class)).thenReturn(defaultProfile);

        assertEquals(expected, spectrumSessionListener.parseProfiles());
    }

    public static Stream<Arguments> profilesValuesProvider() {
        return Stream.of(
                arguments("overridden-profile", "default-profile,first", List.of("overridden-profile")),
                arguments("overridden-profile,second", "default-profile", List.of("overridden-profile", "second")),
                arguments(null, "default-profile", List.of("default-profile")),
                arguments(null, "default-profile,another", List.of("default-profile", "another"))
        );
    }

    @DisplayName("parseVars should put in the VARS map all the variables read from the configuration yaml files")
    @ParameterizedTest
    @MethodSource("varsValuesProvider")
    public void parseVars(final Map<String, String> defaultVars, final Map<String, String> vars, final Map<String, String> envVars, final Map<String, String> expected) {
        final String profileConfiguration = "profileConfiguration";

        System.setProperty("os.name", "Win");
        when(YamlUtils.getInstance()).thenReturn(yamlUtils);

        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readNode(VARS_NODE, CONFIGURATION_YAML, Map.class)).thenReturn(vars);
        when(yamlUtils.readNode(VARS_NODE, profileConfiguration, Map.class)).thenReturn(envVars);

        spectrumSessionListener.parseVars(profileConfiguration);
        assertEquals(expected, VARS);
    }

    @DisplayName("parseVars should put in the VARS map also those read from the internal configuration.default.unix.yaml")
    @ParameterizedTest
    @MethodSource("varsValuesProvider")
    public void parseVarsUnix(final Map<String, String> defaultVars, final Map<String, String> vars, final Map<String, String> envVars, final Map<String, String> expected) {
        final String profileConfiguration = "profileConfiguration";

        System.setProperty("os.name", "nix");
        when(YamlUtils.getInstance()).thenReturn(yamlUtils);

        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML, Map.class)).thenReturn(defaultVars);
        when(yamlUtils.readNode(VARS_NODE, CONFIGURATION_YAML, Map.class)).thenReturn(vars);
        when(yamlUtils.readNode(VARS_NODE, profileConfiguration, Map.class)).thenReturn(envVars);

        spectrumSessionListener.parseVars(profileConfiguration);
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

    @DisplayName("isUnix should check the OS")
    @ParameterizedTest(name = "with OS {0} we expect {1}")
    @MethodSource("isUnixValuesProvider")
    public void isUnix(final String osName, final boolean expected) {
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
