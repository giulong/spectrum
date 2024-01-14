package io.github.giulong.spectrum.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.utils.Configuration.FreeMarker;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Getter
@NoArgsConstructor(access = PRIVATE)
public final class FreeMarkerWrapper implements SessionHook {

    private static final FreeMarkerWrapper INSTANCE = new FreeMarkerWrapper();

    private Configuration configuration;

    public static FreeMarkerWrapper getInstance() {
        return INSTANCE;
    }

    @Override
    public void sessionOpenedFrom(final io.github.giulong.spectrum.utils.Configuration spectrumConfiguration) {
        log.debug("Session opened hook");

        final FreeMarker freeMarker = spectrumConfiguration.getFreeMarker();
        final String version = freeMarker.getVersion();

        log.debug("Configuring FreeMarker version {}", version);
        this.configuration = new Configuration(new Version(version));
        this.configuration.setLocale(freeMarker.getLocale());
        this.configuration.setNumberFormat(freeMarker.getNumberFormat());
    }

    @SneakyThrows
    public String interpolate(final String templateName, final String source, final Map<String, Object> vars) {
        final Template template = new Template(templateName, new StringReader(source), configuration);
        final Writer writer = new StringWriter();
        template.process(vars, writer);
        return writer.toString();
    }
}
