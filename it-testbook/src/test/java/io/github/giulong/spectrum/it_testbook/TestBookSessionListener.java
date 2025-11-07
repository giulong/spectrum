package io.github.giulong.spectrum.it_testbook;

import static java.util.Comparator.comparingLong;
import static java.util.function.Predicate.not;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.github.giulong.spectrum.utils.*;
import io.github.giulong.spectrum.utils.reporters.FileReporter;

import lombok.SneakyThrows;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;

@SuppressWarnings("unused")
public class TestBookSessionListener implements LauncherSessionListener {

    private final MetadataManager metadataManager = MetadataManager.getInstance();

    private final List<String> fakeExtentReportsDirectories = List.of("fakeExtentReport1", "fakeExtentReport2", "fakeExtentReport3");
    private final List<String> fakeExtentReports = List.of("fakeExtentReport1.html", "fakeExtentReport2.html", "fakeExtentReport3.html");
    private final List<String> fakeHtmlTestBooks = List.of("fakeTestBook1.html", "fakeTestBook2.html", "fakeTestBook3.html");
    private final List<String> fakeTxtTestBooks = List.of("fakeTestBook1.txt", "fakeTestBook2.txt", "fakeTestBook3.txt");
    private final List<String> fakeHtmlSummaries = List.of("fakeSummary1.html", "fakeSummary2.html", "fakeSummary3.html");
    private final List<String> fakeTxtSummaries = List.of("fakeSummary1.txt", "fakeSummary2.txt", "fakeSummary3.txt");
    private final FileUtils fileUtils = FileUtils.getInstance();
    private final YamlUtils yamlUtils = YamlUtils.getInstance();
    private Path extentReportsDirectory;
    private Path htmlTestBooksDirectory;
    private Path txtTestBooksDirectory;
    private Path htmlSummaryReportsDirectory;
    private Path txtSummaryReportsDirectory;
    private int extentTotalRetention;
    private int htmlTestBookTotalRetention;
    private int htmlTestBookSuccessfulRetention;
    private int txtTestBookTotalRetention;
    private int txtTestBookSuccessfulRetention;
    private int htmlSummaryTotalRetention;
    private int htmlSummarySuccessfulRetention;
    private int txtSummaryTotalRetention;
    private int txtSummarySuccessfulRetention;

    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        final Configuration configuration = yamlUtils.readInternal("configuration.yml", Configuration.class);
        final Configuration.Extent extent = configuration.getExtent();
        final FileReporter htmlTestBookReporter = getTestBookReporterFrom(configuration, "html");
        final FileReporter txtTestBookReporter = getTestBookReporterFrom(configuration, "txt");
        final FileReporter htmlSummaryReporter = getSummaryReporterFrom(configuration, "html");
        final FileReporter txtSummaryReporter = getSummaryReporterFrom(configuration, "txt");
        final Path testBookReportsPath = Path.of("target/spectrum/testbook");
        final Path summaryReportsPath = Path.of("target/spectrum/summary");

        extentReportsDirectory = Path.of("target/spectrum/reports");
        htmlTestBooksDirectory = testBookReportsPath;
        txtTestBooksDirectory = testBookReportsPath;
        htmlSummaryReportsDirectory = summaryReportsPath;
        txtSummaryReportsDirectory = summaryReportsPath;

        fileUtils.deleteContentOf(extentReportsDirectory);
        fileUtils.deleteContentOf(htmlTestBooksDirectory);
        fileUtils.deleteContentOf(txtTestBooksDirectory);
        fileUtils.deleteContentOf(htmlSummaryReportsDirectory);
        fileUtils.deleteContentOf(txtSummaryReportsDirectory);

        extentTotalRetention = extent.getRetention().getTotal();
        assertEquals(3, extentTotalRetention);

        htmlTestBookTotalRetention = htmlTestBookReporter.getRetention().getTotal();
        htmlTestBookSuccessfulRetention = htmlTestBookReporter.getRetention().getSuccessful();
        assertEquals(Integer.MAX_VALUE, htmlTestBookTotalRetention);
        assertEquals(2, htmlTestBookSuccessfulRetention);

