package io.github.giulong.spectrum.utils.reporters;

import io.github.giulong.spectrum.interfaces.reports.CanReport;
import io.github.giulong.spectrum.interfaces.reports.Reportable;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class Reporter implements CanReport {

    protected static final FileUtils FILE_UTILS = FileUtils.getInstance();

    protected static final FreeMarkerWrapper FREE_MARKER_WRAPPER = FreeMarkerWrapper.getInstance();

    @Override
    public void flush(@NotNull final Reportable reportable) {
        final String template = getTemplate();
        doOutputFrom(FREE_MARKER_WRAPPER.interpolate(template, FILE_UTILS.read(String.format("/%s", template)), reportable.getVars()));
        cleanupOldReports();
    }
}
