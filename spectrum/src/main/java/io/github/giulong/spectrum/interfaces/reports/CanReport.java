package io.github.giulong.spectrum.interfaces.reports;

import lombok.Generated;

public interface CanReport {
    String getTemplate();

    void flush(Reportable reportable);

    void doOutputFrom(String interpolatedTemplate);

    @Generated
    default void cleanupOldReports() {
    }

    @Generated
    default void open() {
    }
}
