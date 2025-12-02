package io.github.giulong.spectrum.utils.tests_comparators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class NameComparatorTest {

    @Mock
    private com.aventstack.extentreports.model.Test test1;

    @Mock
    private com.aventstack.extentreports.model.Test test2;

    @InjectMocks
    private NameComparator nameComparator;

    @DisplayName("compare should compare by tests' names")
    @ParameterizedTest(name = "with name1 {1} and name2 {2}, we expect {3}")
    @MethodSource("valuesProvider")
    void compare(final String name1, final String name2, final int expected) {
        when(test1.getName()).thenReturn(name1);
        when(test2.getName()).thenReturn(name2);

        assertEquals(expected, nameComparator.compare(test1, test2));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments("aaa", "bbb", -1),
                arguments("bbb", "aaa", 1),
                arguments("aaa", "aaa", 0));
    }
}
