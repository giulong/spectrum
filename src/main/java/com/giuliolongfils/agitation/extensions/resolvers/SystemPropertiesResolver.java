package com.giuliolongfils.agitation.extensions.resolvers;

import com.giuliolongfils.agitation.config.YamlParser;
import com.giuliolongfils.agitation.config.YamlWriter;
import com.giuliolongfils.agitation.pojos.SystemProperties;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class SystemPropertiesResolver extends TypeBasedParameterResolver<SystemProperties> {

    @Getter
    private final SystemProperties systemProperties;

    @SneakyThrows
    public SystemPropertiesResolver() {
        log.debug("Building SystemProperties");
        final YamlParser yamlParser = YamlParser.getInstance();
        final YamlWriter yamlWriter = YamlWriter.getInstance();
        systemProperties = yamlParser.readInternal("yaml/system-properties.default.yaml", SystemProperties.class);
        yamlParser.update(systemProperties, yamlWriter.write(System.getProperties()));

        log.trace("System properties:\n{}", yamlWriter.write(systemProperties));
    }

    @Override
    public SystemProperties resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        return systemProperties;
    }
}
