package io.github.giulong.spectrum.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.aventstack.extentreports.ExtentTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class StatefulExtentTestTest {

    @Mock
    private ExtentTest currentNode;

    @Mock
    private ExtentTest previousNode;

    @Mock
    private ExtentTest newNode;

    private StatefulExtentTest statefulExtentTest;

    @BeforeEach
    void beforeEach() {
        // @InjectMocks doesn't work with multiple instances of the same type,
        // such as all the "nodes" here, that are all of type ExtentTest
        // See https://github.com/mockito/mockito/issues/1066
        statefulExtentTest = StatefulExtentTest
                .builder()
                .currentNode(currentNode)
                .previousNode(previousNode)
                .build();
    }

    @Test
    @DisplayName("createNode should create a new node, set the previous one and return the new")
    void createNode() {
        final String name = "name";

        when(currentNode.createNode(name)).thenReturn(newNode);

        assertEquals(newNode, statefulExtentTest.createNode(name));

        assertEquals(currentNode, statefulExtentTest.getPreviousNode());
        assertEquals(newNode, statefulExtentTest.getCurrentNode());
    }

    @Test
    @DisplayName("closeNode should set the previous node to the current one")
    void closeNode() {
        assertEquals(previousNode, statefulExtentTest.closeNode());

        assertEquals(previousNode, statefulExtentTest.getCurrentNode());
    }
}
