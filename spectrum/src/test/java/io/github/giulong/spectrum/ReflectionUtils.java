package io.github.giulong.spectrum;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

public class ReflectionUtils {

    @SneakyThrows
    public static void setField(final String fieldName, final Object object, final Object value) {
        final Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
}
