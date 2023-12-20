package io.github.giulong.spectrum.it_testbook;

import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.YamlUtils;
import io.github.giulong.spectrum.utils.testbook.reporters.FileTestBookReporter;
import io.github.giulong.spectrum.utils.testbook.reporters.HtmlTestBookReporter;
import io.github.giulong.spectrum.utils.testbook.reporters.TxtTestBookReporter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static java.util.Comparator.comparingLong;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class TestBookSessionListener implements LauncherSessionListener {

    private final List<String> fakeExtentReportsDirectories = List.of("fakeExtentReport1", "fakeExtentReport2", "fakeExtentReport3");
    private final List<String> fakeExtentReports = List.of("fakeExtentReport1.html", "fakeExtentReport2.html", "fakeExtentReport3.html");
    private final List<String> fakeHtmlTestBooks = List.of("fakeTestBook1.html", "fakeTestBook2.html", "fakeTestBook3.html");
    private final List<String> fakeTxtTestBooks = List.of("fakeTestBook1.txt", "fakeTestBook2.txt", "fakeTestBook3.txt");
    private final FileUtils fileUtils = FileUtils.getInstance();
    private final YamlUtils yamlUtils = YamlUtils.getInstance();
    private Path extentReportsDirectory;
    private Path htmlTestBooksDirectory;
    private Path txtTestBooksDirectory;
    private int extentTotalRetention;
    private int txtTestBookTotalRetention;

    @Override
    @SneakyThrows
    public void launcherSessionOpened(final LauncherSession session) {
        final Configuration configuration = yamlUtils.readInternal("configuration.yaml", Configuration.class);
        final Configuration.Extent extent = configuration.getExtent();
        final FileTestBookReporter htmlTestBookReporter = getReporterFrom(configuration, HtmlTestBookReporter.class);
        final FileTestBookReporter txtTestBookReporter = getReporterFrom(configuration, TxtTestBookReporter.class);

        extentReportsDirectory = Path.of("target/spectrum/reports");
        htmlTestBooksDirectory = Path.of(htmlTestBookReporter.getOutput()).getParent();
        txtTestBooksDirectory = Path.of(txtTestBookReporter.getOutput()).getParent();

        fileUtils.deleteDirectory(extentReportsDirectory);
        fileUtils.deleteDirectory(htmlTestBooksDirectory);
        fileUtils.deleteDirectory(txtTestBooksDirectory);
        Files.createDirectories(extentReportsDirectory);
        Files.createDirectories(htmlTestBooksDirectory);
        Files.createDirectories(txtTestBooksDirectory);

        extentTotalRetention = extent.getRetention().getTotal();
        assertEquals(3, extentTotalRetention);
        txtTestBookTotalRetention = txtTestBookReporter.getRetention().getTotal();
        assertEquals(2, txtTestBookTotalRetention);

        createDirectories(fakeExtentReportsDirectories, extentReportsDirectory);
        createFiles(fakeExtentReports, extentReportsDirectory);
        createFiles(fakeHtmlTestBooks, htmlTestBooksDirectory);
        createFiles(fakeTxtTestBooks, txtTestBooksDirectory);
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        final List<File> remainingExtentReportsDirectories = getRemainingDirectoriesFrom(extentReportsDirectory.toFile().listFiles());
        final List<String> remainingExtentReportsDirectoriesNames = getNamesOf(remainingExtentReportsDirectories);
        assertEquals(extentTotalRetention, remainingExtentReportsDirectories.size());
        assertFalse(remainingExtentReportsDirectoriesNames.contains(fakeExtentReportsDirectories.get(0)), "The first extent report directory should have been deleted due to retention policies");

        final List<File> remainingExtentReports = getRemainingFilesFrom(extentReportsDirectory.toFile().listFiles(), "html");
        final List<String> remainingExtentReportsNames = getNamesOf(remainingExtentReports);
        assertEquals(extentTotalRetention, remainingExtentReports.size());
        assertFalse(remainingExtentReportsNames.contains(fakeExtentReports.get(0)), "The first extent report should have been deleted due to retention policies");

        final List<File> remainingHtmlTestBooks = getRemainingFilesFrom(htmlTestBooksDirectory.toFile().listFiles(), "html");
        assertTrue(getNamesOf(remainingHtmlTestBooks).containsAll(fakeHtmlTestBooks),
                "Html reporter should have the default total retention of Integer.MAX_VALUE, so no one should be deleted");

        final List<File> remainingTxtTestBooks = getRemainingFilesFrom(htmlTestBooksDirectory.toFile().listFiles(), "txt");
        final List<String> remainingTxtTestBooksNames = getNamesOf(remainingTxtTestBooks);
        assertEquals(txtTestBookTotalRetention, remainingTxtTestBooks.size());
        assertFalse(remainingTxtTestBooksNames.contains(fakeTxtTestBooks.get(0)), "The first txt testbook should have been deleted due to retention policies");
        assertFalse(remainingTxtTestBooksNames.contains(fakeTxtTestBooks.get(1)), "The second txt testbook should have been deleted due to retention policies");

        deleteDirectories(fakeExtentReportsDirectories, extentReportsDirectory);
        deleteFiles(fakeExtentReports, extentReportsDirectory);
        deleteFiles(fakeHtmlTestBooks, htmlTestBooksDirectory);
        deleteFiles(fakeTxtTestBooks, txtTestBooksDirectory);
    }

    @SneakyThrows
    private void createFiles(final List<String> fileNames, final Path directory) {
        for (String fileName : fileNames) {
            assertTrue(Files.createFile(directory.resolve(fileName)).toFile().exists());
            Thread.sleep(10);   // just to be sure files have different creation dates
        }
    }

    @SneakyThrows
    private void createDirectories(final List<String> directoryNames, final Path parentDirectory) {
        for (String fileName : directoryNames) {
            assertTrue(Files.createDirectories(parentDirectory.resolve(fileName)).toFile().exists());
            Thread.sleep(10);   // just to be sure files have different creation dates
        }
    }

    private List<String> getNamesOf(final List<File> files) {
        return files.stream().map(File::getName).toList();
    }

    private void deleteFiles(final List<String> fileNames, final Path directory) {
        for (String fileName : fileNames) {
            final File file = directory.resolve(fileName).toFile();

            if (file.exists()) {
                file.deleteOnExit();
            }
        }
    }

    private void deleteDirectories(final List<String> directoryNames, final Path parentDirectory) {
        directoryNames
                .stream()
                .map(Path::of)
                .map(parentDirectory::resolve)
                .forEach(fileUtils::deleteDirectory);
    }

    private FileTestBookReporter getReporterFrom(final Configuration configuration, final Class<? extends FileTestBookReporter> clazz) {
        return (FileTestBookReporter) configuration
                .getTestBook()
                .getReporters()
                .stream()
                .filter(testBookReporter -> testBookReporter.getClass().equals(clazz))
                .findFirst()
                .orElseThrow();
    }

    private List<File> getRemainingFilesFrom(final File[] files, final String extension) {
        return Arrays
                .stream(files)
                .filter(file -> !file.isDirectory())
                .filter(file -> file.getName().endsWith(extension))
                .sorted(comparingLong(File::lastModified))
                .toList();
    }

    private List<File> getRemainingDirectoriesFrom(final File[] files) {
        return Arrays
                .stream(files)
                .filter(File::isDirectory)
                .sorted(comparingLong(File::lastModified))
                .toList();
    }
}
