package com.github.giulong.spectrum.utils;

import com.github.giulong.spectrum.pojos.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static java.util.Locale.US;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FreeMarkerWrapper")
class FreeMarkerWrapperTest {

    @Mock
    private freemarker.template.Configuration configuration;

    @Mock
    private Configuration.FreeMarker freeMarker;

    @InjectMocks
    private FreeMarkerWrapper freeMarkerWrapper;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        assertSame(FreeMarkerWrapper.getInstance(), FreeMarkerWrapper.getInstance());
    }

    @Test
    @DisplayName("setupFrom should setup the configuration from the freemarker node in the configuration.yaml")
    public void setupFrom() {
        final String version = "version";
        final String numberFormat = "numberFormat";

        when(freeMarker.getVersion()).thenReturn(version);
        when(freeMarker.getLocale()).thenReturn(US);
        when(freeMarker.getNumberFormat()).thenReturn(numberFormat);

        MockedConstruction<Version> versionMockedConstruction = mockConstruction(Version.class, context -> {
            assertEquals(version, context.arguments().get(0));
            return withSettings();
        });
        MockedConstruction<freemarker.template.Configuration> configurationMockedConstruction = mockConstruction(freemarker.template.Configuration.class, context -> {
            assertEquals(versionMockedConstruction.constructed().get(0), context.arguments().get(0));
            return withSettings();
        });

        freeMarkerWrapper.setupFrom(freeMarker);
        assertEquals(freeMarkerWrapper.getConfiguration(), configurationMockedConstruction.constructed().get(0));

        versionMockedConstruction.close();
        configurationMockedConstruction.close();
    }

    @Test
    @DisplayName("interpolate should create a template from the provided source, interpolating it with the provided vars and returning the interpolated string")
    public void interpolate() throws TemplateException, IOException {
        final String templateName = "templateName";
        final String source = "source";
        final Map<String, Object> vars = Map.of("one", "value");

        MockedConstruction<StringReader> stringReaderMockedConstruction = mockConstruction(StringReader.class, context -> {
            assertEquals(source, context.arguments().get(0));
            return withSettings();
        });
        MockedConstruction<Template> templateMockedConstruction = mockConstruction(Template.class, context -> {
            assertEquals(templateName, context.arguments().get(0));
            assertEquals(stringReaderMockedConstruction.constructed().get(0), context.arguments().get(1));
            assertEquals(configuration, context.arguments().get(2));
            return withSettings();
        });
        MockedConstruction<StringWriter> stringWriterMockedConstruction = mockConstruction(StringWriter.class, context -> withSettings());

        final String actual = freeMarkerWrapper.interpolate(templateName, source, vars);

        final Template template = templateMockedConstruction.constructed().get(0);
        final Writer writer = stringWriterMockedConstruction.constructed().get(0);
        verify(template).process(vars, writer);
        assertEquals(writer.toString(), actual);

        stringReaderMockedConstruction.close();
        templateMockedConstruction.close();
        stringWriterMockedConstruction.close();
    }
}
