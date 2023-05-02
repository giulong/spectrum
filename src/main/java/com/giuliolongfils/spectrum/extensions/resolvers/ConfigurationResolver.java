package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.config.YamlParser;
import com.giuliolongfils.spectrum.config.YamlWriter;
import com.giuliolongfils.spectrum.pojos.Configuration;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ConfigurationResolver extends TypeBasedParameterResolver<Configuration> {

    public static final String DEFAULT_CONFIGURATION = "yaml/configuration.default.yaml";
    public static final String CONFIGURATION = "configuration.yaml";
    public static final String ENV_NODE = "/systemProperties/env";
    public static final String VARS_NODE = "/vars";
    public static final Map<String, String> VARS = new HashMap<>();

    @Getter
    private final Configuration configuration;
    private final YamlParser yamlParser = YamlParser.getInstance();

    @SneakyThrows
    public ConfigurationResolver() {
        log.debug("Parsing Configuration");

        final String envConfiguration = String.format("configuration-%s.yaml", parseEnv());
        parseVars(envConfiguration);

        configuration = yamlParser.readInternal(DEFAULT_CONFIGURATION, Configuration.class);
        yamlParser.updateWithFile(configuration, CONFIGURATION);
        yamlParser.updateWithFile(configuration, envConfiguration);

        log.trace("Configuration:\n{}", YamlWriter.getInstance().write(configuration));
    }

    @Override
    public Configuration resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        return configuration;
    }

    protected String parseEnv() {
        return Optional
                .ofNullable(yamlParser.readInternalNode(ENV_NODE, CONFIGURATION, String.class))
                .orElse(yamlParser.readInternalNode(ENV_NODE, DEFAULT_CONFIGURATION, String.class));
    }

    @SuppressWarnings("unchecked")
    protected void parseVars(final String envConfiguration) {
        VARS.putAll(yamlParser.readInternalNode(VARS_NODE, DEFAULT_CONFIGURATION, Map.class));
        VARS.putAll(Optional.ofNullable(yamlParser.readNode(VARS_NODE, CONFIGURATION, Map.class)).orElse(new HashMap<>()));
        VARS.putAll(Optional.ofNullable(yamlParser.readNode(VARS_NODE, envConfiguration, Map.class)).orElse(new HashMap<>()));
    }
}
