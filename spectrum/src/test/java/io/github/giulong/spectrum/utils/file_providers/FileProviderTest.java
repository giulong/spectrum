package io.github.giulong.spectrum.utils.file_providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import io.github.giulong.spectrum.internals.jackson.views.Views;

import lombok.AllArgsConstructor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class FileProviderTest {

    @Mock
    private ObjectMapper mapper;

    @Mock
    private ObjectReader reader;

    @Mock
    private InjectableValues.Std std;

    @InjectMocks
    private DummyFileProvider fileProvider;

    @Test
    @DisplayName("augment should return the reader augmented with views and injectable values from the provided mapper")
    void augment() {
        when(mapper.reader()).thenReturn(reader);
        when(reader.withView(Views.Client.class)).thenReturn(reader);
        when(reader.with(std)).thenReturn(reader);

        assertEquals(reader, fileProvider.augment(mapper));
    }

    @AllArgsConstructor
    private static final class DummyFileProvider implements FileProvider {

        private InjectableValues.Std std;

        @Override
        public Class<? extends Views> getViews() {
            return Views.Client.class;
        }

        @Override
        public InjectableValues getInjectableValues() {
            return std;
        }

        @Override
        public String find(String file) {
            return "";
        }
    }
}