        txtTestBookTotalRetention = txtTestBookReporter.getRetention().getTotal();
        txtTestBookSuccessfulRetention = txtTestBookReporter.getRetention().getSuccessful();
        assertEquals(3, txtTestBookTotalRetention);
        assertEquals(2, txtTestBookSuccessfulRetention);

        htmlSummaryTotalRetention = htmlSummaryReporter.getRetention().getTotal();
        htmlSummarySuccessfulRetention = htmlSummaryReporter.getRetention().getSuccessful();
        assertEquals(Integer.MAX_VALUE, htmlSummaryTotalRetention);
        assertEquals(0, htmlSummarySuccessfulRetention);

        txtSummaryTotalRetention = txtSummaryReporter.getRetention().getTotal();
        txtSummarySuccessfulRetention = txtSummaryReporter.getRetention().getSuccessful();
        assertEquals(Integer.MAX_VALUE, txtSummaryTotalRetention);
        assertEquals(0, txtSummarySuccessfulRetention);

        createDirectories(fakeExtentReportsDirectories, extentReportsDirectory);
        createExtentFiles(fakeExtentReports, extentReportsDirectory);
        createFiles(fakeHtmlTestBooks, htmlTestBooksDirectory);
        createFiles(fakeTxtTestBooks, txtTestBooksDirectory);
        createFiles(fakeHtmlSummaries, htmlSummaryReportsDirectory);
        createFiles(fakeTxtSummaries, txtSummaryReportsDirectory);
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        final Map<String, FixedSizeQueue<File>> successfulReports = metadataManager.getMetadata().getExecution().getSuccessful().getReports();

        final List<File> remainingExtentReportsDirectories = getRemainingDirectoriesFrom(extentReportsDirectory.toFile().listFiles());
        final List<String> remainingExtentReportsDirectoriesNames = getNamesOf(remainingExtentReportsDirectories);
        assertEquals(extentTotalRetention, remainingExtentReportsDirectories.size());
        assertFalse(remainingExtentReportsDirectoriesNames.contains(fakeExtentReportsDirectories.getFirst()),
                "The first extent report directory should have been deleted due to retention policies");

        final List<File> remainingHtmlTestBooks = getRemainingFilesFrom(htmlTestBooksDirectory.toFile().listFiles(), "html");
        assertTrue(remainingHtmlTestBooks.size() <= htmlTestBookTotalRetention);
        assertTrue(remainingHtmlTestBooks.size() >= htmlTestBookSuccessfulRetention);
        assertTrue(remainingHtmlTestBooks
                .stream()
                .map(File::toPath)
                .map(Path::toAbsolutePath)
                .map(Path::toFile)
                .toList()
                .containsAll(successfulReports.get("HtmlTestBookReporter")));
        assertTrue(getNamesOf(remainingHtmlTestBooks).containsAll(fakeHtmlTestBooks),
                "Html reporter should have the default total retention of Integer.MAX_VALUE, so no one should be deleted");

        final List<File> remainingTxtTestBooks = getRemainingFilesFrom(txtTestBooksDirectory.toFile().listFiles(), "txt");
        final List<String> remainingTxtTestBooksNames = getNamesOf(remainingTxtTestBooks);
        assertTrue(remainingTxtTestBooks.size() <= txtTestBookTotalRetention);
        assertTrue(remainingTxtTestBooks.size() >= txtTestBookSuccessfulRetention);
        assertTrue(remainingTxtTestBooks
                .stream()
                .map(File::toPath)
                .map(Path::toAbsolutePath)
                .map(Path::toFile)
                .toList()
                .containsAll(successfulReports.get("TxtTestBookReporter")));
        assertFalse(remainingTxtTestBooksNames.contains(fakeTxtTestBooks.getFirst()), "The first txt testbook should have been deleted due to retention policies");
        assertTrue(remainingTxtTestBooksNames.contains(fakeTxtTestBooks.get(1)), "The second txt testbook should have been kept due to retention policies");
        assertTrue(remainingTxtTestBooksNames.contains(fakeTxtTestBooks.get(2)), "The third txt testbook should have been kept due to retention policies");

