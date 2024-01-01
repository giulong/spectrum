package io.github.giulong.spectrum.interfaces;

import io.github.giulong.spectrum.utils.reporters.CanReport;

import java.util.List;
import java.util.Map;

public interface Reportable {
    List<? extends CanReport> getReporters();

    Map<String, Object> getVars();
}
