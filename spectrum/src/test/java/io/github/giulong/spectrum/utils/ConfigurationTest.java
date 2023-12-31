package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.utils.summary.Summary;
import io.github.giulong.spectrum.utils.testbook.TestBook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Configuration")
class ConfigurationTest {

    @Mock
    private TestBook testBook;

    @Mock
    private Summary summary;

    @InjectMocks
    private Configuration configuration;

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(Configuration.getInstance(), Configuration.getInstance());
    }

    @Test
    @DisplayName("sessionOpened should parse the testbook")
    public void sessionOpened() {
        configuration.sessionOpened();

        verify(testBook).parse();
    }

    @Test
    @DisplayName("sessionClosed should flush the testbook")
    public void sessionClosed() {
        configuration.sessionClosed();

        verify(testBook).flush();
        verify(summary).sessionClosed();
    }
}
