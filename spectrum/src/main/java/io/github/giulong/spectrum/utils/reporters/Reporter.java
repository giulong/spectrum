package io.github.giulong.spectrum.utils.reporters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.giulong.spectrum.interfaces.reports.CanReport;
import io.github.giulong.spectrum.interfaces.reports.Reportable;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class Reporter implements CanReport {

    @JsonIgnore
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();

    @JsonIgnore
    protected final FileUtils fileUtils = FileUtils.getInstance();

    @Override
    public void flush(@NotNull final Reportable reportable) {
        doOutputFrom(freeMarkerWrapper.interpolate(fileUtils.read(getTemplate()), reportable.getVars()));
        open();
        cleanupOldReports();
    }
}
