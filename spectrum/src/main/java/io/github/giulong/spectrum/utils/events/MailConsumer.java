package io.github.giulong.spectrum.utils.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.github.giulong.spectrum.pojos.events.Attachment;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;

import lombok.Getter;

import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import jakarta.activation.FileDataSource;

@Getter
public class MailConsumer extends EventsConsumer {

    @JsonIgnore
    private final FreeMarkerWrapper freeMarkerWrapper = FreeMarkerWrapper.getInstance();

    @JsonIgnore
    private final Mailer mailer = MailerBuilder.buildMailer();

    @JsonPropertyDescription("Template to be used when creating the message")
    private final String template = "mail.html";

    @JsonPropertyDescription("List of attachments to add to the email")
    private final List<Attachment> attachments = new ArrayList<>();

    @Override
    public void accept(final Event event) {
        final Map<String, Object> vars = Map.of("event", event);
        final String interpolatedTemplate = freeMarkerWrapper.interpolateTemplate(template, vars);

        mailer.sendMail(EmailBuilder
                .startingBlank()
                .withHTMLText(interpolatedTemplate)
                .withAttachments(attachments
                        .stream()
                        .map(a -> new AttachmentResource(a.getName(), new FileDataSource(a.getFile())))
                        .toList())
                .buildEmail());
    }
}
