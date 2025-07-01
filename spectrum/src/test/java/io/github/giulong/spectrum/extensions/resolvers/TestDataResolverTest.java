package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

class TestDataResolverTest {

    private static final String UUID_REGEX = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.mp4";
    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";
    private static final String REPORTS_FOLDER = "reportsFolder";
    private static final String SCREENSHOTS_FOLDER = "screenshotsFolder";

    private static MockedStatic<TestData> testDataMockedStatic;

    @Mock
    private Path path;

    @Mock
    private FileUtils fileUtils;

    @Mock
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
    private Configuration configuration;

    @Mock
    private Configuration.Extent extent;

    @Mock
    private TestData.TestDataBuilder testDataBuilder;

    @Mock
    private Configuration.Application application;

    @Mock
    private TestData testData;

    @Mock
    private Video video;

    @Captor
    private ArgumentCaptor<Path> pathArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @InjectMocks
    private TestDataResolver testDataResolver;

    @BeforeEach
    void beforeEach() throws IOException {
        Reflections.setField("fileUtils", testDataResolver, fileUtils);
        Reflections.setField("contextManager", testDataResolver, contextManager);

        testDataMockedStatic = mockStatic(TestData.class);
    }

    @AfterEach
    void afterEach() {
        testDataMockedStatic.close();
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
        final String fileName = "fileName";
        final String fileNameWithoutExtension = "fileNameWithoutExtension";

        // joinTestDisplayNamesIn
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getParent()).thenReturn(Optional.of(rootContext));
        when(context.getDisplayName()).thenReturn(displayName);

        when(fileUtils.removeExtensionFrom(fileName)).thenReturn(fileNameWithoutExtension);
        when(fileUtils.sanitize(classDisplayName)).thenReturn(sanitizedClassDisplayName);
        when(fileUtils.sanitize(displayName)).thenReturn(sanitizedDisplayName);

        // getScreenshotFolderPathForCurrentTest
        when(fileUtils.deleteContentOf(
                Path.of(REPORTS_FOLDER, fileNameWithoutExtension, "screenshots", sanitizedClassDisplayName, sanitizedDisplayName).toAbsolutePath()))
                .thenReturn(path);

        // getVideoPathForCurrentTest
        when(fileUtils.deleteContentOf(
                Path.of(REPORTS_FOLDER, fileNameWithoutExtension, "videos", sanitizedClassDisplayName, sanitizedDisplayName).toAbsolutePath()))
                .thenReturn(path);

        when(context.getStore(GLOBAL)).thenReturn(store);
        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getApplication()).thenReturn(application);
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(REPORTS_FOLDER);
        when(extent.getFileName()).thenReturn(fileName);
        doReturn(String.class).when(context).getRequiredTestClass();
        when(context.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        when(TestData.builder()).thenReturn(testDataBuilder);
        when(testDataBuilder.className(className)).thenReturn(testDataBuilder);
        when(testDataBuilder.methodName(methodName)).thenReturn(testDataBuilder);
        when(testDataBuilder.classDisplayName(sanitizedClassDisplayName)).thenReturn(testDataBuilder);
        when(testDataBuilder.displayName(sanitizedDisplayName)).thenReturn(testDataBuilder);
        when(testDataBuilder.testId(testId)).thenReturn(testDataBuilder);
        when(testDataBuilder.screenshotFolderPath(path)).thenReturn(testDataBuilder);
        when(testDataBuilder.videoPath(pathArgumentCaptor.capture())).thenReturn(testDataBuilder);
        when(testDataBuilder.build()).thenReturn(testData);

        final TestData actual = testDataResolver.resolveParameter(parameterContext, context);

        assertEquals(testData, actual);
        verify(store).put(TEST_DATA, actual);
        verify(contextManager).put(context, TEST_DATA, actual);
    }

    @Test
    @DisplayName("getScreenshotFolderPathForCurrentTest should return the path for the current test and create the dirs")
    void getScreenshotFolderPathForCurrentTest() {
        final String extentFileName = "extentFileName";

        when(fileUtils.deleteContentOf(Path.of(REPORTS_FOLDER, extentFileName, "screenshots", CLASS_NAME, METHOD_NAME).toAbsolutePath())).thenReturn(path);
        assertEquals(path, testDataResolver.getScreenshotFolderPathForCurrentTest(null, REPORTS_FOLDER, extentFileName, CLASS_NAME, METHOD_NAME));
    }

    @Test
    @DisplayName("getScreenshotFolderPathForCurrentTest should return the path for the current test and create the dirs if screenshotPath is provided")
    void getScreenshotFolderPathForCurrentTestScreenshotPathProvided() {
        final String extentFileName = "extentFileName";

        when(fileUtils.deleteContentOf(Path.of(SCREENSHOTS_FOLDER, "screenshots", CLASS_NAME, METHOD_NAME).toAbsolutePath())).thenReturn(path);
        assertEquals(path, testDataResolver.getScreenshotFolderPathForCurrentTest(SCREENSHOTS_FOLDER, REPORTS_FOLDER, extentFileName, CLASS_NAME, METHOD_NAME));
    }

    @Test
    @DisplayName("getVideoPathForCurrentTest should return the path for the current test and create the directories")
    void getVideoPathForCurrentTest() {
        final String extentFileName = "extentFileName";

        when(fileUtils.deleteContentOf(Path.of(REPORTS_FOLDER, extentFileName, "videos", CLASS_NAME, METHOD_NAME).toAbsolutePath())).thenReturn(path);
        when(path.resolve(stringArgumentCaptor.capture())).thenReturn(path);

        assertEquals(path, testDataResolver.getVideoPathForCurrentTest(false, REPORTS_FOLDER, extentFileName, CLASS_NAME, METHOD_NAME));
        assertThat(stringArgumentCaptor.getValue(), matchesPattern(UUID_REGEX));
    }

    @Test
    @DisplayName("getVideoPathForCurrentTest should return null if video is disabled")
    void getVideoPathForCurrentTestDisabled() {
        assertNull(testDataResolver.getVideoPathForCurrentTest(true, REPORTS_FOLDER, "extentFileName", CLASS_NAME, METHOD_NAME));
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

    @DisplayName("dummy")
    private static final class DummyDisplayName {
    }
}
