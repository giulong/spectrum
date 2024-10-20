package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

class ScreenshotConsumerTest {

    private static final String UUID_REGEX = AUTO_AFTER.getValue() + "-([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})\\.png";

    private static MockedStatic<Files> filesMockedStatic;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Mock
    private TestData testData;

    @Mock
    private Path screenshotFolderPath;

    @Mock
    private Path resolvedPath;

    @Mock
    private Path file;

    @Mock(extraInterfaces = TakesScreenshot.class)
    private WebDriver driver;

    @Mock
    private Video video;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<byte[]> byteArgumentCaptor;

    @InjectMocks
    private ScreenshotConsumer screenshotConsumer;

    @BeforeEach
    void beforeEach() {
        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    void afterEach() {
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("accept should record the screenshot")
    void accept() {
        when(testData.getScreenshotFolderPath()).thenReturn(screenshotFolderPath);
        when(screenshotFolderPath.resolve(stringArgumentCaptor.capture())).thenReturn(resolvedPath);
        when(resolvedPath.getFileName()).thenReturn(file);
        when(video.shouldRecord(eq("file"))).thenReturn(true);
        when(((TakesScreenshot) driver).getScreenshotAs(BYTES)).thenReturn(new byte[]{1, 2, 3});
        when(webDriverEvent.getFrame()).thenReturn(AUTO_AFTER);

        screenshotConsumer.accept(webDriverEvent);

        filesMockedStatic.verify(() -> Files.write(eq(resolvedPath), byteArgumentCaptor.capture()));
        assertArrayEquals(new byte[]{1, 2, 3}, byteArgumentCaptor.getValue());
        assertThat(stringArgumentCaptor.getValue(), matchesPattern(UUID_REGEX));
    }

    @Test
    @DisplayName("accept should not record the screenshot")
    void acceptShouldNotRecord() {
        when(testData.getScreenshotFolderPath()).thenReturn(screenshotFolderPath);
        when(screenshotFolderPath.resolve(stringArgumentCaptor.capture())).thenReturn(resolvedPath);
        when(resolvedPath.getFileName()).thenReturn(file);
        when(video.shouldRecord(eq("file"))).thenReturn(false);
        when(webDriverEvent.getFrame()).thenReturn(AUTO_AFTER);

        screenshotConsumer.accept(webDriverEvent);

        filesMockedStatic.verifyNoInteractions();
        verifyNoInteractions(driver);
    }
}