package io.github.giulong.spectrum.extensions.resolvers;

import static io.github.giulong.spectrum.extensions.resolvers.FakerResolver.FAKER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Locale;
import java.util.Random;
import java.util.function.Function;

import io.github.giulong.spectrum.utils.Configuration;

import net.datafaker.Faker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;

class FakerResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Faker faker;

    @Mock
    private Locale locale;

    @Mock
    private Random random;

    @Captor
    private ArgumentCaptor<Function<String, Faker>> functionArgumentCaptor;

    @InjectMocks
    private FakerResolver fakerResolver;

    @Test
    @DisplayName("resolveParameter should return an instance of PageLoadWaits on the current stored WebDriver")
    void testResolveParameter() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(ConfigurationResolver.CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getFaker()).thenReturn(faker);
        when(faker.getLocale()).thenReturn(locale);
        when(faker.getRandom()).thenReturn(random);

        final MockedConstruction<Faker> mockedConstruction = mockConstruction((mock, context) -> {
            assertEquals(locale, context.arguments().getFirst());
            assertEquals(random, context.arguments().get(1));
        });

        fakerResolver.resolveParameter(parameterContext, extensionContext);

        verify(rootStore).computeIfAbsent(eq(FAKER), functionArgumentCaptor.capture(), eq(Faker.class));
        final Function<String, Faker> function = functionArgumentCaptor.getValue();
        final Faker actual = function.apply("value");

        Faker expected = mockedConstruction.constructed().getFirst();
        assertEquals(expected, actual);

        mockedConstruction.close();
    }
}
