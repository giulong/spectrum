package io.github.giulong.spectrum.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public final class Reflections {

    @SneakyThrows
    public static Field getField(final String fieldName, final Object object) {
        final Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return field;
    }

    @SneakyThrows
    public static Field getParentField(final String fieldName, final Object object) {
        final Field field = object.getClass().getSuperclass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return field;
    }

    @SneakyThrows
    public static Object getFieldValue(final String fieldName, final Object object) {
        return getField(fieldName, object).get(object);
    }

    @SneakyThrows
    public static Object getParentFieldValue(final String fieldName, final Object object) {
        return getParentField(fieldName, object).get(object);
    }

    @SneakyThrows
    public static void setField(final String fieldName, final Object object, final Object value) {
        final Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    @SneakyThrows
    public static void setParentField(final String fieldName, final Object object, final Class<?> superclass, final Object value) {
        final Field field = superclass.getDeclaredField(fieldName);
        field.setAccessible(true);
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
