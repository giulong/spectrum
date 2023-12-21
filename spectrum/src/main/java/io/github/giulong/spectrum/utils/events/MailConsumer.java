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

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Getter
public class MailConsumer extends EventsConsumer {

    private static final FileUtils FILE_UTILS = FileUtils.getInstance();

    private static final FreeMarkerWrapper FREE_MARKER_WRAPPER = FreeMarkerWrapper.getInstance();

    private static final Mailer MAILER = MailerBuilder.buildMailer();

    @SuppressWarnings("FieldMayBeFinal")
    @JsonPropertyDescription("Template to be used when creating the message")
    private String template = "mail.html";

    @JsonPropertyDescription("List of attachments to add to the email")
    protected List<Attachment> attachments;

    public void consumes(final Event event) {
        final Map<String, Object> vars = Map.of("event", event);
        final String interpolatedTemplate = FREE_MARKER_WRAPPER.interpolate("mail", FILE_UTILS.readTemplate(template), vars);

        MAILER.sendMail(
                EmailBuilder
                        .startingBlank()
                        .withHTMLText(interpolatedTemplate)
                        .withAttachments(attachments
                                .stream()
                                .map(a -> new AttachmentResource(a.getName(), new FileDataSource(a.getFile())))
                                .collect(toList()))
                        .buildEmail());
    }
}
