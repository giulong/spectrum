package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.YamlUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Type;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class DataResolver<Data> implements ParameterResolver {

    public static final String DATA = "data";
    private final YamlUtils yamlUtils = YamlUtils.getInstance();

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) {
        return parameterContext.getParameter().getParameterizedType().getTypeName().equals(DataResolver.class.getTypeParameters()[0].getName());
    }

    @Override
    public Data resolveParameter(final ParameterContext arg0, final ExtensionContext context) {
        final Type type = Reflections.getGenericSuperclassOf(context.getRequiredTestClass(), SpectrumTest.class).getActualTypeArguments()[0];

        if (Void.class.equals(type)) {
            log.debug("Running an instance of SpectrumTest<Void>. No Data class injected in test '{}'", context.getDisplayName());
            return null;
        }

        @SuppressWarnings("unchecked") final Class<Data> dataClass = (Class<Data>) type;
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.computeIfAbsent(DATA, e -> {
            log.debug("Resolving {}", DATA);

            final Configuration.Data dataConfiguration = rootStore.get(CONFIGURATION, Configuration.class).getData();
            final Data data = yamlUtils.readClient(String.format("%s/data.yaml", dataConfiguration.getFolder()), dataClass);
            log.trace("Data:\n{}", yamlUtils.write(data));

            return data;
        }, dataClass);
    }
}
