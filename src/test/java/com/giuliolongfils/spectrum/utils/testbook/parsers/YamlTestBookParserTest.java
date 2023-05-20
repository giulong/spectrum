package com.giuliolongfils.spectrum.utils.testbook.parsers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("YamlTestBookParser")
class YamlTestBookParserTest {

    @InjectMocks
    private YamlTestBookParser testBookParser;

    @Test
    @DisplayName("parse should read the provided file line by line and return the list of test names")
    public void parse() {
        testBookParser.setPath("testbook.yaml");
        List<String> actual = testBookParser.parse();
        assertEquals(3, actual.size());
        assertTrue(actual.containsAll(List.of("first class::one", "first class::two", "second class::three")));
    }

    @Test
    @DisplayName("validate for yaml testbook shouldn't do nothing")
    public void validate() {
        testBookParser.validate("anything");
    }
}