        final List<File> remainingHtmlSummaries = getRemainingFilesFrom(htmlSummaryReportsDirectory.toFile().listFiles(), "html");
        assertTrue(remainingHtmlSummaries.size() <= htmlSummaryTotalRetention);
        assertTrue(remainingHtmlSummaries.size() >= htmlSummarySuccessfulRetention);
        assertTrue(remainingHtmlSummaries
                .stream()
                .map(File::toPath)
                .map(Path::toAbsolutePath)
                .map(Path::toFile)
                .toList()
                .containsAll(successfulReports.get("HtmlSummaryReporter")));
        assertTrue(getNamesOf(remainingHtmlSummaries).containsAll(fakeHtmlSummaries),
                "Html reporter should have the default total retention of Integer.MAX_VALUE, so no one should be deleted");

        final List<File> remainingTxtSummaries = getRemainingFilesFrom(txtSummaryReportsDirectory.toFile().listFiles(), "txt");
        assertTrue(remainingTxtSummaries.size() <= txtSummaryTotalRetention);
        assertTrue(remainingTxtSummaries.size() >= txtSummarySuccessfulRetention);
        assertTrue(remainingTxtSummaries
                .stream()
                .map(File::toPath)
                .map(Path::toAbsolutePath)
                .map(Path::toFile)
                .toList()
                .containsAll(successfulReports.get("TxtSummaryReporter")));
        assertTrue(getNamesOf(remainingTxtSummaries).containsAll(fakeTxtSummaries),
                "Txt reporter should have the default total retention of Integer.MAX_VALUE, so no one should be deleted");

        deleteDirectories(fakeExtentReportsDirectories, extentReportsDirectory);
        deleteFiles(fakeHtmlTestBooks, htmlTestBooksDirectory);
        deleteFiles(fakeTxtTestBooks, txtTestBooksDirectory);
        deleteFiles(fakeTxtTestBooks, htmlSummaryReportsDirectory);
        deleteFiles(fakeTxtTestBooks, txtSummaryReportsDirectory);
    }

    @SneakyThrows
    private void createExtentFiles(final List<String> fileNames, final Path directory) {
        for (String fileName : fileNames) {
            assertTrue(Files.createFile(directory.resolve(fileUtils.removeExtensionFrom(fileName)).resolve(fileName)).toFile().exists());
            Thread.sleep(1000); // just to be sure files have different creation dates
        }
    }

    @SneakyThrows
    private void createFiles(final List<String> fileNames, final Path directory) {
        for (String fileName : fileNames) {
            assertTrue(Files.createFile(directory.resolve(fileName)).toFile().exists());
            Thread.sleep(1000); // just to be sure files have different creation dates
        }
    }

    @SneakyThrows
    private void createDirectories(final List<String> directoryNames, final Path parentDirectory) {
        for (String fileName : directoryNames) {
            assertTrue(Files.createDirectories(parentDirectory.resolve(fileName)).toFile().exists());
            Thread.sleep(1000); // just to be sure files have different creation dates
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
                .forEach(fileUtils::delete);
    }

    private FileReporter getTestBookReporterFrom(final Configuration configuration, final String extension) {
        return configuration
                .getTestBook()
                .getReporters()
                .stream()
                .filter(reporter -> reporter instanceof FileReporter)
                .map(FileReporter.class::cast)
                .filter(reporter -> FileUtils.getInstance().getExtensionOf(reporter.getTemplate()).equals(extension))
                .findFirst()
                .orElseThrow();
    }

    private FileReporter getSummaryReporterFrom(final Configuration configuration, final String extension) {
        return configuration
                .getSummary()
                .getReporters()
                .stream()
                .filter(reporter -> reporter instanceof FileReporter)
                .map(FileReporter.class::cast)
                .filter(reporter -> FileUtils.getInstance().getExtensionOf(reporter.getTemplate()).equals(extension))
                .findFirst()
                .orElseThrow();
    }

    private List<File> getRemainingFilesFrom(final File[] files, final String extension) {
        return Arrays
                .stream(files)
                .filter(not(File::isDirectory))
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
