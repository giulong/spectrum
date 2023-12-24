package io.github.giulong.spectrum;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.pojos.SpectrumProperties;
import io.github.giulong.spectrum.utils.ExtentReporter;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.YamlUtils;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.*;

import static io.github.giulong.spectrum.utils.events.EventsDispatcher.*;

@Slf4j
public class SpectrumSessionListener implements LauncherSessionListener {

    public static final int BANNER_LINE_LENGTH = 86;
    public static final String DEFAULT_CONFIGURATION_YAML = "yaml/configuration.default.yaml";
    public static final String DEFAULT_CONFIGURATION_UNIX_YAML = "yaml/configuration.default.unix.yaml";
    public static final String CONFIGURATION_YAML = "configuration.yaml";
    public static final String PROFILE_NODE = "/runtime/profiles";
    public static final String VARS_NODE = "/vars";
    public static final Map<String, String> VARS = new HashMap<>();

    private final YamlUtils yamlUtils = YamlUtils.getInstance();
    private final FileUtils fileUtils = FileUtils.getInstance();
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();
    private final ExtentReporter extentReporter = ExtentReporter.getInstance();

    @Getter
    protected static Configuration configuration;

    @Getter
    protected static EventsDispatcher eventsDispatcher;

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        log.info(String.format(Objects.requireNonNull(fileUtils.read("/banner.txt")), buildVersionLine()));

        parseConfiguration();
        configuration.getTestBook().parse();

        final Configuration.Extent extent = configuration.getExtent();

        extentReporter
                .setupFrom(extent)
                .cleanupOldReports(extent);

        initEventsDispatcher();

        freeMarkerWrapper.setupFrom(configuration.getFreeMarker());
        eventsDispatcher.fire(BEFORE, Set.of(SUITE));
    }

    @Override
    public void launcherSessionClosed(final LauncherSession session) {
        configuration.getTestBook().flush();
        extentReporter.flush();
        eventsDispatcher.fire(AFTER, Set.of(SUITE));
    }

    protected String buildVersionLine() {
        final SpectrumProperties spectrumProperties = yamlUtils.readProperties("spectrum.properties", SpectrumProperties.class);
        final String version = String.format("Version: %s", spectrumProperties.getVersion());
        final int wrappingSpacesLeft = (BANNER_LINE_LENGTH - version.length()) / 2;
        final int wrappingSpacesRight = version.length() % 2 == 0 ? wrappingSpacesLeft : wrappingSpacesLeft + 1;
        return String.format("#%" + wrappingSpacesLeft + "s%s%" + wrappingSpacesRight + "s#", " ", version, " ");
    }

    protected void parseConfiguration() {
        final List<String> profileConfigurations = parseProfiles()
                .stream()
                .map(profile -> String.format("configuration-%s.yaml", profile))
                .toList();

        profileConfigurations.forEach(this::parseVars);
        configuration = yamlUtils.readInternal(DEFAULT_CONFIGURATION_YAML, Configuration.class);

        if (isUnix()) {
            yamlUtils.updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_UNIX_YAML);
        }

        yamlUtils.updateWithFile(configuration, CONFIGURATION_YAML);
        profileConfigurations.forEach(pc -> yamlUtils.updateWithFile(configuration, pc));

        log.trace("Configuration:\n{}", yamlUtils.write(configuration));
    }

    protected List<String> parseProfiles() {
        return Arrays.stream(Optional
                        .ofNullable(yamlUtils.readInternalNode(PROFILE_NODE, CONFIGURATION_YAML, String.class))
                        .orElse(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML, String.class))
                        .split(","))
                .toList();
    }

    @SuppressWarnings("unchecked")
    protected void parseVars(final String profileConfiguration) {
        VARS.putAll(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class));

        if (isUnix()) {
            VARS.putAll(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML, Map.class));
        }

        VARS.putAll(Optional.ofNullable(yamlUtils.readNode(VARS_NODE, CONFIGURATION_YAML, Map.class)).orElse(new HashMap<>()));
        VARS.putAll(Optional.ofNullable(yamlUtils.readNode(VARS_NODE, profileConfiguration, Map.class)).orElse(new HashMap<>()));
    }

    protected void initEventsDispatcher() {
        eventsDispatcher = EventsDispatcher
                .builder()
                .consumers(configuration.getEventsConsumers())
                .build();
    }

    protected boolean isUnix() {
        return !System.getProperty("os.name").toLowerCase().contains("win");
    }
}
