package io.github.giulong.spectrum.utils;

import static java.lang.Integer.MAX_VALUE;
import static java.time.ZoneId.systemDefault;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import io.github.giulong.spectrum.MockSingleton;

import lombok.SneakyThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class RetentionTest {

    private static MockedStatic<Files> filesMockedStatic;
    private static MockedStatic<LocalDateTime> localDateTimeMockedStatic;

    private final int old = 100;
    private final int young = 0;

    @Mock
    private LocalDateTime now;

    @Mock
    private LocalDateTime dateTime1;

    @Mock
    private LocalDateTime dateTime2;

    @Mock
    private LocalDateTime dateTime3;

    @Mock
    private LocalDateTime dateTime4;

    @Mock
    private LocalDateTime dateTime5;

    @Mock
    private FileTime creationTime1;

    @Mock
    private FileTime creationTime2;

    @Mock
    private FileTime creationTime3;

    @Mock
    private FileTime creationTime4;

    @Mock
    private FileTime creationTime5;

    @Mock
    private Instant instant1;

    @Mock
    private Instant instant2;

    @Mock
    private Instant instant3;

    @Mock
    private Instant instant4;

    @Mock
    private Instant instant5;

    @MockSingleton
    @SuppressWarnings("unused")
    private MetadataManager metadataManager;

    @MockSingleton
    @SuppressWarnings("unused")
    private FileUtils fileUtils;

    @Mock
    private ExtentReporter metadataProducer;

    @Mock
    private FixedSizeQueue<File> successfulQueue;

    @Mock
    private File file1;

    @Mock
    private File file2;

    @Mock
    private File file3;

    @Mock
    private File file4;

    @Mock
    private File file5;

    @Mock
    private File absoluteFile1;

    @Mock
    private File absoluteFile2;

    @Mock
    private File absoluteFile3;

    @Mock
    private File absoluteFile4;

    @Mock
    private File absoluteFile5;

    @InjectMocks
    private Retention retention;

    @BeforeEach
    void beforeEach() {
        filesMockedStatic = mockStatic(Files.class);
        localDateTimeMockedStatic = mockStatic(LocalDateTime.class);
    }

    @AfterEach
    void afterEach() {
        filesMockedStatic.close();
        localDateTimeMockedStatic.close();
    }

    @SneakyThrows
    private void isOldStubs(final long days1, final long days2, final long days3, final long days4, final long days5) {
        Reflections.setField("days", retention, 5);

        when(LocalDateTime.now()).thenReturn(now);
        when(metadataManager.getSuccessfulQueueOf(metadataProducer)).thenReturn(successfulQueue);

        lenient().when(file1.getAbsoluteFile()).thenReturn(absoluteFile1);
        lenient().when(file2.getAbsoluteFile()).thenReturn(absoluteFile2);
        lenient().when(file3.getAbsoluteFile()).thenReturn(absoluteFile3);
        lenient().when(file4.getAbsoluteFile()).thenReturn(absoluteFile4);
        lenient().when(file5.getAbsoluteFile()).thenReturn(absoluteFile5);

        lenient().when(fileUtils.getCreationTimeOf(file1)).thenReturn(creationTime1);
        lenient().when(fileUtils.getCreationTimeOf(file2)).thenReturn(creationTime2);
        lenient().when(fileUtils.getCreationTimeOf(file3)).thenReturn(creationTime3);
        lenient().when(fileUtils.getCreationTimeOf(file4)).thenReturn(creationTime4);
        lenient().when(fileUtils.getCreationTimeOf(file5)).thenReturn(creationTime5);

        lenient().when(creationTime1.toInstant()).thenReturn(instant1);
        lenient().when(creationTime2.toInstant()).thenReturn(instant2);
        lenient().when(creationTime3.toInstant()).thenReturn(instant3);
        lenient().when(creationTime4.toInstant()).thenReturn(instant4);
        lenient().when(creationTime5.toInstant()).thenReturn(instant5);

        lenient().when(LocalDateTime.ofInstant(instant1, systemDefault())).thenReturn(dateTime1);
        lenient().when(LocalDateTime.ofInstant(instant2, systemDefault())).thenReturn(dateTime2);
        lenient().when(LocalDateTime.ofInstant(instant3, systemDefault())).thenReturn(dateTime3);
        lenient().when(LocalDateTime.ofInstant(instant4, systemDefault())).thenReturn(dateTime4);
        lenient().when(LocalDateTime.ofInstant(instant5, systemDefault())).thenReturn(dateTime5);

        lenient().when(dateTime1.until(now, DAYS)).thenReturn(days1);
        lenient().when(dateTime2.until(now, DAYS)).thenReturn(days2);
        lenient().when(dateTime3.until(now, DAYS)).thenReturn(days3);
        lenient().when(dateTime4.until(now, DAYS)).thenReturn(days4);
        lenient().when(dateTime5.until(now, DAYS)).thenReturn(days5);
    }

    @Test
    @DisplayName("the default retention should be: total MAX_VALUE, successful 0, and days MAX_VALUE")
    void defaultRetention() {
        assertEquals(MAX_VALUE, retention.getTotal());
        assertEquals(0, retention.getSuccessful());
        assertEquals(MAX_VALUE, retention.getDays());
    }

    @Test
    @DisplayName("deleteArtifactsFrom should delete files if there are more reports than the total allowed")
    void deleteArtifactsFrom() {
        isOldStubs(young, young, young, young, young);

        Reflections.setField("total", retention, 1);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3), metadataProducer);

        verify(fileUtils).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils, never()).delete(file3);
    }

    @Test
    @DisplayName("deleteArtifactsFrom should delete no file if there are less reports than the total allowed")
    void deleteArtifactsFromMinimum() {
        isOldStubs(young, young, young, young, young);

        Reflections.setField("total", retention, 5);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3), metadataProducer);

        verify(file1, never()).delete();
        verify(file2, never()).delete();
        verify(file3, never()).delete();
    }

    @Test
    @DisplayName("#1 deleteArtifactsFrom should keep the configured number of successful reports, keeping old files if they're successful and should be kept")
    void deleteArtifactsFromMixedOne() {
        isOldStubs(old, young, young, young, young);

        Reflections.setField("total", retention, 2);
        Reflections.setField("successful", retention, 1);

        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
        verify(fileUtils).delete(file4);
        verify(fileUtils, never()).delete(file5);
    }

    @Test
    @DisplayName("#2 deleteArtifactsFrom should keep the configured number of successful reports, keeping old files if they're successful and should be kept")
    void deleteArtifactsFromMixedTwo() {
        isOldStubs(old, young, young, young, young);

        Reflections.setField("total", retention, 2);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
        verify(fileUtils, never()).delete(file4);
        verify(fileUtils, never()).delete(file5);
    }

    @Test
    @DisplayName("#3 deleteArtifactsFrom should keep the configured number of successful reports, keeping old files if they're successful and should be kept")
    void deleteArtifactsFromMixedThree() {
        isOldStubs(old, old, young, young, young);

        Reflections.setField("total", retention, 2);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
        verify(fileUtils, never()).delete(file4);
        verify(fileUtils, never()).delete(file5);
    }

    @Test
    @DisplayName("#4 deleteArtifactsFrom should keep the configured number of successful reports, keeping old files if they're successful and should be kept")
    void deleteArtifactsFromMixedFour() {
        isOldStubs(old, old, young, young, young);

        Reflections.setField("total", retention, 2);
        Reflections.setField("successful", retention, 2);

        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);
        when(successfulQueue.contains(absoluteFile2)).thenReturn(false);
        when(successfulQueue.contains(absoluteFile3)).thenReturn(true);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils, never()).delete(file3);
        verify(fileUtils).delete(file4);
        verify(fileUtils).delete(file5);
    }

    @Test
    @DisplayName("deleteArtifactsFrom should delete all old files, regardless of total, if no successful is configured")
    void deleteArtifactsFromDeleteAllOld() {
        isOldStubs(old, old, old, old, old);

        Reflections.setField("total", retention, 2);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
        verify(fileUtils).delete(file4);
        verify(fileUtils).delete(file5);
    }

    @Test
    @DisplayName("#1 deleteArtifactsFrom should keep the configured number of successful reports")
    void deleteArtifactsFromSuccessfulOne() {
        isOldStubs(young, young, young, young, young);

        Reflections.setField("total", retention, 2);
        Reflections.setField("successful", retention, 1);

        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
        verify(fileUtils).delete(file4);
        verify(fileUtils, never()).delete(file5);
    }

    @Test
    @DisplayName("#2 deleteArtifactsFrom should keep the configured number of successful reports")
    void deleteArtifactsFromSuccessfulTwo() {
        isOldStubs(young, young, young, young, young);

        Reflections.setField("total", retention, 5);
        Reflections.setField("successful", retention, 1);

        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(file1, never()).delete();
        verify(file2, never()).delete();
        verify(file3, never()).delete();
        verify(file4, never()).delete();
        verify(file5, never()).delete();
    }

    @Test
    @DisplayName("#3 deleteArtifactsFrom should keep the configured number of successful reports")
    void deleteArtifactsFromSuccessfulThree() {
        isOldStubs(young, young, young, young, young);

        Reflections.setField("total", retention, 2);
        Reflections.setField("successful", retention, 2);

        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
        verify(fileUtils).delete(file4);
        verify(fileUtils, never()).delete(file5);
    }

    @Test
    @DisplayName("#4 deleteArtifactsFrom should keep the configured number of successful reports")
    void deleteArtifactsFromSuccessfulFour() {
        isOldStubs(young, young, young, young, young);

        Reflections.setField("total", retention, 3);
        Reflections.setField("successful", retention, 2);

        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);
        when(successfulQueue.contains(absoluteFile2)).thenReturn(false);
        when(successfulQueue.contains(absoluteFile3)).thenReturn(true);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils, never()).delete(file3);
        verify(fileUtils).delete(file4);
        verify(fileUtils, never()).delete(file5);
    }

    @Test
    @DisplayName("#5 deleteArtifactsFrom should keep the configured number of successful reports")
    void deleteArtifactsFromSuccessfulFive() {
        isOldStubs(young, young, young, young, young);

        Reflections.setField("total", retention, 3);
        Reflections.setField("successful", retention, 2);

        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);
        when(successfulQueue.contains(absoluteFile2)).thenReturn(true);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils, never()).delete(file2);
        verify(fileUtils).delete(file3);
        verify(fileUtils).delete(file4);
        verify(fileUtils, never()).delete(file5);
    }

    @Test
    @DisplayName("#6 deleteArtifactsFrom should keep the configured number of successful reports")
    void deleteArtifactsFromSuccessfulSix() {
        isOldStubs(young, young, young, young, young);

        Reflections.setField("total", retention, 3);
        Reflections.setField("successful", retention, 2);

        when(successfulQueue.contains(absoluteFile1)).thenReturn(true);
        when(successfulQueue.contains(absoluteFile2)).thenReturn(false);
        when(successfulQueue.contains(absoluteFile3)).thenReturn(false);
        when(successfulQueue.contains(absoluteFile4)).thenReturn(true);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3, file4, file5), metadataProducer);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
        verify(fileUtils, never()).delete(file4);
        verify(fileUtils, never()).delete(file5);
    }

    @Test
    @DisplayName("deleteArtifactsFrom should respect files' creation time, deleting older ones first")
    void deleteFromSorted() {
        isOldStubs(1, 2, 3, old, old);

        Reflections.setField("total", retention, 1);

        when(creationTime2.compareTo(creationTime1)).thenReturn(-1);
        when(creationTime3.compareTo(creationTime2)).thenReturn(-1);

        retention.deleteArtifactsFrom(List.of(file1, file2, file3), metadataProducer);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
    }

    @Test
    @DisplayName("#1 deleteFrom should delete all the provided old files, retaining successful ones, and returning the number of files kept")
    void deleteFromOne() {
        retention.deleteFrom(List.of(file1, file2, file3), 1);

        verify(fileUtils).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils, never()).delete(file3);
    }

    @Test
    @DisplayName("#2 deleteFrom should delete all the provided old files, retaining successful ones, and returning the number of files kept")
    void deleteFromTwo() {
        retention.deleteFrom(List.of(file1, file2, file3), 2);

        verify(fileUtils).delete(file1);
        verify(fileUtils, never()).delete(file2);
        verify(fileUtils, never()).delete(file3);
    }

    @Test
    @DisplayName("deleteFrom should clamp the maxToKeep to avoid negative numbers provided from client side")
    void deleteFromNegativeMaxToKeep() {
        retention.deleteFrom(List.of(file1, file2, file3), -2);

        verify(fileUtils).delete(file1);
        verify(fileUtils).delete(file2);
        verify(fileUtils).delete(file3);
    }

    @Test
    @DisplayName("deleteFrom should clamp the maxToKeep to avoid numbers bigger than the files list size")
    void deleteFromHigherMaxToKeep() {
        retention.deleteFrom(List.of(file1, file2, file3), MAX_VALUE);

        verify(fileUtils, never()).delete(file1);
        verify(fileUtils, never()).delete(file2);
        verify(fileUtils, never()).delete(file3);
    }

    @DisplayName("isOld should check if the provided path was created before than the retention days count set to 3")
    @ParameterizedTest(name = "with {0} days old we expect {1}")
    @MethodSource("valuesProvider")
    void isOld(final long days, final boolean expected) {
        Reflections.setField("days", retention, 3);

        lenient().when(fileUtils.getCreationTimeOf(file1)).thenReturn(creationTime1);
        when(creationTime1.toInstant()).thenReturn(instant1);
        when(LocalDateTime.ofInstant(instant1, systemDefault())).thenReturn(dateTime1);
        when(LocalDateTime.now()).thenReturn(now);
        when(dateTime1.until(now, DAYS)).thenReturn(days);

        assertEquals(expected, retention.isOld(file1));
    }

    static Stream<Arguments> valuesProvider() {
        return Stream.of(
                arguments(5L, true),
                arguments(3L, true),
                arguments(0L, false));
    }
}
