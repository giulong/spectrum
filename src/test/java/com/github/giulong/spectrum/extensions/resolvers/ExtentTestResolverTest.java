package com.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.aventstack.extentreports.Status.*;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExtentTestResolver")
class ExtentTestResolverTest {

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext extensionContext;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtentReports extentReports;

    @Mock
    private ExtentTest extentTest;

    @InjectMocks
    private ExtentTestResolver extentTestResolver;

    private final String className = "className";
    private final String displayName = "displayName";

    @Test
    @DisplayName("createExtentTestFrom should get ExtentReports from the provided context and create a test")
    public void createExtentTestFrom() {
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(ExtentReportsResolver.EXTENT_REPORTS, ExtentReports.class)).thenReturn(extentReports);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
        when(extentReports.createTest(String.format("<div>%s</div>%s", className, displayName))).thenReturn(extentTest);

        assertEquals(extentTest, ExtentTestResolver.createExtentTestFrom(extensionContext));
    }

    @DisplayName("getColorOf should return the color corresponding to the provided status")
    @ParameterizedTest()
    @MethodSource("valuesProvider")
    public void getColorOf(final Status status, final ExtentColor expected) {
        assertEquals(expected, ExtentTestResolver.getColorOf(status));
    }

    @Test
    @DisplayName("resolveParameter should return the initialized ExtentTest")
    public void testResolveParameter() {
        // createExtentTestFrom
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(ExtentReportsResolver.EXTENT_REPORTS, ExtentReports.class)).thenReturn(extentReports);
        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(extensionContext.getDisplayName()).thenReturn(displayName);
        when(extensionContext.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(className);
        when(extentReports.createTest(String.format("<div>%s</div>%s", className, displayName))).thenReturn(extentTest);

        when(extentTest.info(any(Markup.class))).thenReturn(extentTest);

        ExtentTest actual = extentTestResolver.resolveParameter(parameterContext, extensionContext);

        ArgumentCaptor<Markup> markupArgumentCaptor = ArgumentCaptor.forClass(Markup.class);
        verify(extentTest).info(markupArgumentCaptor.capture());
        Markup markup = markupArgumentCaptor.getValue();
        assertEquals(createLabel("START TEST", GREEN).getMarkup(), markup.getMarkup());

        verify(store).put(ExtentTestResolver.EXTENT_TEST, actual);
        assertEquals(extentTest, actual);
    }

    public static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(FAIL, RED),
                arguments(SKIP, AMBER),
                arguments(INFO, GREEN)
        );
    }
}
