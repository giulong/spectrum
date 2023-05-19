package com.giuliolongfils.spectrum.utils.testbook.parsers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@DisplayName("TxtTestBookParser")
class TxtTestBookParserTest {

    @InjectMocks
    private TxtTestBookParser testBookParser;

    @Test
    @DisplayName("parse should read the provided file line by line and return the list of test names")
    public void parse() {
        testBookParser.setPath("testbook.txt");
        List<String> actual = testBookParser.parse();
        assertEquals(List.of("test 1::one", "another test::another"), actual);
    }

    @DisplayName("validate should throw an exception if the provided line doesn't match the pattern")
    @ParameterizedTest(name = "with line {0}")
    @ValueSource(strings = {"a", "a::", "::a"})
    public void validate(final String line) {
        Exception exception = assertThrows(RuntimeException.class, () -> testBookParser.validate(line));
        assertEquals(String.format("Line '%s' in TestBook doesn't match pattern ClassName::TestName", line), exception.getMessage());
    }
}