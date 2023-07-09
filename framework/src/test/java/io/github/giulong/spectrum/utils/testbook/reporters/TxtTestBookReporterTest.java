package io.github.giulong.spectrum.utils.testbook.reporters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@DisplayName("TxtTestBookReporter")
class TxtTestBookReporterTest {

    private MockedStatic<Files> filesMockedStatic;

    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;

    @InjectMocks
    private TxtTestBookReporter testBookReporter;

    @BeforeEach
    public void beforeEach() {
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    public void afterEach() {
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("doOutputFrom should interpolate the timestamp in the provided template name, create the output dir and write the file in it")
    public void doOutputFrom() {
        final String interpolatedTemplate = "interpolatedTemplate";
        final String output = testBookReporter.getOutput();

        testBookReporter.doOutputFrom(interpolatedTemplate);
        filesMockedStatic.verify(() -> Files.createDirectories(Path.of(output).getParent()));
        filesMockedStatic.verify(() -> {
            Files.write(pathArgumentCaptor.capture(), eq(interpolatedTemplate.getBytes()));
            assertThat(pathArgumentCaptor.getValue().toString().replace("\\", "/"),
                    matchesPattern(output.replace("{timestamp}.txt", "[0-9]{2}-[0-9]{2}-[0-9]{4}_[0-9]{2}-[0-9]{2}-[0-9]{2}.txt")));
        });
    }
}
