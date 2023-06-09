package com.github.giulong.spectrum.extensions.resolvers;

import com.github.giulong.spectrum.SpectrumSessionListener;
import com.github.giulong.spectrum.pojos.Configuration;
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
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.getOrComputeIfAbsent(CONFIGURATION, e -> {
            log.debug("Resolving {}", CONFIGURATION);

            final Configuration configuration = SpectrumSessionListener.getConfiguration();
            rootStore.put(CONFIGURATION, configuration);
            return configuration;
        }, Configuration.class);
    }
}
