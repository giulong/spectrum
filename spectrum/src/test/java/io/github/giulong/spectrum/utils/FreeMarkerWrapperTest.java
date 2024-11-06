package io.github.giulong.spectrum.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class FreeMarkerWrapperTest {

    @Mock
    private freemarker.template.Configuration configuration;

    @Mock
    private Configuration spectrumConfiguration;

    @Mock
    private Configuration.FreeMarker freeMarker;

    @InjectMocks
    private FreeMarkerWrapper freeMarkerWrapper;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("spectrumConfiguration", freeMarkerWrapper, spectrumConfiguration);
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(FreeMarkerWrapper.getInstance(), FreeMarkerWrapper.getInstance());
    }

    @Test
    @DisplayName("sessionOpened should setup the configuration from the freemarker node in the configuration.yaml")
    void sessionOpened() {
        final String version = "version";
        final String numberFormat = "numberFormat";

        when(spectrumConfiguration.getFreeMarker()).thenReturn(freeMarker);
        when(freeMarker.getVersion()).thenReturn(version);
        when(freeMarker.getLocale()).thenReturn(US);
        when(freeMarker.getNumberFormat()).thenReturn(numberFormat);

        MockedConstruction<Version> versionMockedConstruction = mockConstruction(Version.class, context -> {
            assertEquals(version, context.arguments().getFirst());
            return withSettings();
        });
        MockedConstruction<freemarker.template.Configuration> configurationMockedConstruction = mockConstruction(freemarker.template.Configuration.class, context -> {
            assertEquals(versionMockedConstruction.constructed().getFirst(), context.arguments().getFirst());
            return withSettings();
        });

        freeMarkerWrapper.sessionOpened();
        assertEquals(freeMarkerWrapper.getConfiguration(), configurationMockedConstruction.constructed().getFirst());

        versionMockedConstruction.close();
        configurationMockedConstruction.close();
    }

    @Test
    @DisplayName("interpolate should create a template from the provided source, interpolating it with the provided vars and returning the interpolated string")
    void interpolate() throws TemplateException, IOException {
        final String source = "source";
        final Map<String, Object> vars = Map.of("one", "value");

        MockedConstruction<StringReader> stringReaderMockedConstruction = mockConstruction(StringReader.class, context -> {
            assertEquals(source, context.arguments().getFirst());
            return withSettings();
        });
        MockedConstruction<Template> templateMockedConstruction = mockConstruction(Template.class, context -> {
            assertEquals("freemarker", context.arguments().getFirst());
            assertEquals(stringReaderMockedConstruction.constructed().getFirst(), context.arguments().get(1));
            assertEquals(configuration, context.arguments().get(2));
            return withSettings();
        });
        MockedConstruction<StringWriter> stringWriterMockedConstruction = mockConstruction(StringWriter.class, context -> withSettings());

        final String actual = freeMarkerWrapper.interpolate(source, vars);

        final Template template = templateMockedConstruction.constructed().getFirst();
        final Writer writer = stringWriterMockedConstruction.constructed().getFirst();
        verify(template).process(vars, writer);
        assertEquals(writer.toString(), actual);

        stringReaderMockedConstruction.close();
        templateMockedConstruction.close();
        stringWriterMockedConstruction.close();
    }
}
