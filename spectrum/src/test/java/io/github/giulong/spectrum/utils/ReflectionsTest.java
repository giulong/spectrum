package io.github.giulong.spectrum.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReflectionsTest {

    @DisplayName("getGenericSuperclassOf should return the generic superclass of the provided class, looking it up to the provided limit")
    @ParameterizedTest(name = "with class {0}")
    @ValueSource(classes = {TestClass.class, TestParentClass.class})
    public void getGenericSuperclassOf(final Class<?> clazz) {
        final ParameterizedType type = Reflections.getGenericSuperclassOf(clazz, Parameterized.class);

        assertEquals(String.class, type.getActualTypeArguments()[0]);
    }

    @Test
    @DisplayName("getField should return the field with the provided name on the provided object")
    public void getField() throws NoSuchFieldException {
        final String fieldName = "fieldString";
        final Dummy dummy = new Dummy(fieldName);
        final Field fieldString = Dummy.class.getDeclaredField(fieldName);

        assertEquals(fieldString, Reflections.getField(fieldName, dummy));
    }

    @Test
    @DisplayName("getField should return the field with the provided name on the provided object even if it's in the parent class")
    public void getFieldParent() throws NoSuchFieldException {
        final String fieldName = "fieldString";
        final String parentField = "parentField";
        final Dummy dummy = new Dummy(fieldName, parentField);
        final Field fieldString = DummyParent.class.getDeclaredField(parentField);

        assertEquals(fieldString, Reflections.getField(parentField, dummy));
    }

    @Test
    @DisplayName("getField should return the field with the provided name on the provided object even if it's in the parent class")
    public void getFieldNotFound() {
        final String fieldName = "notFound";
        final Dummy dummy = new Dummy(fieldName);

        assertThrows(NoSuchFieldException.class, () -> Reflections.getField(fieldName, dummy));
    }

    @Test
    @DisplayName("getFieldValue should return the value of the field with the provided name on the provided object")
    public void getFieldValue() {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(value);

        assertEquals(value, Reflections.getFieldValue(fieldName, dummy));
    }

    @Test
    @DisplayName("getFieldValue should return the value of the field with the provided name on the provided object, casted to the provided class")
    public void getFieldValueCast() {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(value);

        assertEquals(value, Reflections.getFieldValue(fieldName, dummy, String.class));
    }

    @Test
    @DisplayName("setField should set the field with the provided name on the provided object with the provided value")
    public void setFieldString() throws NoSuchFieldException, IllegalAccessException {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(null);
        final Field fieldString = Dummy.class.getDeclaredField(fieldName);

        Reflections.setField(fieldName, dummy, value);
        assertEquals(value, fieldString.get(dummy));
    }

    @Test
    @DisplayName("setField should set the provided field on the provided object with the provided value")
    public void setField() throws NoSuchFieldException, IllegalAccessException {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(null);
        final Field fieldString = Dummy.class.getDeclaredField(fieldName);

        Reflections.setField(fieldString, dummy, value);
        assertEquals(value, fieldString.get(dummy));
    }

    @Test
    @DisplayName("copyField should copy the provided field")
    public void copyField() throws NoSuchFieldException, IllegalAccessException {
        final String fieldName = "fieldString";
        final String value = "value";
        final Dummy dummy = new Dummy(value);
        final DummySecond dummySecond = new DummySecond();
        final Field fieldString = Dummy.class.getDeclaredField(fieldName);
        final Field fieldStringSecond = DummySecond.class.getDeclaredField(fieldName);

        Reflections.copyField(fieldString, dummy, fieldStringSecond, dummySecond);
        assertEquals(value, fieldStringSecond.get(dummySecond));
    }

    @Test
    @DisplayName("copyField should copy the provided field if it's the same on source and dest because it's inherited")
    public void copyFieldSame() {
        final String parentField = "parentField";
        final Dummy dummy = new Dummy(null, parentField);
        final DummyThird dummyThird = new DummyThird();
        final Field field = Reflections.getField(parentField, dummy);

        Reflections.copyField(field, dummy, dummyThird);
        assertEquals(parentField, Reflections.getFieldValue(parentField, dummyThird));
    }

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    @AllArgsConstructor
    private static class Dummy extends DummyParent {

        private final String fieldString;

        public Dummy(String fieldString, String parentField) {
            super(parentField);
            this.fieldString = fieldString;
        }
    }

    @SuppressWarnings("unused")
    private static class DummySecond {
        private String fieldString;
    }

    @SuppressWarnings("unused")
    private static class DummyThird extends DummyParent {
    }

    @SuppressWarnings("unused")
    @AllArgsConstructor
    @NoArgsConstructor
    private static class DummyParent {
        private String parentField;
    }

    @SuppressWarnings("unused")
    private static class Parameterized<T> {
    }

    private static class Parent extends Parameterized<String> {
    }

    private static class TestClass extends Parameterized<String> {
    }

    private static class TestParentClass extends Parent {
    }
}
