package io.github.giulong.spectrum.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;

import static io.github.giulong.spectrum.utils.Metadata.FILE_NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Metadata")
class MetadataTest {

    private static MockedStatic<Path> pathMockedStatic;

    @Mock
    private Path path;

    @Mock
    private Path filePath;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private JsonUtils jsonUtils;

    @Mock
    private Configuration configuration;

    @Mock
    private Configuration.Runtime runtime;

    @InjectMocks
    private Metadata metadata;

    @BeforeEach
    public void beforeEach() {
        pathMockedStatic = mockStatic(Path.class);

        ReflectionUtils.setField("jsonUtils", metadata, jsonUtils);
        ReflectionUtils.setField("fileUtils", metadata, fileUtils);
    }

    @AfterEach
    public void afterEach() {
        pathMockedStatic.close();;
    }

    @Test
    @DisplayName("getInstance should return the singleton")
    public void getInstance() {
        //noinspection EqualsWithItself
        assertSame(Metadata.getInstance(), Metadata.getInstance());
    }

    @Test
    @DisplayName("sessionClosedFrom should write the metadata.json in the configured cache folder")
    public void sessionClosedFrom() {
        final String cacheFolder = "cacheFolder";
        final String content = "content";

        when(configuration.getRuntime()).thenReturn(runtime);
        when(runtime.getCacheFolder()).thenReturn(cacheFolder);
        when(Path.of(cacheFolder)).thenReturn(path);
        when(path.resolve(FILE_NAME)).thenReturn(filePath);
        when(jsonUtils.write(metadata)).thenReturn(content);

        metadata.sessionClosedFrom(configuration);


        verify(fileUtils).write(filePath, content);
    }
}