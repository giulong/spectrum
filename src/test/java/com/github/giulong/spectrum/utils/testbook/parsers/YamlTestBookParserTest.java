package com.github.giulong.spectrum.utils.testbook.parsers;

import com.github.giulong.spectrum.pojos.testbook.Test;
import org.junit.jupiter.api.DisplayName;
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

    @org.junit.jupiter.api.Test
    @DisplayName("parse should read the provided file line by line and return the list of test names")
    public void parse() {
        testBookParser.setPath("testbook.yaml");
        List<Test> actual = testBookParser.parse();
        assertEquals(3, actual.size());

        final Test test1 = Test.builder()
                .className("first class")
                .testName("one")
                .build();

        final Test test2 = Test.builder()
                .className("first class")
                .testName("two")
                .build();

        final Test test3 = Test.builder()
                .className("second class")
                .testName("one")
                .weight(12)
                .build();

        assertTrue(actual.containsAll(List.of(test1, test2, test3)));

        // weights are not considered in equals. We need to check them one by one
        actual
                .stream()
                .filter(t -> t.getClassName().equals("first class"))
                .map(Test::getWeight)
                .forEach(weight -> assertEquals(1, weight));

        assertEquals(12, actual
                .stream()
                .filter(t -> t.getClassName().equals("second class"))
                .findFirst()
                .orElseThrow()
                .getWeight());
    }
}
