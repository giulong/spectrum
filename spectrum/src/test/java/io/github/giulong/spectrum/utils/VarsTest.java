package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.internals.jackson.deserializers.InterpolatedBooleanDeserializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Vars")
class VarsTest {

    @InjectMocks
    private Vars vars;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(Vars.getInstance(), Vars.getInstance());
    }
}
