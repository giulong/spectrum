package io.github.giulong.spectrum.utils;

import com.aventstack.extentreports.ExtentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatefulExtentTestTest {

    @Mock
    private ExtentTest currentNode;

    @Mock
    private ExtentTest previousNode;

    @Mock
    private ExtentTest newNode;

    private StatefulExtentTest statefulExtentTest;

    @BeforeEach
    public void beforeEach() {
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
    public void createNode() {
        final String name = "name";

        when(currentNode.createNode(name)).thenReturn(newNode);

        assertEquals(newNode, statefulExtentTest.createNode(name));

        assertEquals(currentNode, statefulExtentTest.getPreviousNode());
        assertEquals(newNode, statefulExtentTest.getCurrentNode());
    }

    @Test
    @DisplayName("closeNode should set the previous node to the current one")
    public void closeNode() {
        assertEquals(previousNode, statefulExtentTest.closeNode());

        assertEquals(previousNode, statefulExtentTest.getCurrentNode());
    }
}