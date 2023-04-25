package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.config.YamlParser;
import com.giuliolongfils.agitation.config.YamlWriter;
import com.giuliolongfils.agitation.pojos.Configuration;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class ConfigurationResolver extends TypeBasedParameterResolver<Configuration> {

	@Getter
	private final Configuration configuration;

	@SneakyThrows
	public ConfigurationResolver(final SystemProperties systemProperties) {
		log.debug("Parsing Configuration");
		final YamlParser yamlParser = YamlParser.getInstance();
		final String envConfiguration = String.format("configuration-%s.yaml", systemProperties.getEnv());
		configuration = yamlParser.readInternal("yaml/configuration.default.yaml", Configuration.class);
		yamlParser.updateWithFile(configuration, "configuration.yaml");
		yamlParser.updateWithFile(configuration, envConfiguration);

		log.trace("Configuration:\n{}", YamlWriter.getInstance().write(configuration));
	}

	@Override
	public Configuration resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
		return configuration;
	}
}
