package io.github.giulong.spectrum.interfaces;

import io.github.giulong.spectrum.utils.reporters.Reporter;

import java.util.List;
import java.util.Map;

public interface Reportable {
    List<Reporter> getReporters();

    Map<String, Object> getVars();
}
