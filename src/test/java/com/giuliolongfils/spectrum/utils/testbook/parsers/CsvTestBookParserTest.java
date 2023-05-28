package com.giuliolongfils.spectrum.utils.testbook.parsers;

import com.giuliolongfils.spectrum.utils.FileReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CsvTestBookParser")
class CsvTestBookParserTest {

    private MockedStatic<FileReader> fileReaderMockedStatic;

    @Mock
    private FileReader fileReader;

    @InjectMocks
    private CsvTestBookParser testBookParser;

    @BeforeEach
    public void beforeEach() {
        fileReaderMockedStatic = mockStatic(FileReader.class);
    }

    @AfterEach
    public void afterEach() {
        fileReaderMockedStatic.close();
    }

    @DisplayName("parse should throw an IllegalArgumentException")
    @ParameterizedTest(name = "with line {0}")
    @ValueSource(strings = {"a", "a,", "a,b,c", "a,b,c,d"})
    public void parseException(final String line) {
        final String path = "path";
        when(FileReader.getInstance()).thenReturn(fileReader);
        when(fileReader.read(String.format("/%s", path))).thenReturn(line);

        testBookParser.setPath(path);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> testBookParser.parse());
        assertEquals(String.format("Line '%s' in TestBook doesn't match pattern %s", line, testBookParser.getRegex()), exception.getMessage());
    }

    @DisplayName("parse should work")
    @ParameterizedTest(name = "with line {0}")
    @ValueSource(strings = {"a,b", "a,b,23"})
    public void parseValid(final String line) {
        final String path = "path";
        when(FileReader.getInstance()).thenReturn(fileReader);
        when(fileReader.read(String.format("/%s", path))).thenReturn(line);

        testBookParser.setPath(path);

        assertDoesNotThrow(() -> testBookParser.parse());
    }
}
