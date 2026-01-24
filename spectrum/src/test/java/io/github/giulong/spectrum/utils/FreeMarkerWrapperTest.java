package io.github.giulong.spectrum.utils;

import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import io.github.giulong.spectrum.MockFinal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

class FreeMarkerWrapperTest {

    @Mock
    private freemarker.template.Configuration configuration;

    @MockFinal
    @SuppressWarnings("unused")
    private Configuration spectrumConfiguration;

    @MockFinal
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @Mock
    private Configuration.FreeMarker freeMarker;

    @InjectMocks
    private FreeMarkerWrapper freeMarkerWrapper;

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

        MockedConstruction<Version> versionMockedConstruction = mockConstruction((mock, context) -> assertEquals(version, context.arguments().getFirst()));
        MockedConstruction<freemarker.template.Configuration> configurationMockedConstruction = mockConstruction(
                (mock, context) -> assertEquals(versionMockedConstruction.constructed().getFirst(), context.arguments().getFirst()));

        freeMarkerWrapper.sessionOpened();
        assertEquals(Reflections.getFieldValue("configuration", freeMarkerWrapper), configurationMockedConstruction.constructed().getFirst());

        versionMockedConstruction.close();
        configurationMockedConstruction.close();
    }

    @Test
    @DisplayName("interpolate should create a template from the provided source, interpolating it with the provided vars and returning the interpolated string")
    void interpolate() throws TemplateException, IOException {
        final String source = "source";
        final Map<String, Object> vars = Map.of("one", "value");

        MockedConstruction<StringReader> stringReaderMockedConstruction = mockConstruction((mock, context) -> assertEquals(source, context.arguments().getFirst()));
        MockedConstruction<Template> templateMockedConstruction = mockConstruction((mock, context) -> {
            assertEquals("freemarker", context.arguments().getFirst());
            assertEquals(stringReaderMockedConstruction.constructed().getFirst(), context.arguments().get(1));
            assertEquals(configuration, context.arguments().get(2));
        });
        MockedConstruction<StringWriter> stringWriterMockedConstruction = mockConstruction();

        final String actual = freeMarkerWrapper.interpolate(source, vars);

        final Template template = templateMockedConstruction.constructed().getFirst();
        final Writer writer = stringWriterMockedConstruction.constructed().getFirst();
        verify(template).process(vars, writer);
        assertEquals(writer.toString(), actual);

        stringReaderMockedConstruction.close();
        templateMockedConstruction.close();
        stringWriterMockedConstruction.close();
    }

    @Test
    @DisplayName("interpolateTemplate should load the template and then just delegate to the interpolate method")
    void interpolateTemplate() throws TemplateException, IOException {
        final String templateName = "templateName";
        final String source = "source";
        final Map<String, Object> vars = Map.of("one", "value");

        MockedConstruction<StringReader> stringReaderMockedConstruction = mockConstruction((mock, context) -> assertEquals(source, context.arguments().getFirst()));
        MockedConstruction<Template> templateMockedConstruction = mockConstruction((mock, context) -> {
            assertEquals("freemarker", context.arguments().getFirst());
            assertEquals(stringReaderMockedConstruction.constructed().getFirst(), context.arguments().get(1));
            assertEquals(configuration, context.arguments().get(2));
        });
        MockedConstruction<StringWriter> stringWriterMockedConstruction = mockConstruction();
        when(fileUtils.readTemplate(templateName)).thenReturn(source);

        final String actual = freeMarkerWrapper.interpolateTemplate(templateName, vars);

        final Template template = templateMockedConstruction.constructed().getFirst();
        final Writer writer = stringWriterMockedConstruction.constructed().getFirst();
        verify(template).process(vars, writer);
        assertEquals(writer.toString(), actual);

        stringReaderMockedConstruction.close();
        templateMockedConstruction.close();
        stringWriterMockedConstruction.close();
    }
}
