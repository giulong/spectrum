package io.github.giulong.spectrum.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import io.github.giulong.spectrum.MockFinal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class HtmlUtilsTest {

    private MockedStatic<Path> pathMockedStatic;
    private MockedStatic<Files> filesMockedStatic;

    private final String testId = "testId";

    @MockFinal
    @SuppressWarnings("unused")
    private ContextManager contextManager;

    @Mock
    private Map<String, byte[]> screenshots;

    @Mock
    private Path path;

    @Mock
    private Path path2;

    @Mock
    private TestData testData;

    @MockFinal
    @SuppressWarnings("unused")
    private FreeMarkerWrapper freeMarkerWrapper;

    @MockFinal
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @Captor
    private ArgumentCaptor<Map<String, Object>> freeMarkerVarsArgumentCaptor;

    @InjectMocks
    private HtmlUtils htmlUtils;

    @BeforeEach
    void beforeEach() {
        pathMockedStatic = mockStatic(Path.class);
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    void afterEach() {
        pathMockedStatic.close();
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    void getInstance() {
        //noinspection EqualsWithItself
        assertSame(HtmlUtils.getInstance(), HtmlUtils.getInstance());
    }

    @Test
    @DisplayName("sessionOpened should init the html utils")
    void sessionOpened() {
        final String videoTemplate = "videoTemplate";
        final String divTemplate = "divTemplate";
        final String frameTemplate = "frameTemplate";
        final String imageTemplate = "imageTemplate";
        final String visualRegressionTemplate = "visualRegressionTemplate";

        when(fileUtils.readTemplate("video.html")).thenReturn(videoTemplate);
        when(fileUtils.readTemplate("div-template.html")).thenReturn(divTemplate);
        when(fileUtils.readTemplate("frame-template.html")).thenReturn(frameTemplate);
        when(fileUtils.readTemplate("image-template.html")).thenReturn(imageTemplate);
        when(fileUtils.readTemplate("visual-regression-template.html")).thenReturn(visualRegressionTemplate);

        htmlUtils.sessionOpened();

        assertEquals(videoTemplate, Reflections.getFieldValue("videoTemplate", htmlUtils));
        assertEquals(divTemplate, Reflections.getFieldValue("divTemplate", htmlUtils));
        assertEquals(frameTemplate, Reflections.getFieldValue("frameTemplate", htmlUtils));
        assertEquals(imageTemplate, Reflections.getFieldValue("imageTemplate", htmlUtils));
        assertEquals(visualRegressionTemplate, Reflections.getFieldValue("visualRegressionTemplate", htmlUtils));

        verifyNoMoreInteractions(fileUtils);
    }

    @Test
    @DisplayName("buildFrameTagFor should return the tag with the provided frame number and content")
    void buildFrameTagForOverloaded() {
        when(testData.getTestId()).thenReturn(testId);
        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("classes", "", "id", testId, "number", 123, "content", "content");

        Reflections.setField("frameTemplate", htmlUtils, source);
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);

        final String result = htmlUtils.buildFrameTagFor(123, "content", testData);

        verify(freeMarkerWrapper).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        assertEquals(interpolatedTemplate, result);
        assertEquals(expectedParams, freeMarkerVarsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("buildFrameTagFor should return the tag with the provided frame number, content, and css classes provided")
    void buildFrameTagFor() {
        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("classes", "classes", "id", testId, "number", 123, "content", "content");

        Reflections.setField("frameTemplate", htmlUtils, source);

        when(testData.getTestId()).thenReturn(testId);
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);

        final String result = htmlUtils.buildFrameTagFor(123, "content", testData, "classes");

        verify(freeMarkerWrapper).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        assertEquals(interpolatedTemplate, result);
        assertEquals(expectedParams, freeMarkerVarsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("buildVisualRegressionTagFor should return the tag with the provided frame number, test id, reference, regression, and no diff")
    void buildVisualRegressionTagForNull() {
        when(testData.getTestId()).thenReturn(testId);
        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("id", testId, "number", 123, "reference", "AQID", "regression", "BAUG");

        Reflections.setField("visualRegressionTemplate", htmlUtils, source);
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);

        final String result = htmlUtils.buildVisualRegressionTagFor(123, testData, new byte[]{1, 2, 3}, new byte[]{4, 5, 6});

        verify(freeMarkerWrapper).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        assertEquals(interpolatedTemplate, result);
        assertEquals(expectedParams, freeMarkerVarsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("buildVisualRegressionTagFor should return the tag with the provided frame number, test id, reference, regression, and diff")
    void buildVisualRegressionTagFor() {
        when(testData.getTestId()).thenReturn(testId);
        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("id", testId, "number", 123, "reference", "AQID", "regression", "BAUG", "diff", "Bw==");

        Reflections.setField("visualRegressionTemplate", htmlUtils, source);
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);

        final String result = htmlUtils.buildVisualRegressionTagFor(123, testData, new byte[]{1, 2, 3}, new byte[]{4, 5, 6}, new byte[]{7});

        verify(freeMarkerWrapper).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        assertEquals(interpolatedTemplate, result);
        assertEquals(expectedParams, freeMarkerVarsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("inline should call both the inlineImagesOf and inlineVideosOf on the provided html and return the one with all of those replaced")
    void inline() throws IOException {
        final String report = "abc<video src=\"src1\"/>def<div class=\"row mb-3\"><div class=\"col-md-3\"><img class=\"inline\" src=\"src2\"/></div></div>def";

        when(Path.of("src1")).thenReturn(path);
        when(Files.readAllBytes(path)).thenReturn(new byte[]{1, 2, 3});
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("encoded", "BAUG");

        Reflections.setField("imageTemplate", htmlUtils, source);
        final String interpolatedTemplate = "interpolatedTemplate";
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);

        when(contextManager.getScreenshots()).thenReturn(screenshots);
        when(screenshots.get("src2")).thenReturn(new byte[]{4, 5, 6});

        final String actual = htmlUtils.inline(report);

        verify(freeMarkerWrapper).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        assertEquals("abc<video src=\"data:video/mp4;base64,AQID\"/>definterpolatedTemplatedef", actual);
        assertEquals(expectedParams, freeMarkerVarsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("inlineImagesOf should return the provided html with all the images replaced with their own base64")
    void inlineImagesOf() {
        final String html = "abc<div class=\"row mb-3\"><div class=\"col-md-3\"><img class=\"inline\" src=\"src1\"/></div></div>def" +
                "<div class=\"row mb-3\"><div class=\"col-md-3\"><img class=\"inline\" src=\"src2\"/></div></div>ghi";

        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("encoded", "AQID");
        final Map<String, Object> expectedParams1 = Map.of("encoded", "BAUG");

        Reflections.setField("imageTemplate", htmlUtils, source);
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);
        when(freeMarkerWrapper.interpolate(source, expectedParams1)).thenReturn(interpolatedTemplate);
        when(contextManager.getScreenshots()).thenReturn(screenshots);
        when(screenshots.get("src1")).thenReturn(new byte[]{1, 2, 3});
        when(screenshots.get("src2")).thenReturn(new byte[]{4, 5, 6});

        final String actual = htmlUtils.inlineImagesOf(html);

        verify(freeMarkerWrapper, times(2)).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        final List<Map<String, Object>> capturedVars = freeMarkerVarsArgumentCaptor.getAllValues();

        final Map<String, Object> firstCallVars = capturedVars.get(0);
        final Map<String, Object> secondCallVars = capturedVars.get(1);
        assertEquals(expectedParams, firstCallVars);
        assertEquals(expectedParams1, secondCallVars);
        assertThat(actual, containsString(interpolatedTemplate));

    }

    @Test
    @DisplayName("inlineVideosOf should return the provided html with all the videos replaced with their own base64")
    void inlineVideosOf() throws IOException {
        final String html = "abc<video src=\"src1\"/>def<video src=\"src2\"/>ghi";

        when(Path.of("src1")).thenReturn(path);
        when(Path.of("src2")).thenReturn(path2);
        when(Files.readAllBytes(path)).thenReturn(new byte[]{1, 2, 3});
        when(Files.readAllBytes(path2)).thenReturn(new byte[]{4, 5, 6});

        final String actual = htmlUtils.inlineVideosOf(html);

        assertThat(actual, matchesPattern(".*<video.*src=\"data:video/mp4;base64,AQID\"/>.*<video.*src=\"data:video/mp4;base64,BAUG\"/>.*"));
    }

    @Test
    @DisplayName("generateVideoTag should return a correctly formatted video HTML tag with given attributes")
    void generateVideoTag() {
        final String videoId = "abc123";
        final String width = "640";
        final String height = "360";
        final String mockPathString = "/videos/test.mp4";
        final Path mockPath = Mockito.mock(Path.class);
        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("videoId", videoId, "width", width, "height", height, "src", mockPathString);
        Reflections.setField("videoTemplate", htmlUtils, source);
        when(mockPath.toString()).thenReturn(mockPathString);

        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);
        final String result = htmlUtils.generateVideoTag(videoId, width, height, mockPath);

        verify(freeMarkerWrapper).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        assertEquals(interpolatedTemplate, result);
        assertEquals(expectedParams, freeMarkerVarsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("generateTestInfoDivs should return two divs with correct IDs and content")
    void testGenerateTestInfoDivs() {
        final String id = "test1";
        final String classDisplayName = "MyTestClass";
        final String testDisplayName = "shouldDoSomething";
        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("id", id, "classDisplayName", classDisplayName, "testDisplayName", testDisplayName);
        Reflections.setField("divTemplate", htmlUtils, source);
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);

        final String result = htmlUtils.generateTestInfoDivs(id, classDisplayName, testDisplayName);

        verify(freeMarkerWrapper).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        assertEquals(interpolatedTemplate, result);
        assertEquals(expectedParams, freeMarkerVarsArgumentCaptor.getValue());
    }
}
