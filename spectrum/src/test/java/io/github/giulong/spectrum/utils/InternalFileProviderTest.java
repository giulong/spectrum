package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.internals.jackson.views.Views;
import io.github.giulong.spectrum.utils.file_providers.InternalFileProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InternalFileProviderTest {

    @InjectMocks
    private InternalFileProvider fileProvider;

    @Test
    @DisplayName("getViews should return Views.Internal.class")
    void getViews() {
        assertEquals(Views.Internal.class, fileProvider.getViews());
    }

    @Test
    @DisplayName("findFile should immediately return the file if it's internal: we know those exists!")
    void find() {
        final String file = "file";

        assertEquals(file, fileProvider.find(file));
    }
}
