package io.github.giulong.spectrum.utils;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfigurationTest {

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(Configuration.getInstance(), Configuration.getInstance());
    }
}
