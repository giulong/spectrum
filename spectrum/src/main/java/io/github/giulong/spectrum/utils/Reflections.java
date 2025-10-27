package io.github.giulong.spectrum.utils;

import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public final class Reflections {

    public static ParameterizedType getGenericSuperclassOf(Class<?> clazz, final Class<?> limit) {
        log.trace("Getting generic superclass of {} up to {}", clazz.getTypeName(), limit.getTypeName());

        while (clazz.getSuperclass() != limit) {
            clazz = clazz.getSuperclass();
            log.trace("Checking {}", clazz.getTypeName());
        }

        return (ParameterizedType) clazz.getGenericSuperclass();
    }

    public static List<Field> getFieldsOf(Class<?> clazz, final Class<?> limit) {
        log.trace("Getting fields of {}", clazz.getSimpleName());

        final List<Field> fields = new ArrayList<>(asList(clazz.getDeclaredFields()));

        while (clazz.getSuperclass() != limit) {
            clazz = clazz.getSuperclass();
            log.trace("Getting also fields of superclass {}", clazz.getSimpleName());
            fields.addAll(asList(clazz.getDeclaredFields()));
        }

        return fields;
    }

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

    @SafeVarargs
    public static <T> T getFieldValue(final String fieldName, final Object object, final T... reified) {
        if (reified == null || reified.length > 0) {
            throw new IllegalArgumentException("Do not pass arguments as last parameter");
        }

        final Object value = getValueOf(getField(fieldName, object), object);
        return getClassOf(reified).cast(value);
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
    public static void copyField(final Field field, final Object source, final Object dest) {
        log.trace("Copying field {} from {} to {}", field.getName(), source.getClass().getSimpleName(), dest.getClass().getSimpleName());
        field.setAccessible(true);
        field.set(dest, field.get(source));
    }

    public static List<Field> getAnnotatedFields(final Class<?> clazz, final Class<? extends Annotation> annotation) {
        final String className = clazz.getTypeName();
        final String annotationName = annotation.getTypeName();

        return Arrays
                .stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(annotation))
                .peek(f -> log.debug("Field {}.{} is annotated with {}", className, f.getName(), annotationName))
                .peek(f -> f.setAccessible(true))
                .toList();
    }

    public static List<Field> getAnnotatedFields(final Object object, final Class<? extends Annotation> annotation) {
        return getAnnotatedFields(object.getClass(), annotation);
    }

    public static <T> List<T> getAnnotatedFieldsValues(final Object object, final Class<? extends Annotation> annotation, final Class<T> clazz) {
        return getAnnotatedFields(object, annotation)
                .stream()
                .map(f -> getValueOf(f, object))
                .map(clazz::cast)
                .toList();
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassOf(final T[] array) {
        return (Class<T>) array.getClass().getComponentType();
    }

    @SneakyThrows
    static Object getValueOf(final Field field, final Object object) {
        return field.get(object);
    }
}
