package io.github.giulong.spectrum.utils.testbook.parsers;

import io.github.giulong.spectrum.pojos.testbook.TestBookTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @Test
    @DisplayName("parse should read the configured file line by line and return the corresponding list of TestBookTests")
    public void parse() {
        testBookParser.setPath("testbook.csv");

        List<TestBookTest> actual = testBookParser.parse();

        final TestBookTest test1 = TestBookTest.builder()
                .className("test 1")
                .testName("one")
                .build();

        final TestBookTest test2 = TestBookTest.builder()
                .className("another test")
                .testName("another")
                .build();

        final TestBookTest test3 = TestBookTest.builder()
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
