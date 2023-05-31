package com.github.giulong.spectrum.utils.testbook.parsers;

import com.github.giulong.spectrum.pojos.testbook.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimpleTestBookParser")
class SimpleTestBookParserTest {

    @InjectMocks
    private CsvTestBookParser testBookParser;

    @org.junit.jupiter.api.Test
    @DisplayName("parse should read the configured file line by line and return the corresponding list of TestBookTests")
    public void parse() {
        testBookParser.setPath("testbook.csv");

        List<Test> actual = testBookParser.parse();

        final Test test1 = Test.builder()
                .className("test 1")
                .testName("one")
                .build();

        final Test test2 = Test.builder()
                .className("another test")
                .testName("another")
                .build();

        final Test test3 = Test.builder()
                .className("three")
                .testName("name")
                .weight(3)
                .build();

        assertEquals(List.of(test1, test2, test3), actual);

        // weights are not considered in equals. We need to check them one by one
        assertEquals(1, actual.get(0).getWeight());
        assertEquals(1, actual.get(1).getWeight());
        assertEquals(3, actual.get(2).getWeight());
    }
}
