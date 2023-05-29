package com.github.giulong.spectrum.extensions.resolvers;

import com.github.giulong.spectrum.utils.YamlUtils;
import com.github.giulong.spectrum.pojos.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ConfigurationResolver extends TypeBasedParameterResolver<Configuration> {

    public static final String CONFIGURATION = "configuration";
    public static final String DEFAULT_CONFIGURATION_YAML = "yaml/configuration.default.yaml";
    public static final String CONFIGURATION_YAML = "configuration.yaml";
    public static final String ENV_NODE = "/runtime/env";
    public static final String VARS_NODE = "/vars";
    public static final Map<String, String> VARS = new HashMap<>();

    private final YamlUtils yamlUtils = YamlUtils.getInstance();

    @Override
    public Configuration resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.getOrComputeIfAbsent(CONFIGURATION, e -> {
            log.debug("Resolving {}", CONFIGURATION);

            final String envConfiguration = String.format("configuration-%s.yaml", parseEnv());
            parseVars(envConfiguration);

            final Configuration configuration = yamlUtils.readInternal(DEFAULT_CONFIGURATION_YAML, Configuration.class);
            yamlUtils.updateWithFile(configuration, CONFIGURATION_YAML);
            yamlUtils.updateWithFile(configuration, envConfiguration);

            log.trace("Configuration:\n{}", YamlUtils.getInstance().write(configuration));

            rootStore.put(CONFIGURATION, configuration);
            return configuration;
        }, Configuration.class);
    }

    protected String parseEnv() {
        return Optional
                .ofNullable(yamlUtils.readInternalNode(ENV_NODE, CONFIGURATION_YAML, String.class))
                .orElse(yamlUtils.readInternalNode(ENV_NODE, DEFAULT_CONFIGURATION_YAML, String.class));
    }

    @SuppressWarnings("unchecked")
    protected void parseVars(final String envConfiguration) {
        VARS.putAll(yamlUtils.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION_YAML, Map.class));
        VARS.putAll(Optional.ofNullable(yamlUtils.readNode(VARS_NODE, CONFIGURATION_YAML, Map.class)).orElse(new HashMap<>()));
        VARS.putAll(Optional.ofNullable(yamlUtils.readNode(VARS_NODE, envConfiguration, Map.class)).orElse(new HashMap<>()));
    }
}
