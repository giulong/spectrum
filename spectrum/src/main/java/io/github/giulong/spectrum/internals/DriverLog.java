package io.github.giulong.spectrum.internals;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.io.OutputStream;

@Slf4j
@Builder
public class DriverLog extends OutputStream {

    private final Level level;

    @Default
    private StringBuffer stringBuffer = new StringBuffer();

    @Override
    public void write(final int b) {
        final char c = (char) b;
        if (c == '\n') {
            flush();
            return;
        }

        stringBuffer.append(c);
    }

    @Override
    public void flush() {
        log.atLevel(level).log(stringBuffer.toString());
        stringBuffer.setLength(0);
    }
}
