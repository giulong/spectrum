package com.giuliolongfils.spectrum.utils;

import com.giuliolongfils.spectrum.utils.testbook.parsers.TxtTestBookParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        testBookParser.setPath("src/test/resources/testbook.txt");
        List<String> actual = testBookParser.parse();
        assertEquals(List.of("test 1", "another test"), actual);
    }

    @Test
    @DisplayName("parse should throw an exception if the file doesn't exist")
    public void parseNotExisting() {
        assertThrows(RuntimeException.class, () -> testBookParser.parse());
    }
}