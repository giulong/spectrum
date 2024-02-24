package io.github.giulong.spectrum.internals;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;

import java.io.OutputStream;

@Slf4j
@Builder
public class AppiumLog extends OutputStream {

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
        log.atLevel(level).log(stringBuffer
                .toString()
                .replaceAll("\\u001b\\[([0-9]{1,3}(;[0-9]{1,2};?)?)*[mGK]", "")
                .replace("\\u001B", ""));

        stringBuffer.setLength(0);
    }
}
