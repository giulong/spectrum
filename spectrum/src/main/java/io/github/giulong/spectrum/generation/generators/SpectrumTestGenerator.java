package io.github.giulong.spectrum.generation.generators;

import static freemarker.template.Configuration.VERSION_2_3_34;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

import io.github.giulong.spectrum.generation.server.actions.Action;
import io.github.giulong.spectrum.utils.FileUtils;

import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Slf4j
@Getter
@Builder
public class SpectrumTestGenerator {

    private final FileUtils fileUtils = FileUtils.getInstance();

    private List<Action> actions;
    private Path destination;
    private Path packagePath;
    private String className;

    @SneakyThrows
    public void generate() {
        final Configuration configuration = new Configuration(VERSION_2_3_34);
        configuration.setDefaultEncoding("UTF-8");

        final StringReader stringReader = new StringReader(fileUtils.read("generation/SpectrumTest.java.template"));
        final Template template = new Template("spectrumTest", stringReader, configuration);
        final Writer writer = new StringWriter();

        template.process(this, writer);

        final Path fullDestination = destination.resolve(packagePath).resolve(className);
        log.info("Writing generated test class at '{}'", fullDestination);
        fileUtils.write(fullDestination, writer.toString());
    }
}
