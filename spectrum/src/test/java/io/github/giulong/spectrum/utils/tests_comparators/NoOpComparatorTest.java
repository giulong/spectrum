package io.github.giulong.spectrum.utils.tests_comparators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NoOpComparatorTest {

    @InjectMocks
    private NoOpComparator noOpComparator;

    @Mock
    private com.aventstack.extentreports.model.Test test1;

    @Mock
    private com.aventstack.extentreports.model.Test test2;

    @Test
    @DisplayName("compare should just return 0")
    void compare() {
        assertEquals(0, noOpComparator.compare(test1, test2));
    }
}