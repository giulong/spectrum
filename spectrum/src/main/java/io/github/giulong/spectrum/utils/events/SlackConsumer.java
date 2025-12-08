package io.github.giulong.spectrum.utils.events;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.slack.api.Slack;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class SlackConsumer extends EventsConsumer {

    @JsonIgnore
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();

    @JsonPropertyDescription("Template to be used when creating the message")
    private final String template = "slack.json";

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Target channel where to send the message")
    private String channel;

    @SuppressWarnings("unused")
    @JsonPropertyDescription("Bot User OAuth Token")
    private String token;

    @JsonPropertyDescription("Notification text")
    private final String text = "Spectrum notification";

    @Override
    @SneakyThrows
    public void accept(final Event event) {
        final Map<String, Object> vars = Map.of("event", event);
        final String interpolatedTemplate = freeMarkerWrapper.interpolateTemplate(template, vars);

        Slack
                .getInstance()
                .methods(token)
                .chatPostMessage(ChatPostMessageRequest.builder()
                        .channel(channel)
                        .text(text)
                        .blocksAsString(interpolatedTemplate)
                        .build());
    }
}
