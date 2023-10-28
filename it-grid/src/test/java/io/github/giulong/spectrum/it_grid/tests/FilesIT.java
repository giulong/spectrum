package io.github.giulong.spectrum.it_grid.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_grid.pages.DownloadPage;
import io.github.giulong.spectrum.it_grid.pages.UploadPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.grid.Main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@DisplayName("Files Test")
@SuppressWarnings("unused")
public class FilesIT extends SpectrumTest<Void> {

    private static final String FILE_TO_DOWNLOAD = "empty.txt"; // this must be different from the downloaded file since herokuapp will randomly serve exactly the files used to test the upload
    private static final String FILE_TO_UPLOAD = "spectrum-logo.png";

    private DownloadPage downloadPage;

    private UploadPage uploadPage;

    @BeforeAll
    public static void beforeAll() {
        Main.main(new String[]{"standalone"});
    }

    @Test
    @DisplayName("download")
    public void download() {
        deleteDownloadsFolder();

        downloadPage.open();
        downloadPage.getDownloadLinks().get(0).click();

        assertThrows(TimeoutException.class, () -> checkDownloadedFile(FILE_TO_DOWNLOAD));
    }

    @Test
    @DisplayName("upload")
    public void upload() {
        uploadPage
                .open()
                .upload(uploadPage.getFileUpload(), FILE_TO_UPLOAD)
                .getSubmit().click();

        pageLoadWait.until(visibilityOf(uploadPage.getUploadedFiles()));
        assertEquals(FILE_TO_UPLOAD, uploadPage.getUploadedFiles().getText());
    }
}
