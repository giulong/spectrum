package io.github.giulong.spectrum.internals;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.io.OutputStream;

@Slf4j
@Builder
public class BrowserLog extends OutputStream {

    private final Level level;

    @Default
    private StringBuffer mem = new StringBuffer();

    @Override
    public void write(final int b) {
        final char c = (char) b;
        if (c == '\n') {
            flush();
            return;
        }
        mem.append(c);
    }

    @Override
    public void flush() {
        log.atLevel(level).log(mem.toString());
        mem = new StringBuffer();
    }
}
