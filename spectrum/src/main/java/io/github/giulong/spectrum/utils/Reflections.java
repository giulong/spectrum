package io.github.giulong.spectrum.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.Arrays;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public final class Reflections {

    @SneakyThrows
    public static Field getField(final String fieldName, final Object object) {
        Class<?> clazz = object.getClass();
        while (clazz != Object.class && Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).noneMatch(n -> n.equals(fieldName))) {
            clazz = clazz.getSuperclass();
        }

        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        return field;
    }

    @SneakyThrows
    public static Object getFieldValue(final String fieldName, final Object object) {
        return getField(fieldName, object).get(object);
    }

    @SneakyThrows
    public static void setField(final String fieldName, final Object object, final Object value) {
        final Field field = getField(fieldName, object);
        field.set(object, value);
    }

    @SneakyThrows
    public static void setField(final Field field, final Object object, final Object value) {
        field.setAccessible(true);
        field.set(object, value);
    }

    @SneakyThrows
    public static void copyField(final Field sourceField, final Object source, final Field destField, final Object dest) {
        sourceField.setAccessible(true);
        destField.setAccessible(true);
        destField.set(dest, sourceField.get(source));
    }
}
