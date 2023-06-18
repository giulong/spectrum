package com.github.giulong.spectrum.extensions.resolvers;

import com.github.giulong.spectrum.pojos.Configuration;
import com.github.giulong.spectrum.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class DataResolver<Data> implements ParameterResolver {

    public static final String DATA = "data";

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getParameterizedType().getTypeName().equals(DataResolver.class.getTypeParameters()[0].getName());
    }

    @Override
    public Data resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final String fqdn = rootStore.get(ConfigurationResolver.CONFIGURATION, Configuration.class).getData().getFqdn();

        try {
            @SuppressWarnings("unchecked")
            final Class<Data> dataClass = (Class<Data>) Class.forName(fqdn);

            return rootStore.getOrComputeIfAbsent(DATA, e -> {
                log.debug("Resolving {}", DATA);

                final YamlUtils yamlUtils = YamlUtils.getInstance();
                final Data data = yamlUtils.read("data/data.yaml", dataClass);
                log.trace("Data:\n{}", yamlUtils.write(data));
                rootStore.put(DATA, data);
                return data;
            }, dataClass);
        } catch (ClassNotFoundException e) {
            log.warn("Invalid value for Data class in 'configuration.data.fqdn': {}. If Data class is needed, you can safely ignore this warning.", fqdn);
            return null;
        }
    }
}
