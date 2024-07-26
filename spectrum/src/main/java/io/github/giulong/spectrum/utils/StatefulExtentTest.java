package io.github.giulong.spectrum.utils;

import com.aventstack.extentreports.ExtentTest;
import lombok.*;

@Getter
@Builder
public class StatefulExtentTest {

    private ExtentTest currentNode;
    private ExtentTest previousNode;

    @Setter
    private String displayName;

    public ExtentTest createNode(final String name) {
        previousNode = currentNode;
        currentNode = currentNode.createNode(name);

        return currentNode;
    }

    public ExtentTest closeNode() {
        currentNode = previousNode;

        return currentNode;
    }
}
