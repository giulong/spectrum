package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.types.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HtmlUtilsTest {

    private MockedStatic<Path> pathMockedStatic;
    private MockedStatic<Files> filesMockedStatic;

    private final String testId = "testId";

    @Mock
    private ContextManager contextManager;

    @Mock
    private Map<Path, byte[]> screenshots;

    @Mock
    private Path path;

    @Mock
    private Path path2;

    @Mock
    private TestData testData;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private FileUtils fileUtils;

    @Captor
    private ArgumentCaptor<Map<String, Object>> freeMarkerVarsArgumentCaptor;

    @InjectMocks
    private HtmlUtils htmlUtils;

    @BeforeEach
    void beforeEach() {
        pathMockedStatic = mockStatic(Path.class);
        filesMockedStatic = mockStatic(Files.class);

        Reflections.setField("contextManager", htmlUtils, contextManager);
        Reflections.setField("freeMarkerWrapper", htmlUtils, freeMarkerWrapper);
        Reflections.setField("fileUtils", htmlUtils, fileUtils);
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
        final String videoTemplate = "videoHtml";
        final String divTemplate = "divTemplateHtml";
        final String divFrameTemplate = "divFrameTemplateHtml";
        final String divImageTemplate = "divImageHtml";

        when(fileUtils.readTemplate("video.html")).thenReturn(videoTemplate);
        when(fileUtils.readTemplate("div-template.html")).thenReturn(divTemplate);
        when(fileUtils.readTemplate("div-frame-template.html")).thenReturn(divFrameTemplate);
        when(fileUtils.readTemplate("div-image-template.html")).thenReturn(divImageTemplate);

        htmlUtils.sessionOpened();

        assertEquals(videoTemplate, Reflections.getFieldValue("videoTemplate", htmlUtils));
        assertEquals(divTemplate, Reflections.getFieldValue("divTemplate", htmlUtils));
        assertEquals(divFrameTemplate, Reflections.getFieldValue("frameTemplate", htmlUtils));
        assertEquals(divImageTemplate, Reflections.getFieldValue("inlineImageTemplate", htmlUtils));
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
        when(testData.getTestId()).thenReturn(testId);
        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("classes", "classes", "id", testId, "number", 123, "content", "content");

        Reflections.setField("frameTemplate", htmlUtils, source);
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);

        final String result = htmlUtils.buildFrameTagFor(123, "content", testData, "classes");

        verify(freeMarkerWrapper).interpolate(eq(source), freeMarkerVarsArgumentCaptor.capture());
        assertEquals(interpolatedTemplate, result);
        assertEquals(expectedParams, freeMarkerVarsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("inline should call both the inlineImagesOf and inlineVideosOf on the provided html and return the one with all of those replaced")
    void inline() throws IOException {
        final String report = "abc<video src=\"src1\"/>def<div class=\"row mb-3\"><div class=\"col-md-3\"><img class=\"inline\" src=\"src2\"/></div></div>def";

        when(Path.of("src1")).thenReturn(path);
        when(Path.of("src2")).thenReturn(path2);
        when(Files.readAllBytes(path)).thenReturn(new byte[]{1, 2, 3});
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("encoded", "BAUG");

        Reflections.setField("inlineImageTemplate", htmlUtils, source);
        final String interpolatedTemplate = "interpolatedTemplate";
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);

        when(contextManager.getScreenshots()).thenReturn(screenshots);
        when(screenshots.get(path2)).thenReturn(new byte[]{4, 5, 6});

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

        when(Path.of("src1")).thenReturn(path);
        when(Path.of("src2")).thenReturn(path2);
        final String interpolatedTemplate = "interpolatedTemplate";
        final String source = "source";
        final Map<String, Object> expectedParams = Map.of("encoded", "AQID");
        final Map<String, Object> expectedParams1 = Map.of("encoded", "BAUG");

        Reflections.setField("inlineImageTemplate", htmlUtils, source);
        when(freeMarkerWrapper.interpolate(source, expectedParams)).thenReturn(interpolatedTemplate);
        when(freeMarkerWrapper.interpolate(source, expectedParams1)).thenReturn(interpolatedTemplate);
        when(contextManager.getScreenshots()).thenReturn(screenshots);
        when(screenshots.get(path)).thenReturn(new byte[]{1, 2, 3});
        when(screenshots.get(path2)).thenReturn(new byte[]{4, 5, 6});

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
