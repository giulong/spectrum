package io.github.giulong.spectrum.it.tests;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it.pages.DownloadPage;
import io.github.giulong.spectrum.it.pages.UploadPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@DisplayName("Files Test")
@SuppressWarnings("unused")
public class FilesIT extends SpectrumTest<Void> {

    // this must be different from the downloaded file since herokuapp will randomly serve exactly the files used to test the upload
    private static final String FILE_TO_DOWNLOAD = "empty.txt";
    private static final String FILE_TO_UPLOAD = "spectrum-logo.png";

    private DownloadPage downloadPage;
    private UploadPage uploadPage;

    @Test
    @DisplayName("download")
    @Tags({
            @Tag("tag1"),
            @Tag("tag2"),
    })
    public void download() {
        // We call the inherited helper method to ensure a fresh download
        deleteDownloadsFolder();

        downloadPage.open();
        downloadPage.getDownloadLinks().getFirst().click();

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
    }

    @Test
    @DisplayName("successful download")
    public void successfulDownload() throws InterruptedException {
        // We call the inherited helper method to ensure a fresh download
        deleteDownloadsFolder();

        driver.get("https://demoqa.com/upload-download");

        Thread.sleep(2000); // waiting for ads to be shown to not intercept clicks
        pageLoadWait.until(elementToBeClickable(By.id("downloadButton"))).click();

        assertTrue(checkDownloadedFile("sampleFile.jpeg"));
    }
}
