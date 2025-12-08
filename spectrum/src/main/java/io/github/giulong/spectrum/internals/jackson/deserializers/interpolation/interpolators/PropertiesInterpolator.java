package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import java.util.function.Function;

public class PropertiesInterpolator extends ExternalInterpolator {

    @Override
    public Function<String, String> getConsumer() {
        return System::getProperty;
    }
}
