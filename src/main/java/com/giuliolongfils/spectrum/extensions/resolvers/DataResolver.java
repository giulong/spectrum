package com.giuliolongfils.spectrum.extensions.resolvers;

import com.giuliolongfils.spectrum.config.YamlParser;
import com.giuliolongfils.spectrum.config.YamlWriter;
import com.giuliolongfils.spectrum.pojos.Configuration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import static com.giuliolongfils.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class DataResolver<Data> implements ParameterResolver {

    public static final String DATA = "data";

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getParameterizedType().getTypeName().equals(DataResolver.class.getTypeParameters()[0].getName());
    }

    @Override
    @SneakyThrows
    public Data resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);
        final Configuration.Data dataConfiguration = rootStore.get(CONFIGURATION, Configuration.class).getData();

        @SuppressWarnings("unchecked")
        final Class<Data> dataClass = (Class<Data>) Class.forName(dataConfiguration.getFqdn());

        return rootStore.getOrComputeIfAbsent(DATA, e -> {
            log.debug("Resolving Data");

            final Data data = YamlParser.getInstance().read("data/data.yaml", dataClass);
            log.trace("Data:\n{}", YamlWriter.getInstance().write(data));
            rootStore.put(DATA, data);
            return data;
        }, dataClass);
    }
}
