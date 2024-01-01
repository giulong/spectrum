package io.github.giulong.spectrum.interfaces.reports;

public interface CanReport {
    String getTemplate();

    void flush(Reportable reportable);

    void doOutputFrom(String interpolatedTemplate);

    void cleanupOldReports();
}
