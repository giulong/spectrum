package io.github.giulong.spectrum.extensions.resolvers;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import io.github.giulong.spectrum.SpectrumSessionListener;
import io.github.giulong.spectrum.pojos.Configuration;
import io.github.giulong.spectrum.types.TestData;
import io.github.giulong.spectrum.utils.video.Video;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;

import java.nio.file.Path;

import static com.aventstack.extentreports.Status.INFO;
import static com.aventstack.extentreports.markuputils.ExtentColor.*;
import static com.aventstack.extentreports.markuputils.MarkupHelper.createLabel;
import static io.github.giulong.spectrum.extensions.resolvers.ConfigurationResolver.CONFIGURATION;
import static io.github.giulong.spectrum.extensions.resolvers.TestDataResolver.TEST_DATA;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

@Slf4j
public class ExtentTestResolver extends TypeBasedParameterResolver<ExtentTest> {

    public static final String EXTENT_TEST = "extentTest";

    public static ExtentTest createExtentTestFrom(final ExtensionContext context) {
        final String className = context.getParent().orElseThrow().getDisplayName();
        final String testName = context.getDisplayName();

        return SpectrumSessionListener.getExtentReports()
                .createTest(String.format("<div id=\"%s-%s\">%s</div>%s", transformInKebabCase(className), transformInKebabCase(testName), className, testName));
    }

    protected static String transformInKebabCase(final String string) {
        return string.replaceAll("\\s", "-").toLowerCase();
    }

    public static ExtentColor getColorOf(final Status status) {
        return switch (status) {
            case FAIL -> RED;
            case SKIP -> AMBER;
            default -> GREEN;
        };
    }

    @Override
    public ExtentTest resolveParameter(final ParameterContext arg0, final ExtensionContext context) throws ParameterResolutionException {
        log.debug("Resolving {}", EXTENT_TEST);
        final ExtensionContext.Store store = context.getStore(GLOBAL);
        final ExtentTest extentTest = createExtentTestFrom(context);
        final Video video = store.get(CONFIGURATION, Configuration.class).getVideo();
        final Video.ExtentTest videoExtentTest = video.getExtentTest();

        if (!video.isDisabled() && videoExtentTest.isAttach()) {
            final int width = videoExtentTest.getWidth();
            final int height = videoExtentTest.getHeight();
            final Path src = store.get(TEST_DATA, TestData.class).getVideoPath();

            extentTest.info(String.format("<video controls width=\"%d\" height=\"%d\" src=\"%s\" type=\"video/mp4\"/>", width, height, src));
        }

        extentTest.info(createLabel("START TEST", getColorOf(INFO)));
        store.put(EXTENT_TEST, extentTest);

        return extentTest;
    }
}
