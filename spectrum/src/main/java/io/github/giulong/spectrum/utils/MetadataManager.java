package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.interfaces.reports.CanProduceMetadata;
import io.github.giulong.spectrum.types.ProjectProperties;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class MetadataManager implements SessionHook {

    private static final MetadataManager INSTANCE = new MetadataManager();

    private final YamlUtils yamlUtils = YamlUtils.getInstance();
    private final JsonUtils jsonUtils = JsonUtils.getInstance();
    private final FileUtils fileUtils = FileUtils.getInstance();
    private final ExtentReporter extentReporter = ExtentReporter.getInstance();
    private final ExtentReporterInline extentReporterInline = ExtentReporterInline.getInstance();
    private final Configuration configuration = Configuration.getInstance();

    @Getter
    private Metadata metadata;

    public static MetadataManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void sessionOpened() {
        log.debug("Session opened hook");

        metadata = jsonUtils.readOrEmpty(buildPath().toFile(), Metadata.class);
    }

    @Override
    public void sessionClosed() {
        log.debug("Session closed hook");
        final Summary summary = configuration.getSummary();

        if (summary.isExecutionSuccessful()) {
            extentReporter.produceMetadata();
            extentReporterInline.produceMetadata();

            configuration
                    .getTestBook()
                    .getReporters()
                    .stream()
                    .filter(canReportTestBook -> canReportTestBook instanceof CanProduceMetadata)
                    .map(CanProduceMetadata.class::cast)
                    .forEach(CanProduceMetadata::produceMetadata);

            summary
                    .getReporters()
                    .stream()
                    .filter(canReportSummary -> canReportSummary instanceof CanProduceMetadata)
                    .map(CanProduceMetadata.class::cast)
                    .forEach(CanProduceMetadata::produceMetadata);
        }

        fileUtils.write(buildPath(), jsonUtils.write(metadata));
    }

    public FixedSizeQueue<File> getSuccessfulQueueOf(final CanProduceMetadata canProduceMetadata) {
        return metadata
                .getExecution()
                .getSuccessful()
                .getReports()
                .getOrDefault(getNamespaceOf(canProduceMetadata), new FixedSizeQueue<>());
    }

    public void setSuccessfulQueueOf(final CanProduceMetadata canProduceMetadata, final FixedSizeQueue<File> queue) {
        metadata
                .getExecution()
                .getSuccessful()
                .getReports()
                .put(getNamespaceOf(canProduceMetadata), queue);
    }

    String getNamespaceOf(final Object object) {
        return object.getClass().getSimpleName();
    }

    Path buildPath() {
        final ProjectProperties projectProperties = yamlUtils.readInternal("properties.yaml", ProjectProperties.class);
        final String fileName = String.format("%s-metadata.json", projectProperties.get("name"));

        return Path.of(configuration.getRuntime().getCacheFolder()).resolve(fileName);
    }

    @Getter
    @Generated
    public static class Metadata {

        private final Execution execution = new Execution();

        @Getter
        @Generated
        public static class Execution {

            private final Successful successful = new Successful();

            @Getter
            @Generated
            public static class Successful {
                private final Map<String, FixedSizeQueue<File>> reports = new HashMap<>();
            }
        }
    }
}
