package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ConfigurationResolver extends TypeBasedParameterResolver<Configuration> {

    public static final String CONFIGURATION = "configuration";

    @Override
    public Configuration resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        return context.getRoot().getStore(GLOBAL).getOrComputeIfAbsent(CONFIGURATION, e -> {
            log.debug("Resolving {}", CONFIGURATION);

            return Configuration.getInstance();
        }, Configuration.class);
    }
}
