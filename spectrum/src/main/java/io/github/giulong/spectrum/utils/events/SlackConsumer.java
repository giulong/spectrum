package io.github.giulong.spectrum.utils.events;

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
    private String template = "slack.json";

    protected String channel;

    protected String token;

    public void consumes(final Event event) throws SlackApiException, IOException {
        final Map<String, Object> vars = Map.of("event", event);
        final String interpolatedTemplate = FREE_MARKER_WRAPPER.interpolate("slack", FILE_UTILS.readTemplate(template), vars);

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
