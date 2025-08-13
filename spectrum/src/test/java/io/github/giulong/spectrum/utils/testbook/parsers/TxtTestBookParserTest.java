package io.github.giulong.spectrum.utils.testbook.parsers;

import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class TxtTestBookParserTest {

    private MockedStatic<FileUtils> fileUtilsMockedStatic;

    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private TxtTestBookParser testBookParser;

    @BeforeEach
    void beforeEach() {
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
    }

    @AfterEach
    void afterEach() {
        fileUtilsMockedStatic.close();
    }

    @DisplayName("parse should throw an IllegalArgumentException")
    @ParameterizedTest(name = "with line {0}")
    @ValueSource(strings = {"a", "a::", "::a", "a::b##", "a::b::c"})
    void parseException(final String line) {
        final String path = "path";
        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.read(path)).thenReturn(line);
        Reflections.setField("path", testBookParser, path);

        assertThrowsExactly(IllegalArgumentException.class, () -> testBookParser.parse(),
                String.format("Line '%s' in TestBook doesn't match pattern %s", line, testBookParser.getRegex()));
    }

    @DisplayName("parse should work")
    @ParameterizedTest(name = "with line {0}")
    @ValueSource(strings = {"a::b", "a::b##23"})
    void parseValid(final String line) {
        final String path = "path";
        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.read(path)).thenReturn(line);
        Reflections.setField("path", testBookParser, path);

        Assertions.assertDoesNotThrow(() -> testBookParser.parse());
    }
}
