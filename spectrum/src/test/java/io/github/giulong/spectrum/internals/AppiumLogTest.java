package io.github.giulong.spectrum.internals;

import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.slf4j.event.Level;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AppiumLogTest {

    private static final String LOG_MESSAGE = "log message";

    private MockedConstruction<StringBuffer> stringBufferMockedConstruction;

    @Mock
    @SuppressWarnings("unused")
    private Level level;

    @InjectMocks
    private AppiumLog appiumLog;

    @BeforeEach
    void beforeEach() {
        stringBufferMockedConstruction = mockConstruction(StringBuffer.class);
        Reflections.setField("stringBuffer", appiumLog, new StringBuffer(LOG_MESSAGE));
    }

    @AfterEach
    void afterEach() {
        stringBufferMockedConstruction.close();
    }

    @Test
    @DisplayName("write should append the char provided if it's not a line break")
    void write() {
        final char c = 'a';
        final List<StringBuffer> stringBuffers = stringBufferMockedConstruction.constructed();

        appiumLog.write(c);
        verify(stringBuffers.getFirst()).append(c);
    }

    @Test
    @DisplayName("write should flush the buffer when the char provided is a line break")
    void writeFlush() {
        final char c = '\n';

        appiumLog.write(c);
        final StringBuffer stringBuffer = Reflections.getFieldValue("stringBuffer", appiumLog, StringBuffer.class);

        verify(stringBuffer).setLength(0);
    }

    @Test
    @DisplayName("flush should write the buffer's content at the provided level and re-initialise it")
    void flush() {
        final List<StringBuffer> stringBuffers = stringBufferMockedConstruction.constructed();
        assertEquals(stringBuffers.getFirst(), Reflections.getFieldValue("stringBuffer", appiumLog));

        appiumLog.flush();
        final StringBuffer stringBuffer = Reflections.getFieldValue("stringBuffer", appiumLog, StringBuffer.class);

        verify(stringBuffer).setLength(0);
    }
}
