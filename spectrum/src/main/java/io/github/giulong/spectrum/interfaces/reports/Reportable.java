package io.github.giulong.spectrum.interfaces.reports;

import java.util.List;
import java.util.Map;

public interface Reportable {
    List<? extends CanReport> getReporters();

    Map<String, Object> getVars();
}
