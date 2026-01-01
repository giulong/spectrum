package io.github.giulong.spectrum.extensions.resolvers;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.utils.Configuration;
import io.github.giulong.spectrum.utils.ContextManager;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.TestData;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class TestDataResolverTest {

    private MockedStatic<TestData> testDataMockedStatic;
    private MockedStatic<TestData.VisualRegression> visualRegressionMockedStatic;
    private MockedStatic<Path> pathMockedStatic;

    private final String visualRegressionFolder = "visualRegressionFolder";

    @Mock
    private Path path;

    @Mock
    private Path visualRegressionFolderPath;

    @Mock
    private Configuration.VisualRegression.Snapshots snapshots;

    @MockFinal
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @MockFinal
    @SuppressWarnings("unused")
    private ContextManager contextManager;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext context;

    @Mock
    private ExtensionContext parentContext;

    @Mock
    private ExtensionContext grandParentContext;

    @Mock
    private ExtensionContext rootContext;

    @Mock
    private ExtensionContext.Store store;

    @Mock
    private ExtensionContext.Store rootStore;

    @Mock
    private TestData.TestDataBuilder testDataBuilder;

    @Mock
    private TestData testData;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.VisualRegression visualRegressionConfiguration;

    @Mock
    private TestData.VisualRegression.VisualRegressionBuilder visualRegressionBuilder;

    @Mock
    private TestData.VisualRegression visualRegression;

    @InjectMocks
    private TestDataResolver testDataResolver;

    @BeforeEach
    void beforeEach() {
        testDataMockedStatic = mockStatic();
        visualRegressionMockedStatic = mockStatic();
        pathMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        testDataMockedStatic.close();
        visualRegressionMockedStatic.close();
        pathMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of testData")
    void resolveParameter() throws NoSuchMethodException {
        final Class<String> clazz = String.class;
        final String className = clazz.getSimpleName();
        final String classDisplayName = "String";
        final String sanitizedClassDisplayName = "sanitizedClassDisplayName";
        final String methodName = "resolveParameter";
        final String displayName = "displayName";
        final String sanitizedDisplayName = "sanitizedDisplayName";
        final String testId = "string-sanitizeddisplayname";

        // joinTestDisplayNamesIn
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getParent()).thenReturn(Optional.of(grandParentContext));
        when(context.getDisplayName()).thenReturn(displayName);

        when(fileUtils.sanitize(classDisplayName)).thenReturn(sanitizedClassDisplayName);
        when(fileUtils.sanitize(displayName)).thenReturn(sanitizedDisplayName);
        when(fileUtils.createTempFile("video", ".mp4")).thenReturn(path);

        when(context.getStore(GLOBAL)).thenReturn(store);
        doReturn(String.class).when(context).getRequiredTestClass();
        when(context.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));

        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getVisualRegression()).thenReturn(visualRegressionConfiguration);
        when(visualRegressionConfiguration.getSnapshots()).thenReturn(snapshots);
        when(snapshots.getFolder()).thenReturn(visualRegressionFolder);
        when(Path.of(visualRegressionFolder)).thenReturn(visualRegressionFolderPath);

        // getVisualRegressionPathFrom
        when(visualRegressionFolderPath.resolve(sanitizedClassDisplayName)).thenReturn(visualRegressionFolderPath);
        when(visualRegressionFolderPath.resolve(sanitizedDisplayName)).thenReturn(visualRegressionFolderPath);
        when(visualRegressionFolderPath.toAbsolutePath()).thenReturn(visualRegressionFolderPath);

        when(TestData.VisualRegression.builder()).thenReturn(visualRegressionBuilder);
        when(visualRegressionBuilder.path(visualRegressionFolderPath)).thenReturn(visualRegressionBuilder);
        when(visualRegressionBuilder.build()).thenReturn(visualRegression);

        when(TestData.builder()).thenReturn(testDataBuilder);
        when(testDataBuilder.className(className)).thenReturn(testDataBuilder);
        when(testDataBuilder.methodName(methodName)).thenReturn(testDataBuilder);
        when(testDataBuilder.classDisplayName(sanitizedClassDisplayName)).thenReturn(testDataBuilder);
        when(testDataBuilder.displayName(sanitizedDisplayName)).thenReturn(testDataBuilder);
        when(testDataBuilder.testId(testId)).thenReturn(testDataBuilder);
        when(testDataBuilder.videoPath(path)).thenReturn(testDataBuilder);
        when(testDataBuilder.visualRegression(visualRegression)).thenReturn(testDataBuilder);
        when(testDataBuilder.build()).thenReturn(testData);

        final TestData actual = testDataResolver.resolveParameter(parameterContext, context);

        assertEquals(testData, actual);
        verify(store).put(TEST_DATA, actual);
        verify(contextManager).put(context, TEST_DATA, actual);
    }

    @Test
    @DisplayName("getDisplayNameOf should return the @DisplayName value")
    void getDisplayNameOf() {
        final Class<?> clazz = DummyDisplayName.class;

        assertEquals("dummy", TestDataResolver.getDisplayNameOf(clazz));
    }

    @Test
    @DisplayName("getDisplayNameOf should return the class simple name if it's not annotated with @DisplayName")
    void getDisplayNameOfNoAnnotation() {
        final Class<?> clazz = String.class;

        assertEquals("String", TestDataResolver.getDisplayNameOf(clazz));
    }

    @Test
    @DisplayName("joinTestDisplayNamesIn should join all the display names from the provided context, with all the intermediate containers, excluding the class one")
    void joinTestDisplayNamesIn() {
        final String displayName = "displayName";
        final String parentDisplayName = "parentDisplayName";
        final String expected = String.join(" ", List.of(parentDisplayName, displayName));

        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getParent()).thenReturn(Optional.of(grandParentContext));
        when(grandParentContext.getParent()).thenReturn(Optional.of(rootContext));

        when(context.getDisplayName()).thenReturn(displayName);
        when(parentContext.getDisplayName()).thenReturn(parentDisplayName);

        assertEquals(expected, TestDataResolver.joinTestDisplayNamesIn(context));

        verify(grandParentContext, never()).getDisplayName();
        verify(rootContext, never()).getDisplayName();
    }

    @Test
    @DisplayName("transformInKebabCase should return the provided string with spaces replaced by dashes and in lowercase")
    void transformInKebabCase() {
        assertEquals("some-composite-string", TestDataResolver.transformInKebabCase("Some Composite STRING"));
    }

    @Test
    @DisplayName("getVisualRegressionScreenshotPathFrom should return the absolute path for visual regression screenshots for the current test")
    void getVisualRegressionScreenshotPathFrom() {
        final String className = "className";
        final String methodName = "methodName";

        when(Path.of(visualRegressionFolder)).thenReturn(visualRegressionFolderPath);
        when(visualRegressionFolderPath.resolve(className)).thenReturn(visualRegressionFolderPath);
        when(visualRegressionFolderPath.resolve(methodName)).thenReturn(visualRegressionFolderPath);
        when(visualRegressionFolderPath.toAbsolutePath()).thenReturn(visualRegressionFolderPath);

        assertEquals(visualRegressionFolderPath, testDataResolver.getVisualRegressionPathFrom(visualRegressionFolder, className, methodName));
    }

    @DisplayName("dummy")
    private static final class DummyDisplayName {
    }
}
