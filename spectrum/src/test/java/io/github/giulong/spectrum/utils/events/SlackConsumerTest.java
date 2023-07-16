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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SlackConsumer")
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
    public void beforeEach() {
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        slackMockedStatic = mockStatic(Slack.class);
        chatPostMessageRequestMockedStatic = mockStatic(ChatPostMessageRequest.class);
    }

    @AfterEach
    public void afterEach() {
        fileUtilsMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
        slackMockedStatic.close();
        chatPostMessageRequestMockedStatic.close();
    }

    @Test
    @DisplayName("consume should send a notification to the provided channel using the provided token")
    public void consume() throws SlackApiException, IOException {
        final String template = "template";
        final String interpolatedTemplate = "interpolatedTemplate";
        final String channel = "channel";
        final String token = "token";

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.read("/templates/slack.json")).thenReturn(template);

        when(FreeMarkerWrapper.getInstance()).thenReturn(freeMarkerWrapper);
        when(freeMarkerWrapper.interpolate("slack", template, Map.of("event", event))).thenReturn(interpolatedTemplate);

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
        slackConsumer.consumes(event);

        verify(methodsClient).chatPostMessage(chatPostMessageRequest);
    }
}
