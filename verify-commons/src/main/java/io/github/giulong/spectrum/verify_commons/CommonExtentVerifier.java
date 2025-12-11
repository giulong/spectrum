package io.github.giulong.spectrum.verify_commons;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.experimental.UtilityClass;

import org.openqa.selenium.WebElement;

@UtilityClass
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class CommonExtentVerifier {

    public static void assertVideoDuration(final WebElement video, final int duration) {
        assertEquals(String.valueOf(duration), video.getDomProperty("duration"), "video duration should match");
    }
}
