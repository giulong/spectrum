package io.github.giulong.spectrum.exceptions;

import static com.aventstack.extentreports.markuputils.ExtentColor.RED;
import static io.github.giulong.spectrum.extensions.resolvers.StatefulExtentTestResolver.STATEFUL_EXTENT_TEST;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.github.giulong.spectrum.utils.StatefulExtentTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class VisualRegressionExceptionTest {

    private MockedStatic<MarkupHelper> markupHelperMockedStatic;

    @Mock
    @SuppressWarnings("unused")
    private Object backtrace;

    @Mock
    private Markup markup;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Mock
    private ExtentTest extentTest;

    @InjectMocks
    private VisualRegressionException visualRegressionException = new VisualRegressionException(String.format("There were %d visual regressions", 3));

    @BeforeEach
    void beforeEach() {
        markupHelperMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        markupHelperMockedStatic.close();
    }

    @Test
    @DisplayName("showInReportFrom should fail the extent report with the details")
    void showInReportFrom() {
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(store.get(STATEFUL_EXTENT_TEST, StatefulExtentTest.class)).thenReturn(statefulExtentTest);
        when(statefulExtentTest.getCurrentNode()).thenReturn(extentTest);

        // getReportDetails
        final String reportDetails = "reportDetails";
        when(MarkupHelper.createLabel("TEST FAILED", RED)).thenReturn(markup);
        when(markup.getMarkup()).thenReturn(reportDetails);

        visualRegressionException.showInReportFrom(context);

        verify(extentTest).fail(reportDetails);
    }
}
