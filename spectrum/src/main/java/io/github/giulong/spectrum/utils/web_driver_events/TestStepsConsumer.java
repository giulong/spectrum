package io.github.giulong.spectrum.utils.web_driver_events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.events.EventsConsumer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.nio.file.Path;
import java.util.Map;

import static io.github.giulong.spectrum.extensions.resolvers.DriverResolver.TEST_STEP_BUILDER_CONSUMER;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
@Getter
public class TestStepsConsumer extends EventsConsumer {

    @JsonIgnore
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();

    @JsonIgnore
    private final FileUtils fileUtils = FileUtils.getInstance();

    @JsonPropertyDescription("Path to the template to be used, relative to src/test/resources/template. The report produced will match the template's extension")
    private final String template = "test-steps.txt";

    @JsonPropertyDescription("Where to produce the output, relative to the root of the project")
    private final String output = "target/spectrum/tests-steps";

    @Override
    public void accept(final Event event) {
        log.debug("Building test steps report");

        final ExtensionContext.Store store = event.getContext().getStore(GLOBAL);
        final TestData testData = store.get(TEST_DATA, TestData.class);
        final TestStepBuilderConsumer testStepBuilderConsumer = store.get(TEST_STEP_BUILDER_CONSUMER, TestStepBuilderConsumer.class);
        final String fileName = String.format("%s.%s", testData.getTestId(), fileUtils.getExtensionOf(template));
        final Path path = Path.of(output, fileName);
        final String interpolatedTemplate = freeMarkerWrapper.interpolateTemplate(template, Map.of("steps", testStepBuilderConsumer.getTestSteps()));

        fileUtils.write(path, interpolatedTemplate);
    }
}
