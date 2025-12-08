package io.github.giulong.spectrum.extensions.resolvers;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import io.github.giulong.spectrum.utils.Configuration;

import net.datafaker.Faker;

import lombok.extern.slf4j.Slf4j;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

@Slf4j
public class FakerResolver extends TypeBasedParameterResolver<Faker> {

    public static final String FAKER = "faker";

    @Override
    public Faker resolveParameter(@NonNull final ParameterContext parameterContext, final ExtensionContext context) {
        final ExtensionContext.Store rootStore = context.getRoot().getStore(GLOBAL);

        return rootStore.computeIfAbsent(FAKER, e -> {
            log.debug("Resolving {}", FAKER);
            final Configuration.Faker faker = rootStore.get(CONFIGURATION, Configuration.class).getFaker();

            return new Faker(faker.getLocale(), faker.getRandom());
        }, Faker.class);
    }
}
