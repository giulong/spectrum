package io.github.giulong.spectrum;

import static java.util.function.Predicate.not;

import java.util.*;

import io.github.giulong.spectrum.types.ProjectProperties;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.Configuration.Config;
import io.github.giulong.spectrum.utils.events.EventsDispatcher;

import lombok.extern.slf4j.Slf4j;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.slf4j.bridge.SLF4JBridgeHandler;

@Slf4j
public class SpectrumSessionListener implements LauncherSessionListener {

    public static final String DEFAULT_CONFIGURATION_YAML = "yaml/configuration.default.yaml";
    public static final String DEFAULT_CONFIGURATION_UNIX_YAML = "yaml/configuration.default.unix.yaml";
    public static final String CONFIGURATION = "configuration";
    public static final String PROFILE_NODE = "/runtime/profiles";
    public static final String CONFIG_NODE = "/config";
    public static final String VARS_NODE = "/vars";

    private final Vars vars = Vars.getInstance();
    private final YamlUtils yamlUtils = YamlUtils.getInstance();
    private final FileUtils fileUtils = FileUtils.getInstance();
    private final HtmlUtils htmlUtils = HtmlUtils.getInstance();
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();
    private final ExtentReporter extentReporter = ExtentReporter.getInstance();
    private final EventsDispatcher eventsDispatcher = EventsDispatcher.getInstance();
    private final Configuration configuration = Configuration.getInstance();
    private final MetadataManager metadataManager = MetadataManager.getInstance();

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        final ProjectProperties projectProperties = yamlUtils.readInternal("properties.yaml", ProjectProperties.class);
        log.info(freeMarkerWrapper.interpolate(fileUtils.read("banner.txt"), projectProperties));

        parseConfiguration();
        session.getLauncher().registerTestExecutionListeners(configuration.getSummary().getSummaryGeneratingListener());

        configuration.getRuntime().getEnvironment().sessionOpened();
        configuration.getTestBook().sessionOpened();
        configuration.getSummary().sessionOpened();
        metadataManager.sessionOpened();
        extentReporter.sessionOpened();
        freeMarkerWrapper.sessionOpened();
        eventsDispatcher.sessionOpened();
        htmlUtils.sessionOpened();
    }

    @Override
    public void launcherSessionClosed(final LauncherSession session) {
        configuration.getRuntime().getEnvironment().sessionClosed();
        configuration.getTestBook().sessionClosed();
        configuration.getSummary().sessionClosed();
        metadataManager.sessionClosed();
        extentReporter.sessionClosed();
        eventsDispatcher.sessionClosed();
    }

    void parseConfiguration() {
        final List<String> profileConfigurations = parseProfiles()
                .stream()
                .map(profile -> String.format("configuration-%s", profile))
                .toList();

        parseConfig(profileConfigurations);
        parseVars(profileConfigurations);

        yamlUtils.updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_YAML);

        if (isUnix()) {
            yamlUtils.updateWithInternalFile(configuration, DEFAULT_CONFIGURATION_UNIX_YAML);
        }

        yamlUtils.updateWithClientFile(configuration, CONFIGURATION);
        profileConfigurations.forEach(pc -> yamlUtils.updateWithClientFile(configuration, pc));

        log.trace("Configuration:\n{}", yamlUtils.write(configuration));
    }

    List<String> parseProfiles() {
        return Arrays.stream(Optional
                .<String>ofNullable(yamlUtils.readClientNode(PROFILE_NODE, CONFIGURATION))
                .orElse(yamlUtils.readInternalNode(PROFILE_NODE, DEFAULT_CONFIGURATION_YAML))
                .split(","))
                .filter(not(String::isBlank))
                .toList();
    }

    void parseConfig(final List<String> profileConfigurations) {
        final Config config = yamlUtils.readInternalNode(CONFIG_NODE, DEFAULT_CONFIGURATION_YAML);

        if (isUnix()) {
            yamlUtils.updateWithInternalNode(config, CONFIG_NODE, DEFAULT_CONFIGURATION_UNIX_YAML);
        }

        yamlUtils.updateWithClientNode(config, CONFIG_NODE, CONFIGURATION);
        profileConfigurations.forEach(p -> yamlUtils.updateWithClientNode(config, CONFIG_NODE, p));

        configuration.setConfig(config);
    }

    void parseVars(final List<String> profileConfigurations) {
        vars.putAll(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML));

        if (isUnix()) {
            vars.putAll(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_UNIX_YAML));
        }

        vars.putAll(Optional
                .<Map<String, String>>ofNullable(yamlUtils.readClientNode(VARS_NODE, CONFIGURATION))
                .orElse(new HashMap<>()));

        profileConfigurations.forEach(p -> vars.putAll(Optional
                .<Map<String, String>>ofNullable(yamlUtils.readClientNode(VARS_NODE, p))
                .orElse(new HashMap<>())));
    }

    boolean isUnix() {
        return !System.getProperty("os.name").toLowerCase().contains("win");
    }
}
