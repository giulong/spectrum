package io.github.giulong.spectrum.interfaces.reports;

import io.github.giulong.spectrum.utils.Retention;

public interface CanProduceMetadata {
    Retention getRetention();

    void produceMetadata();
}
