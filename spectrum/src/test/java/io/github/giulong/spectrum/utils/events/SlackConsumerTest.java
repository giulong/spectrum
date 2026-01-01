package io.github.giulong.spectrum.utils.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Map;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;

import io.github.giulong.spectrum.MockFinal;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.Reflections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;

class SlackConsumerTest {

    private static MockedStatic<Slack> slackMockedStatic;
    private static MockedStatic<ChatPostMessageRequest> chatPostMessageRequestMockedStatic;

    @Mock
    private Event event;

    @MockFinal
    @SuppressWarnings("unused")
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private Slack slack;

    @Mock
    private MethodsClient methodsClient;

    @Mock
    private ChatPostMessageRequest.ChatPostMessageRequestBuilder chatPostMessageRequestBuilder;

    @Mock
    private ChatPostMessageRequest chatPostMessageRequest;

    @InjectMocks
    private SlackConsumer consumer;

    @BeforeEach
    void beforeEach() {
        slackMockedStatic = mockStatic();
        chatPostMessageRequestMockedStatic = mockStatic();
    }

    @AfterEach
    void afterEach() {
        slackMockedStatic.close();
        chatPostMessageRequestMockedStatic.close();
    }

    @Test
    @DisplayName("fields should have a default value")
    void defaultValues() {
        assertEquals("slack.json", consumer.getTemplate());
        assertNull(consumer.getChannel());
        assertNull(consumer.getToken());
        assertEquals("Spectrum notification", consumer.getText());
    }

    @Test
    @DisplayName("accept should send a notification to the provided channel using the provided token")
    void accept() throws SlackApiException, IOException {
        final String interpolatedTemplate = "interpolatedTemplate";
        final String channel = "channel";
        final String token = "token";

        Reflections.setField("channel", consumer, channel);
        Reflections.setField("token", consumer, token);

        when(freeMarkerWrapper.interpolateTemplate("slack.json", Map.of("event", event))).thenReturn(interpolatedTemplate);

        when(ChatPostMessageRequest.builder()).thenReturn(chatPostMessageRequestBuilder);
        when(chatPostMessageRequestBuilder.channel(channel)).thenReturn(chatPostMessageRequestBuilder);
        when(chatPostMessageRequestBuilder.text("Spectrum notification")).thenReturn(chatPostMessageRequestBuilder);
        when(chatPostMessageRequestBuilder.blocksAsString(interpolatedTemplate)).thenReturn(chatPostMessageRequestBuilder);
        when(chatPostMessageRequestBuilder.build()).thenReturn(chatPostMessageRequest);
        when(Slack.getInstance()).thenReturn(slack);
        when(slack.methods(token)).thenReturn(methodsClient);

        consumer.accept(event);

        verify(methodsClient).chatPostMessage(chatPostMessageRequest);
    }
}
