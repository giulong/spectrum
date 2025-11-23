package io.github.giulong.spectrum.it_grid.tests;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.giulong.spectrum.SpectrumTest;
import io.github.giulong.spectrum.it_grid.pages.DownloadPage;
import io.github.giulong.spectrum.it_grid.pages.UploadPage;

import lombok.Getter;
import lombok.SneakyThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.TimeoutException;

@DisplayName("Files Test")
@SuppressWarnings("unused")
class FilesIT extends SpectrumTest<Void> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

        final Path downloadsFolder = Path.of(String.valueOf(configuration.getVars().get("downloadsFolder")));
        assertTrue(Files.exists(downloadsFolder));
        assertNotEquals(0, Objects.requireNonNull(downloadsFolder.toFile().listFiles()).length);
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

    @Test
    @DisplayName("additional grid capabilities")
    void additionalGridCapabilities() throws IOException {
        final HttpURLConnection connection = setupConnectionToGrid();

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] payload = "{\"query\":\"{ sessionsInfo { sessions { id, capabilities } } }\"}".getBytes(UTF_8);
            outputStream.write(payload, 0, payload.length);
        }

        final Response response = mapResponseFrom(connection);

        assertEquals("6c85-xxxx-xxxx", response.data.sessionsInfo.sessions.getFirst().capabilities.get("my:token"));
    }

    private HttpURLConnection setupConnectionToGrid() throws IOException {
        final URL url = URI.create("http://localhost:4444/graphql").toURL();
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        return connection;
    }

    @SneakyThrows
    private Response mapResponseFrom(final HttpURLConnection connection) {
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            final String responseString = scanner.useDelimiter("\\A").next()
                    .replace("\\n", "")
                    .replace("\\", "")
                    .replace("\"{", "{")
                    .replace("}\"", "}");

            return OBJECT_MAPPER.readValue(responseString, Response.class);
        }
    }

    @Getter
    private static final class Response {
        private Data data;

        @Getter
        private static final class Data {
            private SessionsInfo sessionsInfo;

            @Getter
            private static final class SessionsInfo {
                private List<Session> sessions;

                @Getter
                private static final class Session {
                    private String id;
                    private Map<String, Object> capabilities;
                }
            }
        }
    }
}
