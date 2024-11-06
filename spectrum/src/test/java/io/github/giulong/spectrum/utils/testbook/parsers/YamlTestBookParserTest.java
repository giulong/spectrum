package io.github.giulong.spectrum.utils.testbook.parsers;

import io.github.giulong.spectrum.pojos.testbook.TestBookTest;
import io.github.giulong.spectrum.utils.Reflections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class YamlTestBookParserTest {

    @InjectMocks
    private YamlTestBookParser testBookParser;

    @Test
    @DisplayName("parse should read the provided file line by line and return the list of test names")
    void parse() {
        Reflections.setField("path", testBookParser, "testbook.yaml");
        List<TestBookTest> actual = testBookParser.parse();
        assertEquals(3, actual.size());

        final TestBookTest test1 = TestBookTest.builder()
                .className("first class")
                .testName("one")
                .build();

        final TestBookTest test2 = TestBookTest.builder()
                .className("first class")
                .testName("two")
                .build();

        final TestBookTest test3 = TestBookTest.builder()
                .className("second class")
                .testName("one")
                .weight(12)
                .build();

        assertTrue(actual.containsAll(List.of(test1, test2, test3)));

        // weights are not considered in equals. We need to check them one by one
        actual
                .stream()
                .filter(t -> t.getClassName().equals("first class"))
                .map(TestBookTest::getWeight)
                .forEach(weight -> assertEquals(1, weight));

        assertEquals(12, actual
                .stream()
                .filter(t -> t.getClassName().equals("second class"))
                .findFirst()
                .orElseThrow()
                .getWeight());
    }
}
