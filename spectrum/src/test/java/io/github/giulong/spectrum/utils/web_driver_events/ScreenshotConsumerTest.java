package io.github.giulong.spectrum.utils.web_driver_events;

import io.github.giulong.spectrum.enums.Frame;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.Reflections;
import io.github.giulong.spectrum.utils.StatefulExtentTest;
import io.github.giulong.spectrum.utils.video.Video;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.openqa.selenium.TakesScreenshot;

import java.nio.file.Files;
import java.nio.file.Path;

import static io.github.giulong.spectrum.enums.Frame.AUTO_AFTER;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.openqa.selenium.OutputType.BYTES;

class ScreenshotConsumerTest {

    private static MockedStatic<Files> filesMockedStatic;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private WebDriverEvent webDriverEvent;

    @Mock
    private TestData testData;

    @Mock
    private Path screenshotFolderPath;

    @Mock
    private Path resolvedPath;

    @Mock
    private TakesScreenshot driver;

    @Mock
    private Video video;

    @Mock
    private StatefulExtentTest statefulExtentTest;

    @Captor
    private ArgumentCaptor<byte[]> byteArgumentCaptor;

    @InjectMocks
    private ScreenshotConsumer screenshotConsumer = new ScreenshotConsumer(ScreenshotConsumer.builder());

    @BeforeEach
    void beforeEach() {
        Reflections.setField("fileUtils", screenshotConsumer, fileUtils);

        filesMockedStatic = mockStatic(Files.class);
    }

    @AfterEach
    void afterEach() {
        filesMockedStatic.close();
    }

    @Test
    @DisplayName("accept should record the screenshot")
    void accept() {
        final Frame frame = AUTO_AFTER;
        final String fileName = "fileName";

        when(testData.getScreenshotFolderPath()).thenReturn(screenshotFolderPath);
        when(screenshotFolderPath.resolve(fileName)).thenReturn(resolvedPath);
        when(video.shouldRecord(eq(frame))).thenReturn(true);
        when(driver.getScreenshotAs(BYTES)).thenReturn(new byte[]{1, 2, 3});
        when(webDriverEvent.getFrame()).thenReturn(frame);

        when(fileUtils.getScreenshotNameFrom(frame, statefulExtentTest)).thenReturn(fileName);

        screenshotConsumer.accept(webDriverEvent);

        filesMockedStatic.verify(() -> Files.write(eq(resolvedPath), byteArgumentCaptor.capture()));
        assertArrayEquals(new byte[]{1, 2, 3}, byteArgumentCaptor.getValue());
    }

    @Test
    @DisplayName("accept should not record the screenshot")
    void acceptShouldNotRecord() {
        final Frame frame = AUTO_AFTER;

        when(video.shouldRecord(eq(frame))).thenReturn(false);
        when(webDriverEvent.getFrame()).thenReturn(frame);

        screenshotConsumer.accept(webDriverEvent);

        filesMockedStatic.verifyNoInteractions();
        verifyNoInteractions(driver);
    }
}
