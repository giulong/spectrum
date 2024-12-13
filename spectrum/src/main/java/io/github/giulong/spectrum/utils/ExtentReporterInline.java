package io.github.giulong.spectrum.utils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@NoArgsConstructor(access = PRIVATE)
public class ExtentReporterInline extends ExtentReporter {

    private static final ExtentReporterInline INSTANCE = new ExtentReporterInline();

    private final HtmlUtils htmlUtils = HtmlUtils.getInstance();

    public static ExtentReporterInline getInstance() {
        return INSTANCE;
    }

    @Override
    public void sessionOpened() {
        log.debug("Session opened hook");

        final Configuration.Extent extent = configuration.getExtent();
        if (!extent.isInline()) {
            return;
        }

        final String reportPath = getReportPathFrom(extent).toString().replace("\\", "/");
        final String reportName = extent.getReportName();

        log.info("After the execution, you'll find the '{}' inline report at file:///{}", reportName, reportPath);
    }

    @SneakyThrows
    @Override
    public void sessionClosed() {
        log.debug("Session closed hook");

        final Configuration.Extent extent = configuration.getExtent();
        if (extent.isInline()) {
            final String inlineReport = htmlUtils.inline(Files.readString(super.getReportPathFrom(extent)));

            fileUtils.write(getReportPathFrom(extent), inlineReport);
        }

        cleanupOldReportsIn(extent.getInlineReportFolder());
    }

    @Override
    Path getMetadata() {
        return getReportPathFrom(configuration.getExtent());
    }

    @Override
    public Path getReportPathFrom(final Configuration.Extent extent) {
        return Path.of(extent.getInlineReportFolder(), extent.getFileName()).toAbsolutePath();
    }
}
