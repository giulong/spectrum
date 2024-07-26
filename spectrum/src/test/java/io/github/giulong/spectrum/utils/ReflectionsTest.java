package io.github.giulong.spectrum.utils;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectionsTest {

    @Test
    @DisplayName("getField should return the field with the provided name on the provided object")
    public void getField() throws NoSuchFieldException {
        final String fieldName = "fieldString";
        final Dummy dummy = new Dummy(fieldName);
        final Field fieldString = Dummy.class.getDeclaredField(fieldName);

        assertEquals(fieldString, Reflections.getField(fieldName, dummy));
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
}
