package io.github.giulong.spectrum.utils.tests_comparators;

import com.aventstack.extentreports.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.stream.Stream;

import static com.aventstack.extentreports.Status.FAIL;
import static com.aventstack.extentreports.Status.PASS;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.when;

class StatusComparatorTest {

    @InjectMocks
    private StatusComparator statusComparator;

    @Mock
    private com.aventstack.extentreports.model.Test test1;

    @Mock
    private com.aventstack.extentreports.model.Test test2;

    @DisplayName("compare should compare by tests' statuses")
    @ParameterizedTest(name = "with status1 {1} and status2 {2}, we expect {3}")
    @MethodSource("valuesProvider")
    public void compare(final Status status1, final Status status2, final int expected) {
        when(test1.getStatus()).thenReturn(status1);
        when(test2.getStatus()).thenReturn(status2);

        assertEquals(expected, statusComparator.compare(test1, test2));
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(PASS, FAIL, -1),
                arguments(FAIL, PASS, 1),
                arguments(PASS, PASS, 0)
        );
    }
}