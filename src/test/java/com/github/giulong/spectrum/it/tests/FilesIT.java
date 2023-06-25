package com.github.giulong.spectrum.it.tests;

import com.github.giulong.spectrum.SpectrumTest;
import com.github.giulong.spectrum.it.pages.DownloadPage;
import com.github.giulong.spectrum.it.pages.UploadPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Files Test")
public class FilesIT extends SpectrumTest<Void> {

    private DownloadPage downloadPage;

    private UploadPage uploadPage;

    @Test
    @DisplayName("download")
    public void download() {
        // We call the inherited helper method to ensure a fresh download
        deleteDownloadsFolder();

        downloadPage.open();
        downloadPage.getDownloadLinks().get(0).click();

        // We call the inherited helper method to check if the downloaded file is the one we expect
        // This is expected to fail since we're comparing it with a wrong file
        assertThrows(TimeoutException.class, () -> checkDownloadedFile("spectrum-logo.png"));
    }

    @Test
    @DisplayName("upload")
    public void upload() {
        uploadPage
                .open()
                .upload(uploadPage.getFileUpload(), "spectrum-logo.png");
    }
}
