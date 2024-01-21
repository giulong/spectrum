package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import lombok.Getter;

import java.io.IOException;
import java.util.Map;

@Getter
public class SlackConsumer extends EventsConsumer {

    private static final FileUtils FILE_UTILS = FileUtils.getInstance();

    private static final FreeMarkerWrapper FREE_MARKER_WRAPPER = FreeMarkerWrapper.getInstance();

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPropertyDescription("Template to be used when creating the message")
    private String template = "slack.json";

    @JsonPropertyDescription("Target channel where to send the message")
    protected String channel;

    @JsonPropertyDescription("Bot User OAuth Token")
    protected String token;

    @Override
    public void consumes(final Event event) throws SlackApiException, IOException {
        final Map<String, Object> vars = Map.of("event", event);
        final String interpolatedTemplate = FREE_MARKER_WRAPPER.interpolate(FILE_UTILS.readTemplate(template), vars);

        Slack
                .getInstance()
                .methods(token)
                .chatPostMessage(ChatPostMessageRequest.builder()
                        .channel(channel)
                        .text("Spectrum notification")
                        .blocksAsString(interpolatedTemplate)
                        .build());
    }
}
