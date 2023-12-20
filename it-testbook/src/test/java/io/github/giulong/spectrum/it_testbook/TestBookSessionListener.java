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

    private final List<String> fakeHtmlTestBooks = List.of("fakeTestBook1.html", "fakeTestBook2.html", "fakeTestBook3.html");
    private final List<String> fakeTxtTestBooks = List.of("fakeTestBook1.txt", "fakeTestBook2.txt", "fakeTestBook3.txt");
    private final FileUtils fileUtils = FileUtils.getInstance();
    private final YamlUtils yamlUtils = YamlUtils.getInstance();
    private Path htmlTestBooksDirectory;
    private Path txtTestBooksDirectory;
    private int txtTestBookTotalRetention;

    @Override
    @SneakyThrows
    public void launcherSessionOpened(final LauncherSession session) {
        final Configuration configuration = yamlUtils.readInternal("configuration.yaml", Configuration.class);
        final FileTestBookReporter htmlTestBookReporter = getReporterFrom(configuration, HtmlTestBookReporter.class);
        final FileTestBookReporter txtTestBookReporter = getReporterFrom(configuration, TxtTestBookReporter.class);

        htmlTestBooksDirectory = Path.of(htmlTestBookReporter.getOutput()).getParent();
        txtTestBooksDirectory = Path.of(txtTestBookReporter.getOutput()).getParent();

        fileUtils.deleteDirectory(htmlTestBooksDirectory);
        fileUtils.deleteDirectory(txtTestBooksDirectory);
        Files.createDirectories(htmlTestBooksDirectory);
        Files.createDirectories(txtTestBooksDirectory);

        txtTestBookTotalRetention = txtTestBookReporter.getRetention().getTotal();
        assertEquals(2, txtTestBookTotalRetention);

        createTestBooks(fakeHtmlTestBooks, htmlTestBooksDirectory);
        createTestBooks(fakeTxtTestBooks, txtTestBooksDirectory);
    }

    @Override
    public void launcherSessionClosed(LauncherSession session) {
        final List<File> remainingHtmlTestBooks = getRemainingTestBooksFrom(htmlTestBooksDirectory.toFile().listFiles(), "html");

        assertTrue(getNamesOf(remainingHtmlTestBooks).containsAll(fakeHtmlTestBooks),
                "Html reporter should have the default total retention of Integer.MAX_VALUE, so no one should be deleted");

        final List<File> remainingTxtTestBooks = getRemainingTestBooksFrom(htmlTestBooksDirectory.toFile().listFiles(), "txt");
        final List<String> remainingTxtTestBooksNames = getNamesOf(remainingTxtTestBooks);
        assertEquals(txtTestBookTotalRetention, remainingTxtTestBooks.size());
        assertFalse(remainingTxtTestBooksNames.contains(fakeTxtTestBooks.get(0)), "The first txt testbook should have been deleted due to retention policies");
        assertFalse(remainingTxtTestBooksNames.contains(fakeTxtTestBooks.get(1)), "The second txt testbook should have been deleted due to retention policies");

        deleteTestBooks(fakeHtmlTestBooks, htmlTestBooksDirectory);
        deleteTestBooks(fakeTxtTestBooks, txtTestBooksDirectory);
    }

    @SneakyThrows
    private void createTestBooks(final List<String> testBooks, final Path directory) {
        for (String testBook : testBooks) {
            assertTrue(Files.createFile(directory.resolve(testBook)).toFile().exists());
            Thread.sleep(10);   // just to be sure files have different creation dates
        }
    }

    private List<String> getNamesOf(final List<File> testBooks) {
        return testBooks.stream().map(File::getName).toList();
    }

    private void deleteTestBooks(final List<String> testBooks, final Path directory) {
        for (String testBook : testBooks) {
            final File testBookFile = directory.resolve(testBook).toFile();

            if (testBookFile.exists()) {
                testBookFile.deleteOnExit();
            }
        }
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

    private List<File> getRemainingTestBooksFrom(final File[] files, final String extension) {
        return Arrays
                .stream(files)
                .filter(file -> !file.isDirectory())
                .filter(file -> file.getName().endsWith(extension))
                .sorted(comparingLong(File::lastModified))
                .toList();
    }
}
