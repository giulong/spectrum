package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it.pages.DownloadPage;
import io.github.giulong.spectrum.it.pages.UploadPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@DisplayName("Files Test")
@SuppressWarnings("unused")
public class FilesIT extends SpectrumTest<Void> {

    // this must be different from the downloaded file since herokuapp will randomly serve exactly the files used to test the upload
    private static final String FILE_TO_DOWNLOAD = "empty.txt";
    private static final String FILE_TO_UPLOAD = "spectrum-logo.png";

    private DownloadPage downloadPage;
    private UploadPage uploadPage;

    @BeforeEach
    void beforeEach() {
        // We call the inherited helper method to ensure a fresh download
        deleteDownloadsFolder();
    }

    @Test
    @DisplayName("download")
    @Tags({
            @Tag("tag1"),
            @Tag("tag2"),
    })
    public void download() {
        downloadPage
                .open()
                .getDownloadLinks()
                .getFirst()
                .click();

        // We call the inherited helper method to check if the downloaded file is the one we expect
        // This is expected to fail since we're comparing it with a wrong file
        assertThrows(TimeoutException.class, () -> checkDownloadedFile(FILE_TO_DOWNLOAD));
    }

    @Test
    @DisplayName("upload")
    @Tag("tag2")
    public void upload() {
        uploadPage
                .open()
                .upload(uploadPage.getFileUpload(), FILE_TO_UPLOAD)
                .getSubmit().click();

        pageLoadWait.until(visibilityOf(uploadPage.getUploadedFiles()));
        assertEquals(FILE_TO_UPLOAD, uploadPage.getUploadedFiles().getText());

        downloadPage
                .open()
                .getDownloadLinks()
                .stream()
                .filter(webElement -> webElement.getText().equals(FILE_TO_UPLOAD))
                .findFirst()
                .orElseThrow()
                .click();

        assertTrue(checkDownloadedFile(FILE_TO_UPLOAD));
    }
}
