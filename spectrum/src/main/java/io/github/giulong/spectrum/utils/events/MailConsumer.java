package io.github.giulong.spectrum.utils.events;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.giulong.spectrum.pojos.events.Attachment;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import jakarta.activation.FileDataSource;
import lombok.Getter;
import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class MailConsumer extends EventsConsumer {

    private static final FileUtils FILE_UTILS = FileUtils.getInstance();

    private static final FreeMarkerWrapper FREE_MARKER_WRAPPER = FreeMarkerWrapper.getInstance();

    private static final Mailer MAILER = MailerBuilder.buildMailer();

    @JsonPropertyDescription("Template to be used when creating the message")
    private final String template = "mail.html";

    @JsonPropertyDescription("List of attachments to add to the email")
    private final List<Attachment> attachments = new ArrayList<>();

    @Override
    public void accept(final Event event) {
        final Map<String, Object> vars = Map.of("event", event);
        final String interpolatedTemplate = FREE_MARKER_WRAPPER.interpolate(FILE_UTILS.readTemplate(template), vars);

        MAILER.sendMail(EmailBuilder
                .startingBlank()
                .withHTMLText(interpolatedTemplate)
                .withAttachments(attachments
                        .stream()
                        .map(a -> new AttachmentResource(a.getName(), new FileDataSource(a.getFile())))
                        .toList())
                .buildEmail());
    }
}
