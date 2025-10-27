package io.github.giulong.spectrum.utils.file_providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.InjectableValues;

import io.github.giulong.spectrum.internals.jackson.views.Views;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;

class InternalFileProviderTest {

    @InjectMocks
    private InternalFileProvider fileProvider;

    @Test
    @DisplayName("getViews should return Views.Internal.class")
    void getViews() {
        assertEquals(Views.Internal.class, fileProvider.getViews());
    }

    @Test
    @DisplayName("getInjectableValues should return the internal injectables")
    void getInjectableValues() {
        final MockedConstruction<InjectableValues.Std> mockedConstruction = mockConstruction(InjectableValues.Std.class, (mock, context) ->
                when(mock.addValue("enabledFromClient", false)).thenReturn(mock));

        final InjectableValues actual = fileProvider.getInjectableValues();

        assertEquals(mockedConstruction.constructed().getFirst(), actual);

        mockedConstruction.close();
    }

    @Test
    @DisplayName("findFile should immediately return the file if it's internal: we know those exists!")
    void find() {
        final String file = "file";

        assertEquals(file, fileProvider.find(file));
    }
}
