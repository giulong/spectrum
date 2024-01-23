package io.github.giulong.spectrum;

import io.github.giulong.spectrum.pojos.SpectrumProperties;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.*;

@Slf4j
public class SpectrumSessionListener implements LauncherSessionListener {

    public static final int BANNER_LINE_LENGTH = 37;
    public static final String DEFAULT_CONFIGURATION_YAML = "yaml/configuration.default.yaml";
    public static final String DEFAULT_CONFIGURATION_UNIX_YAML = "yaml/configuration.default.unix.yaml";
    public static final String CONFIGURATION_YAML = "configuration.yaml";
    public static final String PROFILE_NODE = "/runtime/profiles";
    public static final String VARS_NODE = "/vars";

    private final Vars vars = Vars.getInstance();
    private final YamlUtils yamlUtils = YamlUtils.getInstance();
    private final FileUtils fileUtils = FileUtils.getInstance();
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();
    private final ExtentReporter extentReporter = ExtentReporter.getInstance();
    private final EventsDispatcher eventsDispatcher = EventsDispatcher.getInstance();
    private final Configuration configuration = Configuration.getInstance();
    private final MetadataManager metadataManager = MetadataManager.getInstance();

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        log.info(String.format(Objects.requireNonNull(fileUtils.read("/banner.txt")), buildVersionLine()));

        parseConfiguration();
        session.getLauncher().registerTestExecutionListeners(configuration.getSummary().getSummaryGeneratingListener());

        configuration.getTestBook().sessionOpened();
        configuration.getSummary().sessionOpened();
        metadataManager.sessionOpened();
        extentReporter.sessionOpened();
        freeMarkerWrapper.sessionOpened();
        eventsDispatcher.sessionOpened();
    }

    @Override
    public void launcherSessionClosed(final LauncherSession session) {
        configuration.getTestBook().sessionClosed();
        configuration.getSummary().sessionClosed();
        extentReporter.sessionClosed();
        eventsDispatcher.sessionClosed();
        metadataManager.sessionClosed();
    }

    protected String buildVersionLine() {
        final SpectrumProperties spectrumProperties = yamlUtils.readProperties("spectrum.properties", SpectrumProperties.class);
        final String version = String.format("Version: %s", spectrumProperties.getVersion());
        final int wrappingSpacesLeft = BANNER_LINE_LENGTH - version.length();
        return "*".repeat(wrappingSpacesLeft) + String.format("  %s  |", version);
    }

    protected void parseConfiguration() {
        final List<String> profileConfigurations = parseProfiles()
                .stream()
                .map(profile -> String.format("configuration-%s.yaml", profile))
                .toList();

        profileConfigurations.forEach(this::parseVars);
        yamlUtils.updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);

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
        vars.putAll(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class));

        if (isUnix()) {
            vars.putAll(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML, Map.class));
        }

        vars.putAll(Optional.ofNullable(yamlUtils.readNode(VARS_NODE, CONFIGURATION_YAML, Map.class)).orElse(new HashMap<>()));
        vars.putAll(Optional.ofNullable(yamlUtils.readNode(VARS_NODE, profileConfiguration, Map.class)).orElse(new HashMap<>()));
    }

    protected boolean isUnix() {
        return !System.getProperty("os.name").toLowerCase().contains("win");
    }
}
