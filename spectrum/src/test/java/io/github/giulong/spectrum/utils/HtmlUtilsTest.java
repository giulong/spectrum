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
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class HtmlUtilsTest {

    private static MockedStatic<Path> pathMockedStatic;
    private static MockedStatic<Files> filesMockedStatic;

    private final String testId = "testId";

    @Mock
    private Path path;

    @Mock
    private Path path2;

    @Mock
    private TestData testData;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Captor
    private ArgumentCaptor<Map<String, Object>> freeMarkerVarsArgumentCaptor;

    @InjectMocks
    private HtmlUtils htmlUtils;

    @BeforeEach
    void beforeEach() {
        pathMockedStatic = mockStatic(Path.class);
        filesMockedStatic = mockStatic(Files.class);

        Reflections.setField("freeMarkerWrapper", htmlUtils, freeMarkerWrapper);
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
    @DisplayName("buildFrameTagFor should return the tag with the provided frame number and content")
    void buildFrameTagForOverloaded() {
        when(testData.getTestId()).thenReturn(testId);

        assertEquals("<div class=\"\" data-test-id=\"testId\" data-frame=\"123\">content</div>", htmlUtils.buildFrameTagFor(123, "content", testData));
    }

    @Test
    @DisplayName("buildFrameTagFor should return the tag with the provided frame number, content, and css classes provided")
    void buildFrameTagFor() {
        when(testData.getTestId()).thenReturn(testId);

        assertEquals("<div class=\"classes\" data-test-id=\"testId\" data-frame=\"123\">content</div>", htmlUtils.buildFrameTagFor(123, "content", testData, "classes"));
    }

    @Test
    @DisplayName("inline should call both the inlineImagesOf and inlineVideosOf on the provided html and return the one with all of those replaced")
    void inline() throws IOException {
        final String report = "abc<video src=\"src1\"/>def<div class=\"row mb-3\"><div class=\"col-md-3\"><img class=\"inline\" src=\"src2\"/></div></div>def";
        when(Path.of("src1")).thenReturn(path);
        when(Path.of("src2")).thenReturn(path2);
        when(Files.readAllBytes(path)).thenReturn(new byte[]{1, 2, 3});
        when(Files.readAllBytes(path2)).thenReturn(new byte[]{4, 5, 6});

        final String actual = htmlUtils.inline(report);

        assertEquals("abc<video src=\"data:video/mp4;base64,AQID\"/>def<div class=\"row mb-3\"><div class=\"col-md-3\"><a href=\"data:image/png;base64,BAUG\" data-featherlight=\"image\"><img class=\"inline\" src=\"data:image/png;base64,BAUG\"/></a></div></div>def", actual);
    }

    @Test
    @DisplayName("inlineImagesOf should return the provided html with all the images replaced with their own base64")
    void inlineImagesOf() throws IOException {
        final String html = "abc<div class=\"row mb-3\"><div class=\"col-md-3\"><img class=\"inline\" src=\"src1\"/></div></div>def" +
                "<div class=\"row mb-3\"><div class=\"col-md-3\"><img class=\"inline\" src=\"src2\"/></div></div>ghi";

        when(Path.of("src1")).thenReturn(path);
        when(Path.of("src2")).thenReturn(path2);
        when(Files.readAllBytes(path)).thenReturn(new byte[]{1, 2, 3});
        when(Files.readAllBytes(path2)).thenReturn(new byte[]{4, 5, 6});

        final String actual = htmlUtils.inlineImagesOf(html);

        assertThat(actual, matchesPattern(".*<div class=\"row mb-3\"><div class=\"col-md-3\"><a href=\"data:image/png;base64,AQID\" data-featherlight=\"image\"><img class=\"inline\" src=\"data:image/png;base64,AQID\"/></a></div></div>.*" +
                "<div class=\"row mb-3\"><div class=\"col-md-3\"><a href=\"data:image/png;base64,BAUG\" data-featherlight=\"image\"><img class=\"inline\" src=\"data:image/png;base64,BAUG\"/></a></div></div>.*"));
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

        when(mockPath.toString()).thenReturn(mockPathString);
        when(freeMarkerWrapper.interpolateTemplate(eq("video.html"), freeMarkerVarsArgumentCaptor.capture())).thenReturn(interpolatedTemplate);

        final String result = htmlUtils.generateVideoTag(videoId, width, height, mockPath);

        assertEquals(interpolatedTemplate, result);
        assertEquals(Map.of("videoId", videoId, "width", width, "height", height, "src", mockPathString), freeMarkerVarsArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("generateTestInfoDivs should return two divs with correct IDs and content")
    void testGenerateTestInfoDivs() {
        final String id = "test1";
        final String classDisplayName = "MyTestClass";
        final String testDisplayName = "shouldDoSomething";
        final String interpolatedTemplate = "interpolatedTemplate";

        when(freeMarkerWrapper.interpolateTemplate(eq("div-template.html"), freeMarkerVarsArgumentCaptor.capture())).thenReturn(interpolatedTemplate);

        final String result = htmlUtils.generateTestInfoDivs(id, classDisplayName, testDisplayName);

        assertEquals(interpolatedTemplate, result);
        assertEquals(Map.of("id", id, "classDisplayName", classDisplayName, "testDisplayName", testDisplayName), freeMarkerVarsArgumentCaptor.getValue());
    }
}
