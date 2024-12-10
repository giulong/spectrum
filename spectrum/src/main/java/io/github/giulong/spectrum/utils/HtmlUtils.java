package io.github.giulong.spectrum.utils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class HtmlUtils {

    private static final HtmlUtils INSTANCE = new HtmlUtils();
    private static final Pattern VIDEO_SRC = Pattern.compile("<video.*?src=\"(?<src>[^\"]*)\"");
    private static final Pattern IMAGE_TAG = Pattern.compile("<div class=\"row mb-3\">\\s*<div class=\"col-md-3\">\\s*<img.*?src=\"(?<src>[^\"]*)\".*?</div>\\s*</div>", DOTALL);

    public static HtmlUtils getInstance() {
        return INSTANCE;
    }

    public String inline(final String html) {
        return inlineVideosOf(inlineImagesOf(html));
    }

    @SneakyThrows
    String inlineImagesOf(final String html) {
        final Matcher matcher = IMAGE_TAG.matcher(html);
        String inlineHtml = html;

        while (matcher.find()) {
            final String src = matcher.group("src");
            final byte[] bytes = Files.readAllBytes(Path.of(src));
            final String encoded = new String(Base64.getEncoder().encode(bytes));
            final String replacement = "<div class=\"row mb-3\"><div class=\"col-md-3\">" +
                    "<a href=\"data:image/png;base64," + encoded + "\" data-featherlight=\"image\"><img class=\"inline\" src=\"data:image/png;base64," + encoded + "\"/></a>" +
                    "</div></div>";

            log.debug("Found img with src {}", src);
            inlineHtml = inlineHtml.replace(matcher.group(0), replacement);
        }

        return inlineHtml;
    }

    @SneakyThrows
    String inlineVideosOf(final String html) {
        final Matcher matcher = VIDEO_SRC.matcher(html);
        String inlineHtml = html;

        while (matcher.find()) {
            final String src = matcher.group("src");
            final byte[] bytes = Files.readAllBytes(Path.of(src));
            final String encoded = new String(Base64.getEncoder().encode(bytes));
            final String replacement = String.format("data:video/mp4;base64,%s", encoded);

            log.debug("Found video with src {}", src);
            inlineHtml = inlineHtml.replace(src, replacement);
        }

        return inlineHtml;
    }
}
