package com.giuliolongfils.agitation.internal.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.giuliolongfils.agitation.browsers.Browser;
import lombok.SneakyThrows;

public class BrowserSerializer extends JsonSerializer<Browser<?>> {

    @Override
    @SneakyThrows
    public void serialize(Browser browser, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        jsonGenerator.writeRawValue(browser.getDriverName());
    }
}
