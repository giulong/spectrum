package io.github.giulong.spectrum.extensions.resolvers;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TestDataResolver")
class TestDataResolverTest {

    private static final String UUID_REGEX = "([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.mp4";
    private static final String REPORTS_FOLDER = "reportsFolder";
    private static final String CLASS_NAME = "className";
    private static final String METHOD_NAME = "methodName";

    private static MockedStatic<TestData> testDataMockedStatic;

    @Mock
    private ParameterContext parameterContext;

    @Mock
    private ExtensionContext extensionContext;

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

    @InjectMocks
    private TestDataResolver testDataResolver;

    @BeforeEach
    public void beforeEach() throws IOException {
        testDataMockedStatic = mockStatic(TestData.class);
        final Path path = Files.createTempDirectory(REPORTS_FOLDER);
        path.toFile().deleteOnExit();
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
        final String methodName = "resolveParameter";
        final Path path = Path.of(REPORTS_FOLDER, "screenshots", className, methodName).toAbsolutePath();
        final Path videoFolderPath = Path.of(REPORTS_FOLDER, "videos", className, methodName).toAbsolutePath();

        when(extensionContext.getStore(GLOBAL)).thenReturn(store);
        when(extensionContext.getRoot()).thenReturn(rootContext);
        when(rootContext.getStore(GLOBAL)).thenReturn(rootStore);
        when(rootStore.get(CONFIGURATION, Configuration.class)).thenReturn(configuration);
        when(configuration.getExtent()).thenReturn(extent);
        when(extent.getReportFolder()).thenReturn(REPORTS_FOLDER);
        doReturn(String.class).when(extensionContext).getRequiredTestClass();
        when(extensionContext.getRequiredTestMethod()).thenReturn(getClass().getDeclaredMethod(methodName));
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        when(TestData.builder()).thenReturn(testDataBuilder);
        when(testDataBuilder.className(className)).thenReturn(testDataBuilder);
        when(testDataBuilder.methodName(methodName)).thenReturn(testDataBuilder);
        when(testDataBuilder.screenshotFolderPath(path)).thenReturn(testDataBuilder);
        when(testDataBuilder.videoPath(pathArgumentCaptor.capture())).thenReturn(testDataBuilder);
        when(testDataBuilder.build()).thenReturn(testData);

        final TestData actual = testDataResolver.resolveParameter(parameterContext, extensionContext);
        final Path videoPath = pathArgumentCaptor.getValue();

        assertTrue(Files.exists(path));
        assertEquals(videoFolderPath, videoPath.getParent());
        assertThat(videoPath.getFileName().toString(), matchesPattern(UUID_REGEX));
        assertTrue(Files.exists(videoFolderPath));
        assertEquals(testData, actual);
        verify(store).put(TEST_DATA, actual);
    }

    @Test
    @DisplayName("getScreenshotFolderPathForCurrentTest should return the path for the current test and create the dirs")
    public void getScreenshotFolderPathForCurrentTest() {
        final Path path = Path.of(REPORTS_FOLDER, "screenshots", CLASS_NAME, METHOD_NAME).toAbsolutePath();
        assertEquals(path, testDataResolver.getScreenshotFolderPathForCurrentTest(REPORTS_FOLDER, CLASS_NAME, METHOD_NAME));

        assertTrue(Files.exists(path));
    }

    @Test
    @DisplayName("getVideoPathForCurrentTest should return the path for the current test and create the directories")
    public void getVideoPathForCurrentTest() {
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(false);

        final Path path = Path.of(REPORTS_FOLDER, "videos", CLASS_NAME, METHOD_NAME).toAbsolutePath();
        final Path actual = testDataResolver.getVideoPathForCurrentTest(configuration, REPORTS_FOLDER, CLASS_NAME, METHOD_NAME);

        assertEquals(path, actual.getParent());
        assertThat(actual.getFileName().toString(), matchesPattern(UUID_REGEX));
        assertTrue(Files.exists(path));
    }

    @Test
    @DisplayName("getVideoPathForCurrentTest should return null if video is disabled")
    public void getVideoPathForCurrentTestDisabled() {
        when(configuration.getVideo()).thenReturn(video);
        when(video.isDisabled()).thenReturn(true);

        assertNull(testDataResolver.getVideoPathForCurrentTest(configuration, REPORTS_FOLDER, CLASS_NAME, METHOD_NAME));
    }
}
