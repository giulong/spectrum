package io.github.giulong.spectrum.extensions.resolvers;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import io.github.giulong.spectrum.utils.Configuration;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class ConfigurationResolver extends TypeBasedParameterResolver<Configuration> {

    public static final String CONFIGURATION = "configuration";

    @Override
    public Configuration resolveParameter(@NonNull final ParameterContext parameterContext, final ExtensionContext context) {
        return context.getRoot().getStore(GLOBAL).computeIfAbsent(CONFIGURATION, e -> {
            log.debug("Resolving {}", CONFIGURATION);

            return Configuration.getInstance();
        }, Configuration.class);
    }
}
