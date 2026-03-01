package io.github.giulong.spectrum.generation.generators;

import static freemarker.template.Configuration.VERSION_2_3_34;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

class SpectrumTestGeneratorTest {

    @Mock
    private FileUtils fileUtils;

    @Mock
    private Path destination;

    @Mock
    private Path packagePath;

    @InjectMocks
    private SpectrumTestGenerator generator;

    @Test
    @DisplayName("generate should read the template, interpolate it with freemarker and write the generated class")
    void generate() throws TemplateException, IOException {
        final String spectrumTestTemplate = "spectrumTestTemplate";
        final String className = "className";

        Reflections.setField("fileUtils", generator, fileUtils);
        Reflections.setField("className", generator, className);
        Reflections.setField("destination", generator, destination);
        Reflections.setField("packagePath", generator, packagePath);

        when(fileUtils.read("generation/SpectrumTest.java.template")).thenReturn(spectrumTestTemplate);
        when(destination.resolve(packagePath)).thenReturn(destination);
        when(destination.resolve(className)).thenReturn(destination);

        final List<StringReader> stringReaders = new ArrayList<>();
        final List<Configuration> configurations = new ArrayList<>();

        try (MockedConstruction<Configuration> configurationMockedConstruction = mockConstruction((mock, context) -> assertEquals(VERSION_2_3_34, context.arguments().getFirst()));
                MockedConstruction<StringReader> stringReaderMockedConstruction = mockConstruction((mock, context) -> {
                    assertEquals(spectrumTestTemplate, context.arguments().getFirst());
                });
                MockedConstruction<Template> templateMockedConstruction = mockConstruction((mock, context) -> {
                    assertEquals("spectrumTest", context.arguments().getFirst());
                    stringReaders.add((StringReader) context.arguments().get(1));
                    configurations.add((Configuration) context.arguments().get(2));
                });
                MockedConstruction<StringWriter> writerMockedConstruction = mockConstruction()) {

            generator.generate();

            final Configuration constructedConfiguration = configurationMockedConstruction.constructed().getFirst();
            assertEquals(constructedConfiguration, configurations.getFirst());
            verify(constructedConfiguration).setDefaultEncoding("UTF-8");

            final StringReader constructedStringReader = stringReaderMockedConstruction.constructed().getFirst();
            assertEquals(constructedStringReader, stringReaders.getFirst());

            final Template constructedTemplate = templateMockedConstruction.constructed().getFirst();
            final Writer constructedWriter = writerMockedConstruction.constructed().getFirst();
            verify(constructedTemplate).process(generator, constructedWriter);

            verify(fileUtils).write(destination, constructedWriter.toString());
        }
    }
}
