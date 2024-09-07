package io.github.giulong.spectrum.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Arrays;

@Slf4j
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public final class Reflections {

    @SneakyThrows
    public static Field getField(final String fieldName, final Object object) {
        log.trace("Getting field {}.{}", object.getClass().getSimpleName(), fieldName);

        Class<?> clazz = object.getClass();
        while (clazz != Object.class && Arrays.stream(clazz.getDeclaredFields()).map(Field::getName).noneMatch(n -> n.equals(fieldName))) {
            clazz = clazz.getSuperclass();
            log.trace("Field {} not found. Looking into superclass {}", fieldName, clazz.getSimpleName());
        }

        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);

        return field;
    }

    @SneakyThrows
    public static Object getFieldValue(final String fieldName, final Object object) {
        log.trace("Getting value of field {}.{}", object.getClass().getSimpleName(), fieldName);
        return getField(fieldName, object).get(object);
    }

    public static <T> T getFieldValue(final String fieldName, final Object object, final Class<T> clazz) {
        return clazz.cast(getFieldValue(fieldName, object));
    }

    public static void setField(final String fieldName, final Object object, final Object value) {
        final Field field = getField(fieldName, object);
        setField(field, object, value);
    }

    @SneakyThrows
    public static void setField(final Field field, final Object object, final Object value) {
        log.trace("Setting field {}.{} to {}", object.getClass().getSimpleName(), field.getName(), value);
        field.setAccessible(true);
        field.set(object, value);
    }

    @SneakyThrows
    public static void copyField(final Field sourceField, final Object source, final Field destField, final Object dest) {
        log.trace("Copying field {}.{} to {}.{}", source.getClass().getSimpleName(), sourceField.getName(), dest.getClass().getSimpleName(), destField.getName());
        sourceField.setAccessible(true);
        destField.setAccessible(true);
        destField.set(dest, sourceField.get(source));
    }

    @SneakyThrows
    public static void copyField(final Field field, final Object source, final Object dest) {
        log.trace("Copying field {} from {} to {}", field.getName(), source.getClass().getSimpleName(), dest.getClass().getSimpleName());
        field.setAccessible(true);
        field.set(dest, field.get(source));
    }
}
