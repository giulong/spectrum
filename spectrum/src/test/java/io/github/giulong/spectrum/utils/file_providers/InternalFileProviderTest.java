package io.github.giulong.spectrum.utils.file_providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.giulong.spectrum.internals.jackson.views.Views;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

class InternalFileProviderTest {

    @InjectMocks
    private InternalFileProvider fileProvider;

    @Test
    @DisplayName("getViews should return Views.Internal.class")
    void getViews() {
        assertEquals(Views.Internal.class, fileProvider.getViews());
    }

    @Test
    @DisplayName("getInjectableValues should return null")
    void getInjectableValues() {
        assertNull(fileProvider.getInjectableValues());
    }

    @Test
    @DisplayName("findFile should immediately return the file if it's internal: we know those exists!")
    void find() {
        final String file = "file";

        assertEquals(file, fileProvider.find(file));
    }
}
