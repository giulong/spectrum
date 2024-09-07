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
    public void beforeEach() throws IOException {
        Reflections.setField("fileUtils", testDataResolver, fileUtils);
        Reflections.setField("contextManager", testDataResolver, contextManager);

        testDataMockedStatic = mockStatic(TestData.class);
    }

    @AfterEach
    public void afterEach() {
        testDataMockedStatic.close();
    }

    @Test
    @DisplayName("resolveParameter should return an instance of testData")
    public void resolveParameter() throws NoSuchMethodException {
        final Class<String> clazz = String.class;
        final String className = clazz.getSimpleName();
        final String classDisplayName = "classDisplayName";
        final String sanitizedClassDisplayName = "sanitizedClassDisplayName";
        final String methodName = "resolveParameter";
        final String displayName = "displayName";
        final String sanitizedDisplayName = "sanitizedDisplayName";
        final String testId = "string-sanitizeddisplayname";
        final String fileName = "fileName";
        final String fileNameWithoutExtension = "fileNameWithoutExtension";

        when(fileUtils.removeExtensionFrom(fileName)).thenReturn(fileNameWithoutExtension);
        when(fileUtils.sanitize(classDisplayName)).thenReturn(sanitizedClassDisplayName);
        when(fileUtils.sanitize(displayName)).thenReturn(sanitizedDisplayName);

        // getScreenshotFolderPathForCurrentTest
        when(fileUtils.deleteContentOf(Path.of(REPORTS_FOLDER, fileNameWithoutExtension, "screenshots", sanitizedClassDisplayName, sanitizedDisplayName).toAbsolutePath())).thenReturn(path);

        // getVideoPathForCurrentTest
        when(fileUtils.deleteContentOf(Path.of(REPORTS_FOLDER, fileNameWithoutExtension, "videos", sanitizedClassDisplayName, sanitizedDisplayName).toAbsolutePath())).thenReturn(path);

        when(context.getStore(GLOBAL)).thenReturn(store);
        when(context.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(REPORTS_FOLDER);
        when(extent.getFileName()).thenReturn(fileName);
        doReturn(String.class).when(context).getRequiredTestClass();
        when(context.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        when(TestData.builder()).thenReturn(testDataBuilder);
        when(testDataBuilder.className(className)).thenReturn(testDataBuilder);
        when(context.getDisplayName()).thenReturn(displayName);
        when(context.getParent()).thenReturn(Optional.of(parentContext));
        when(parentContext.getDisplayName()).thenReturn(classDisplayName);
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
    public void getScreenshotFolderPathForCurrentTest() {
        final String extentFileName = "extentFileName";

        when(fileUtils.deleteContentOf(Path.of(REPORTS_FOLDER, extentFileName, "screenshots", CLASS_NAME, METHOD_NAME).toAbsolutePath())).thenReturn(path);
        assertEquals(path, testDataResolver.getScreenshotFolderPathForCurrentTest(REPORTS_FOLDER, extentFileName, CLASS_NAME, METHOD_NAME));
    }

    @Test
    @DisplayName("getVideoPathForCurrentTest should return the path for the current test and create the directories")
    public void getVideoPathForCurrentTest() {
        final String extentFileName = "extentFileName";

        when(fileUtils.deleteContentOf(Path.of(REPORTS_FOLDER, extentFileName, "videos", CLASS_NAME, METHOD_NAME).toAbsolutePath())).thenReturn(path);
        when(path.resolve(stringArgumentCaptor.capture())).thenReturn(path);

        assertEquals(path, testDataResolver.getVideoPathForCurrentTest(false, REPORTS_FOLDER, extentFileName, CLASS_NAME, METHOD_NAME));
        assertThat(stringArgumentCaptor.getValue(), matchesPattern(UUID_REGEX));
    }

    @Test
    @DisplayName("getVideoPathForCurrentTest should return null if video is disabled")
    public void getVideoPathForCurrentTestDisabled() {
        assertNull(testDataResolver.getVideoPathForCurrentTest(true, REPORTS_FOLDER, "extentFileName", CLASS_NAME, METHOD_NAME));
    }

    @Test
    @DisplayName("transformInKebabCase should return the provided string with spaces replaced by dashes and in lowercase")
    public void transformInKebabCase() {
        assertEquals("some-composite-string", TestDataResolver.transformInKebabCase("Some Composite STRING"));
    }
}
