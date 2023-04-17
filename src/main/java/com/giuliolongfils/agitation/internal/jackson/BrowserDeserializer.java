package com.giuliolongfils.agitation.internal.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.giuliolongfils.agitation.browsers.*;

import java.io.IOException;

public class BrowserDeserializer extends JsonDeserializer<Browser<?>> {

    @Override
    public Browser<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();

        return switch (value) {
            case "chrome" -> new Chrome();
            case "firefox" -> new Firefox();
            case "ie" -> new InternetExplorer();
            case "edge" -> new Edge();
            default -> throw new RuntimeException("Value " + value + " is not a valid browser!");
        };
    }
}
