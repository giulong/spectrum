package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.types.TestData;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class HtmlUtils implements SessionHook {

    private static final HtmlUtils INSTANCE = new HtmlUtils();
    private static final Pattern VIDEO_SRC = Pattern.compile("<video.*?src=\"(?<src>[^\"]*)\"");
    private static final Pattern IMAGE_TAG = Pattern.compile("<div class=\"row mb-3\">\\s*<div class=\"col-md-3\">\\s*<img.*?src=\"(?<src>[^\"]*)\".*?</div>\\s*</div>", DOTALL);
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final String SRC = "src";

    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();
    private final ContextManager contextManager = ContextManager.getInstance();
    private final FileUtils fileUtils = FileUtils.getInstance();

    private String videoTemplate;
    private String divTemplate;
    private String frameTemplate;
    private String inlineImageTemplate;

    @Override
    public void sessionOpened() {
        this.videoTemplate = fileUtils.readTemplate("video.html");
        this.divTemplate = fileUtils.readTemplate("div-template.html");
        this.inlineImageTemplate = fileUtils.readTemplate("div-image-template.html");
        this.frameTemplate = fileUtils.readTemplate("div-frame-template.html");
    }

    public static HtmlUtils getInstance() {
        return INSTANCE;
    }

    public String inline(final String html) {
        return inlineVideosOf(inlineImagesOf(html));
    }

    public String buildFrameTagFor(final int number, final String content, final TestData testData) {
        return buildFrameTagFor(number, content, testData, "");
    }

    public String buildFrameTagFor(final int number, final String content, final TestData testData, final String classes) {
        return freeMarkerWrapper.interpolate(this.frameTemplate, Map.of("classes", classes, "id", testData.getTestId(), "number", number, "content", content));
    }

    @SneakyThrows
    public String inlineImagesOf(final String html) {
        final Matcher matcher = IMAGE_TAG.matcher(html);
        final Map<Path, byte[]> screenshots = contextManager.getScreenshots();
        String inlineHtml = html;

        while (matcher.find()) {
            final String src = matcher.group(SRC);
            final byte[] bytes = screenshots.get(Path.of(src));
            final String encoded = ENCODER.encodeToString(bytes);
            final String replacement = freeMarkerWrapper.interpolate(this.inlineImageTemplate, Map.of("encoded", encoded));

            log.debug("Found img with src {}", src);
            inlineHtml = inlineHtml.replace(matcher.group(0), replacement);
        }

        return inlineHtml;
    }

    @SneakyThrows
    public String inlineVideosOf(final String html) {
        final Matcher matcher = VIDEO_SRC.matcher(html);
        String inlineHtml = html;

        while (matcher.find()) {
            final String src = matcher.group(SRC);
            final byte[] bytes = Files.readAllBytes(Path.of(src));
            final String encoded = ENCODER.encodeToString(bytes);
            final String replacement = String.format("data:video/mp4;base64,%s", encoded);

            log.debug("Found video with src {}", src);
            inlineHtml = inlineHtml.replace(src, replacement);
        }

        return inlineHtml;
    }

    public String generateVideoTag(final String videoId, final String width, final String height, final Path src) {
        return freeMarkerWrapper.interpolate(this.videoTemplate, Map.of("videoId", videoId, "width", width, "height", height, SRC, src.toString()));
    }

    public String generateTestInfoDivs(final String id, final String classDisplayName, final String testDisplayName) {
        return freeMarkerWrapper.interpolate(this.divTemplate, Map.of("id", id, "classDisplayName", classDisplayName, "testDisplayName", testDisplayName));
    }
}
