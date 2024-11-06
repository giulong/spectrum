package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.*;

class SlackConsumerTest {

    private static MockedStatic<FileUtils> fileUtilsMockedStatic;
    private static MockedStatic<FreeMarkerWrapper> freeMarkerWrapperMockedStatic;
    private static MockedStatic<Slack> slackMockedStatic;
    private static MockedStatic<ChatPostMessageRequest> chatPostMessageRequestMockedStatic;

    @Mock
    private Event event;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private Slack slack;

    @Mock
    private MethodsClient methodsClient;

    @Mock
    private ChatPostMessageRequest.ChatPostMessageRequestBuilder chatPostMessageRequestBuilder;

    @Mock
    private ChatPostMessageRequest chatPostMessageRequest;

    @BeforeEach
    void beforeEach() {
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        slackMockedStatic = mockStatic(Slack.class);
        chatPostMessageRequestMockedStatic = mockStatic(ChatPostMessageRequest.class);
    }

    @AfterEach
    void afterEach() {
        fileUtilsMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
        slackMockedStatic.close();
        chatPostMessageRequestMockedStatic.close();
    }

    @Test
    @DisplayName("accept should send a notification to the provided channel using the provided token")
    void accept() throws SlackApiException, IOException {
        final String template = "template";
        final String interpolatedTemplate = "interpolatedTemplate";
        final String channel = "channel";
        final String token = "token";

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.readTemplate("slack.json")).thenReturn(template);

        when(FreeMarkerWrapper.getInstance()).thenReturn(freeMarkerWrapper);
        when(freeMarkerWrapper.interpolate(template, Map.of("event", event))).thenReturn(interpolatedTemplate);

        when(ChatPostMessageRequest.builder()).thenReturn(chatPostMessageRequestBuilder);
        when(chatPostMessageRequestBuilder.channel(channel)).thenReturn(chatPostMessageRequestBuilder);
        when(chatPostMessageRequestBuilder.text("Spectrum notification")).thenReturn(chatPostMessageRequestBuilder);
        when(chatPostMessageRequestBuilder.blocksAsString(interpolatedTemplate)).thenReturn(chatPostMessageRequestBuilder);
        when(chatPostMessageRequestBuilder.build()).thenReturn(chatPostMessageRequest);
        when(Slack.getInstance()).thenReturn(slack);
        when(slack.methods(token)).thenReturn(methodsClient);

        final SlackConsumer slackConsumer = new SlackConsumer();
        slackConsumer.channel = channel;
        slackConsumer.token = token;
        slackConsumer.accept(event);

        verify(methodsClient).chatPostMessage(chatPostMessageRequest);
    }
}
