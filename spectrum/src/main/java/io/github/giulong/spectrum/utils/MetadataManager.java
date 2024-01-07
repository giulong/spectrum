package io.github.giulong.spectrum.utils;

import io.github.giulong.spectrum.interfaces.SessionHook;
import io.github.giulong.spectrum.interfaces.reports.CanReport;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor(access = PRIVATE)
@Slf4j
@SuppressWarnings("unused")
public class MetadataProducer implements SessionHook {

    public static final String FILE_NAME = "metadata.json";
    private static final MetadataProducer INSTANCE = new MetadataProducer();

    private final JsonUtils jsonUtils = JsonUtils.getInstance();
    private final FileUtils fileUtils = FileUtils.getInstance();

    private Metadata metadata;

    public static MetadataProducer getInstance() {
        return INSTANCE;
    }

    @Override
    public void sessionOpenedFrom(final Configuration configuration) {
        log.debug("Session opened hook");

        final Path path = Path.of(configuration.getRuntime().getCacheFolder()).resolve(FILE_NAME);
        metadata = jsonUtils.read(path.toFile(), Metadata.class);
    }

    @Override
    public void sessionClosedFrom(final Configuration configuration) {
        log.debug("Session closed hook");

        if (configuration.getSummary().isExecutionSuccessful()) {
            configuration
                    .getTestBook()
                    .getReporters()
                    .stream()
                    .filter(canReportTestBook -> canReportTestBook.getOutput() != null)
                    .forEach(this::addToNamespace);
        }

        final Path path = Path.of(configuration.getRuntime().getCacheFolder()).resolve(FILE_NAME);
        fileUtils.write(path, jsonUtils.write(metadata));
    }

    public FixedSizeQueue<String> getSuccessfulQueueOf(final CanReport canReport) {
        return metadata.execution.successful.getOrDefault(getNamespaceFor(canReport), new FixedSizeQueue<>());
    }

    public String getNamespaceFor(final CanReport canReport) {
        return canReport.getClass().getSimpleName();
    }

    protected void addToNamespace(final CanReport canReport) {
        final String namespace = getNamespaceFor(canReport);
        final String entryName = canReport.getOutput();
        final int maxSize = canReport.getRetention().getSuccessful();
        final FixedSizeQueue<String> queue = metadata.execution.successful.getOrDefault(namespace, new FixedSizeQueue<>());

        log.debug("Adding {} to namespace {}. Current size: {}, max capacity: {}", entryName, namespace, queue.size(), maxSize);
        queue
                .shrinkTo(maxSize - 1)
                .add(entryName);

        metadata.execution.successful.put(namespace, queue);
    }

    @Getter
    public static class Metadata {

        private final Execution execution = new Execution();

        @Getter
        public static class Execution {
            private Map<String, FixedSizeQueue<String>> successful = new HashMap<>();
        }
    }
}
