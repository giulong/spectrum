package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.interfaces.Secured;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ReflectionsTest {

    @DisplayName("getGenericSuperclassOf should return the generic superclass of the provided class, looking it up to the provided limit")
    @ParameterizedTest(name = "with class {0}")
    @ValueSource(classes = {TestClass.class, TestParentClass.class})
    void getGenericSuperclassOf(final Class<?> clazz) {
        final ParameterizedType type = Reflections.getGenericSuperclassOf(clazz, Parameterized.class);

        assertEquals(String.class, type.getActualTypeArguments()[0]);
    }

    @DisplayName("getFieldsOf should return the list of fields collecting also those from superclasses, up to the provided limit excluded")
    @ParameterizedTest(name = "with class {0} and limit {1} we expect fields {2}")
    @MethodSource("valuesProvider")
    void getFieldsOf(final Class<?> clazz, final Class<?> limit, final List<String> names) {
        final List<Field> actual = Reflections.getFieldsOf(clazz, limit);

        assertEquals(names, actual.stream().map(Field::getName).toList());
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(Dummy.class, DummyParent.class, List.of("fieldString", "secured")),
                arguments(Dummy.class, Object.class, List.of("fieldString", "secured", "parentField"))
        );
    }

    @Test
    @DisplayName("getField should return the field with the provided name on the provided object")
    void getField() throws NoSuchFieldException {
        final String fieldName = "fieldString";
        final Dummy dummy = new Dummy(fieldName);
        final Field fieldString = Dummy.class.getDeclaredField(fieldName);

        assertEquals(fieldString, Reflections.getField(fieldName, dummy));
    }

    @Test
    @DisplayName("getField should return the field with the provided name on the provided object even if it's in the parent class")
    void getFieldParent() throws NoSuchFieldException {
        final String fieldName = "fieldString";
        final String parentField = "parentField";
        final Dummy dummy = new Dummy(fieldName, parentField);
        final Field fieldString = DummyParent.class.getDeclaredField(parentField);

        assertEquals(fieldString, Reflections.getField(parentField, dummy));
    }

    @Test
    @DisplayName("getField should return the field with the provided name on the provided object even if it's in the parent class")
    void getFieldNotFound() {
        final String fieldName = "notFound";
        final Dummy dummy = new Dummy(fieldName);

        assertThrows(NoSuchFieldException.class, () -> Reflections.getField(fieldName, dummy));
    }

    @Test
    @DisplayName("getFieldValue should return the value of the field with the provided name on the provided object")
    void getFieldValue() {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(value);

        assertEquals(value, Reflections.getFieldValue(fieldName, dummy));
    }

    @Test
    @DisplayName("getFieldValue should return the value of the field with the provided name on the provided object, casted to the provided class")
    void getFieldValueCast() {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(value);

        assertEquals(value, Reflections.getFieldValue(fieldName, dummy, String.class));
    }

    @Test
    @DisplayName("getValueOf should return the value of the provided field on the provided object, without looking into its superclasses")
    void getValueOf() {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(value);

        assertEquals(value, Reflections.getValueOf(Reflections.getField(fieldName, dummy), dummy));
    }

    @Test
    @DisplayName("setField should set the field with the provided name on the provided object with the provided value")
    void setFieldString() throws NoSuchFieldException, IllegalAccessException {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(null);
        final Field fieldString = Dummy.class.getDeclaredField(fieldName);

        Reflections.setField(fieldName, dummy, value);
        assertEquals(value, fieldString.get(dummy));
    }

    @Test
    @DisplayName("setField should set the provided field on the provided object with the provided value")
    void setField() throws NoSuchFieldException, IllegalAccessException {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(null);
        final Field fieldString = Dummy.class.getDeclaredField(fieldName);

        Reflections.setField(fieldString, dummy, value);
        assertEquals(value, fieldString.get(dummy));
    }

    @Test
    @DisplayName("copyField should copy the provided field if it's the same on source and dest because it's inherited")
    void copyFieldSame() {
        final String parentField = "parentField";
        final Dummy dummy = new Dummy(null, parentField);
        final DummyThird dummyThird = new DummyThird();
        final Field field = Reflections.getField(parentField, dummy);

        Reflections.copyField(field, dummy, dummyThird);
        assertEquals(parentField, Reflections.getFieldValue(parentField, dummyThird));
    }

    @Test
    @DisplayName("getAnnotatedFields should return the list of fields on the provided class which are annotated with the provided annotation")
    void getAnnotatedFieldsClass() throws IllegalAccessException {
        final String value = "value";
        final Dummy dummy = new Dummy(null, value, null);

        final List<Field> actual = Reflections.getAnnotatedFields(Dummy.class, Secured.class);

        assertEquals(1, actual.size());
        assertEquals(value, actual.getFirst().get(dummy));
    }

    @Test
    @DisplayName("getAnnotatedFields should return the list of fields on the provided object which are annotated with the provided annotation")
    void getAnnotatedFields() throws IllegalAccessException {
        final String value = "value";
        final Dummy dummy = new Dummy(null, value, null);

        final List<Field> actual = Reflections.getAnnotatedFields(dummy, Secured.class);

        assertEquals(1, actual.size());
        assertEquals(value, actual.getFirst().get(dummy));
    }

    @Test
    @DisplayName("getAnnotatedFieldsValues should return the list of fields' values on the provided object " +
            "which are annotated with the provided annotation, casting them to the provided class")
    void getAnnotatedFieldsValues() {
        final String value = "value";
        final Dummy dummy = new Dummy(null, value, null);

        assertEquals(List.of(value), Reflections.getAnnotatedFieldsValues(dummy, Secured.class, String.class));
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static class Dummy extends DummyParent {

        private final String fieldString;

        @Secured
        private String secured;

        Dummy(String fieldString) {
            this.fieldString = fieldString;
        }

        Dummy(String fieldString, String parentField) {
            super(parentField);
            this.fieldString = fieldString;
        }

        Dummy(String fieldString, String secured, String parentField) {
            super(parentField);
            this.fieldString = fieldString;
            this.secured = secured;
        }
    }

    @SuppressWarnings("unused")
    private static final class DummySecond {
        private String fieldString;
    }

    @SuppressWarnings("unused")
    private static final class DummyThird extends DummyParent {
    }

    @SuppressWarnings("unused")
    @NoArgsConstructor
    private static class DummyParent {

        @SuppressWarnings("FieldCanBeLocal")
        private String parentField;

        DummyParent(String parentField) {
            this.parentField = parentField;
        }
    }

    @SuppressWarnings("unused")
    private static class Parameterized<T> {
    }

    private static class Parent extends Parameterized<String> {
    }

    private static final class TestClass extends Parameterized<String> {
    }

    private static final class TestParentClass extends Parent {
    }
}
