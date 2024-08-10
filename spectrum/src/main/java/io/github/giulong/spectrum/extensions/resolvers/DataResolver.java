package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
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
        final ParameterizedType dataType = (ParameterizedType) context.getRequiredTestClass().getGenericSuperclass();
        final Type type = dataType.getActualTypeArguments()[0];

        if (Void.class.equals(type)) {
            log.warn("Running an instance of SpectrumTest<Void>. If no Data class is needed, you can safely ignore this warning.");
            return null;
        }

        @SuppressWarnings("unchecked") final Class<Data> dataClass = (Class<Data>) type;
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.getOrComputeIfAbsent(DATA, e -> {
            log.debug("Resolving {}", DATA);

            final YamlUtils yamlUtils = YamlUtils.getInstance();
            final Configuration.Data dataConfiguration = rootStore.get(CONFIGURATION, Configuration.class).getData();
            final Data data = yamlUtils.read(String.format("%s/data.yaml", dataConfiguration.getFolder()), dataClass);
            log.trace("Data:\n{}", yamlUtils.write(data));

            return data;
        }, dataClass);
    }
}
