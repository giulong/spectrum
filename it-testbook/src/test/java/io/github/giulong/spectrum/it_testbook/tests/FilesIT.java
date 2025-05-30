package io.github.giulong.spectrum.it_testbook.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_testbook.pages.DownloadPage;
import io.github.giulong.spectrum.it_testbook.pages.UploadPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@DisplayName("Files Test")
@SuppressWarnings("unused")
class FilesIT extends SpectrumTest<Void> {

    // this must be different from the downloaded file since herokuapp will randomly serve exactly the files used to test the upload
    private static final String FILE_TO_DOWNLOAD = "empty.txt";
    private static final String FILE_TO_UPLOAD = "spectrum-logo.png";

    private DownloadPage downloadPage;

    private UploadPage uploadPage;

    @Test
    @DisplayName("download")
    void download() {
        deleteDownloadsFolder();

        downloadPage.open();
        downloadPage.getDownloadLinks().getFirst().click();

        assertThrows(TimeoutException.class, () -> checkDownloadedFile(FILE_TO_DOWNLOAD));
    }

    @Test
    @DisplayName("upload")
    void upload() {
        uploadPage
                .open()
                .upload(uploadPage.getFileUpload(), FILE_TO_UPLOAD)
                .getSubmit().click();

        pageLoadWait.until(visibilityOf(uploadPage.getUploadedFiles()));
        assertEquals(FILE_TO_UPLOAD, uploadPage.getUploadedFiles().getText());
    }
}
