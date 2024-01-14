package io.github.giulong.spectrum.interfaces;

import io.github.giulong.spectrum.utils.Configuration;
import lombok.Generated;

public interface SessionHook {

    @Generated
    default void sessionOpened() {
    }

    @Generated
    default void sessionOpenedFrom(Configuration configuration) {
    }

    @Generated
    default void sessionClosed() {
    }

    @Generated
    default void sessionClosedFrom(Configuration configuration) {
    }
}
