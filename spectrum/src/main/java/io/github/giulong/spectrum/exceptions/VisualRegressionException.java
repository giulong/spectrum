package io.github.giulong.spectrum.exceptions;

import io.github.giulong.spectrum.utils.StatefulExtentTest;
import org.junit.jupiter.api.extension.ExtensionContext;

import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class VisualRegressionException extends TestFailedException {

    public VisualRegressionException(final String message) {
        super(message);
    }

    @Override
    public void showInReportFrom(final ExtensionContext context) {
        context
                .getStore(GLOBAL)
                .get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)
                .getCurrentNode()
                .fail(getReportDetails());
    }
}
