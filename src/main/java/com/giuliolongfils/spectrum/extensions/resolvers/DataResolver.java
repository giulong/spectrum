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

@Slf4j
public class DataResolver<Data> implements ParameterResolver {

    private final Data data;

    @SneakyThrows
    public DataResolver(final Configuration.Data dataConfiguration) {
        log.debug("Parsing Data");

        @SuppressWarnings("unchecked")
        final Class<Data> dataClass = (Class<Data>) Class.forName(dataConfiguration.getFqdn());

        data = YamlParser.getInstance().read("data/data.yaml", dataClass);
        log.trace("Data:\n{}", YamlWriter.getInstance().write(data));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getParameterizedType().getTypeName().equals(DataResolver.class.getTypeParameters()[0].getName());
    }

    @Override
    public Data resolveParameter(ParameterContext arg0, ExtensionContext context) throws ParameterResolutionException {
        return data;
    }
}
