package io.github.giulong.spectrum.utils.testbook.reporters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogTestBookReporter")
class LogTestBookReporterTest {

    @InjectMocks
    private LogTestBookReporter testBookReporter;

    @Test
    @DisplayName("doOutputFrom should just log the interpolated template")
    public void doOutputFrom() {
        final String interpolatedTemplate = "interpolatedTemplate";
        testBookReporter.doOutputFrom(interpolatedTemplate);

        // no verifications/assertions needed
    }
}
