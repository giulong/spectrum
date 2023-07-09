package io.github.giulong.spectrum.utils.events;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver;
import io.github.giulong.spectrum.pojos.events.Event;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.function.Function;

import static com.aventstack.extentreports.Status.*;
import static io.github.giulong.spectrum.enums.Result.*;
import static io.github.giulong.spectrum.extensions.resolvers.ExtentTestResolver.EXTENT_TEST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentTestHandler")
class ExtentTestHandlerTest {

    private final ExtentColor color = ExtentColor.TRANSPARENT;

    private static MockedStatic<ExtentTestResolver> extentTestResolverMockedStatic;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtentTest extentTest;

    @Mock
    private SpectrumTest<?> spectrumTest;

    @Mock
    private RuntimeException exception;

    @Mock
    private Event event;

    @Captor
    private ArgumentCaptor<Function<String, ExtentTest>> functionArgumentCaptor;

    @Captor
    private ArgumentCaptor<Markup> markupArgumentCaptor;

    @Captor
    private ArgumentCaptor<Markup> skipMarkupArgumentCaptor;

    @InjectMocks
    private ExtentTestHandler extentTestHandler;

    @BeforeEach
    public void beforeEach() {
        extentTestResolverMockedStatic = mockStatic(ExtentTestResolver.class);
    }

    @AfterEach
    public void afterEach() {
        extentTestResolverMockedStatic.close();
    }

    private void addStubs() {
        when(event.getContext()).thenReturn(context);
        when(event.getClassName()).thenReturn("className");
        when(event.getTestName()).thenReturn("displayName");
        when(context.getStore(GLOBAL)).thenReturn(store);
        when(ExtentTestResolver.createExtentTestFrom(context)).thenReturn(extentTest);
        when(store.getOrComputeIfAbsent(eq(EXTENT_TEST), functionArgumentCaptor.capture(), eq(ExtentTest.class))).thenReturn(extentTest);
    }

    @DisplayName("testDisabled should create the test in the report and delegate to finalizeTest")
    @ParameterizedTest(name = "with method {0} we expect {1}")
    @CsvSource(value = {
            "noReasonMethod,no reason",
            "reasonMethod,specific reason"
    })
    public void testDisabled(final String methodName, String expected) throws NoSuchMethodException {
        addStubs();

        when(event.getResult()).thenReturn(DISABLED);
        when(ExtentTestResolver.getColorOf(SKIP)).thenReturn(color);
        when(context.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));

        extentTestHandler.handle(event);
        verify(extentTest).skip(skipMarkupArgumentCaptor.capture());

        assertEquals("<span class='badge white-text transparent'>Skipped: " + expected + "</span>", skipMarkupArgumentCaptor.getValue().getMarkup());
    }

    @Test
    @DisplayName("testFailed should add a screenshot to the report and delegate to finalizeTest")
    public void testFailed() {
        addStubs();
        when(context.getRequiredTestInstance()).thenReturn(spectrumTest);
        when(context.getExecutionException()).thenReturn(Optional.of(exception));

        when(event.getResult()).thenReturn(FAILED);
        extentTestHandler.handle(event);
        verify(extentTest).fail(exception);
        verify(spectrumTest).addScreenshotToReport("<span class='badge white-text red'>TEST FAILED</span>", FAIL);
    }

    @Test
    @DisplayName("handle should add a log in the extent report by default")
    public void handleDefault() {
        addStubs();
        when(event.getResult()).thenReturn(SUCCESSFUL);
        when(ExtentTestResolver.getColorOf(PASS)).thenReturn(color);

        extentTestHandler.handle(event);

        verify(extentTest).log(eq(PASS), markupArgumentCaptor.capture());
        assertEquals("<span class='badge white-text transparent'>END TEST</span>", markupArgumentCaptor.getValue().getMarkup());
    }

    @Disabled
    @SuppressWarnings("unused")
    private void noReasonMethod() {
    }

    @Disabled("specific reason")
    @SuppressWarnings("unused")
    private void reasonMethod() {
    }
}
