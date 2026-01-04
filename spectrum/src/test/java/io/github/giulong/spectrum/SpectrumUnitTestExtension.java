package io.github.giulong.spectrum;

import static java.util.stream.Collectors.toMap;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Map;

import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;

@SuppressWarnings("unused")
public class SpectrumUnitTestExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final Object testInstance = context.getRequiredTestInstance();
        final Map<String, Object> mockSingletons = Reflections
                .getAnnotatedFields(testInstance, MockFinal.class)
                .stream()
                .collect(toMap(Field::getName, f -> mock(f.getType())));

        Reflections
                .getAnnotatedFields(testInstance, InjectMocks.class)
                .stream()
                .map(Field::getName)
                .map(n -> Reflections.getFieldValue(n, testInstance))
                .forEach(objectUnderTest -> mockSingletons.forEach((name, mock) -> {
                    Reflections.setField(name, objectUnderTest, mock);
                    Reflections.setField(name, testInstance, mock);
                }));
    }
}
