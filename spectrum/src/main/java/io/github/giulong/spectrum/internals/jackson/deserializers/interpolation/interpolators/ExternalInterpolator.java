package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class ExternalInterpolator extends Interpolator {

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Variable prefix")
    private String prefix;

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Variable tokens' delimiter")
    private String delimiter;

    public abstract Function<String, String> getConsumer();

    @Override
    public Optional<String> findVariableFor(final String value, final JsonParser jsonParser) {
        final String className = getClass().getSimpleName();
        final List<String> keyPathTokens = new ArrayList<>();
        keyPathTokens.add(prefix);
        getKeyPathTokens(keyPathTokens, jsonParser.getParsingContext());

        final String keyPath = keyPathTokens
                .stream()
                .filter(Objects::nonNull)
                .collect(joining(delimiter));
        log.trace("{} is looking for {}", className, keyPath);

        final String key = getConsumer().apply(keyPath);
        if (key != null) {
            log.debug("{} found {} = {}", className, keyPath, key);
            return Optional.of(key);
        }

        return Optional.empty();
    }

    void getKeyPathTokens(final List<String> accumulator, final JsonStreamContext context) {
        final JsonStreamContext parentContext = context.getParent();

        if (parentContext != null) {
            getKeyPathTokens(accumulator, parentContext);
            accumulator.add(context.getCurrentName());
        }
    }
}
