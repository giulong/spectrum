package io.github.giulong.spectrum.internals.jackson.deserializers.interpolation.interpolators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;

import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ExternalInterpolatorTest {

    private final String value = "value";

    @Mock
    private JsonParser jsonParser;

    @Mock
    private List<String> accumulator;

    @Mock
    private JsonStreamContext context;

    @Mock
    private JsonStreamContext parentContext;

    @Mock
    private JsonStreamContext grandParentContext;

    @InjectMocks
    private DummyExternalInterpolator interpolator;

    @InjectMocks
    private DummyNotFoundExternalInterpolator interpolatorNotFound;

    @BeforeEach
    void beforeEach() {
        Reflections.setField("prefix", interpolator, "prefix");
        Reflections.setField("delimiter", interpolator, ".");

        Reflections.setField("prefix", interpolatorNotFound, "prefix");
        Reflections.setField("delimiter", interpolatorNotFound, ".");
    }

    @Test
    @DisplayName("findVariableFor should return an empty optional if no variable is found")
    void findVariableForEmpty() {
        when(context.getParent()).thenReturn(null);
        when(jsonParser.getParsingContext()).thenReturn(context);

        assertEquals(Optional.empty(), interpolatorNotFound.findVariableFor(value, jsonParser));
    }

    @Test
    @DisplayName("findVariableFor should return an optional containing the variable found")
    void findVariableFor() {
        when(context.getParent()).thenReturn(null);
        when(jsonParser.getParsingContext()).thenReturn(context);

        assertEquals(Optional.of("prefixFound"), interpolator.findVariableFor(value, jsonParser));
    }

    @Test
    @DisplayName("getKeyPathTokens should stop when parent context is null")
    void getKeyPathTokensNoParentContext() {
        when(context.getParent()).thenReturn(null);

        interpolator.getKeyPathTokens(accumulator, context);

        verifyNoInteractions(accumulator);
        verifyNoMoreInteractions(context);
    }

    @Test
    @DisplayName("getKeyPathTokens should collect all the tokens in the provided accumulator")
    void getKeyPathTokens() {
        final String currentName = "currentName";
        final String parentCurrentName = "parentCurrentName";

        when(context.getParent()).thenReturn(parentContext);
        when(parentContext.getParent()).thenReturn(grandParentContext);
        when(grandParentContext.getParent()).thenReturn(null);

        when(context.getCurrentName()).thenReturn(currentName);
        when(parentContext.getCurrentName()).thenReturn(parentCurrentName);

        final InOrder inOrder = inOrder(accumulator);

        interpolator.getKeyPathTokens(accumulator, context);

        inOrder.verify(accumulator).add(parentCurrentName);
        inOrder.verify(accumulator).add(currentName);
        verifyNoMoreInteractions(grandParentContext);
    }

    private static final class DummyExternalInterpolator extends ExternalInterpolator {

        @Override
        public Function<String, String> getConsumer() {
            return s -> s + "Found";
        }
    }

    private static final class DummyNotFoundExternalInterpolator extends ExternalInterpolator {

        @Override
        public Function<String, String> getConsumer() {
            return s -> null;
        }
    }
}
