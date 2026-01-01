package io.github.giulong.spectrum.exceptions;

import static com.aventstack.extentreports.markuputils.ExtentColor.RED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;

import io.github.giulong.spectrum.SpectrumTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class TestFailedExceptionTest {

    private final String reportDetails = "reportDetails";

    private MockedStatic<MarkupHelper> markupHelperMockedStatic;

    @Mock
    private Markup markup;

    @Mock
    private ExtensionContext context;

    @Mock
    private SpectrumTest<?> spectrumTest;

    @InjectMocks
    private TestFailedException testFailedException;

    @BeforeEach
    void beforeEach() {
        markupHelperMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        markupHelperMockedStatic.close();
    }

    @Test
    @DisplayName("showInReportFrom should call screenshotFail with the report details")
    void showInReportFrom() {
        // getReportDetails
        when(MarkupHelper.createLabel("TEST FAILED", RED)).thenReturn(markup);
        when(markup.getMarkup()).thenReturn(reportDetails);

        when(context.getRequiredTestInstance()).thenReturn(spectrumTest);

        testFailedException.showInReportFrom(context);

        verify(spectrumTest).screenshotFail(reportDetails);
    }

    @Test
    @DisplayName("getReportDetails should return the failed markup")
    void getReportDetails() {
        when(MarkupHelper.createLabel("TEST FAILED", RED)).thenReturn(markup);
        when(markup.getMarkup()).thenReturn(reportDetails);

        assertEquals(reportDetails, testFailedException.getReportDetails());
    }
}
