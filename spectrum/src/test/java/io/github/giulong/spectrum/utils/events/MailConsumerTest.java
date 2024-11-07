package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.pojos.events.Attachment;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import io.github.giulong.spectrum.utils.Reflections;
import jakarta.activation.FileDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MailConsumerTest {

    private static MockedStatic<FileUtils> fileUtilsMockedStatic;
    private static MockedStatic<FreeMarkerWrapper> freeMarkerWrapperMockedStatic;
    private static MockedStatic<MailerBuilder> mailerBuilderMockedStatic;
    private static MockedStatic<EmailBuilder> emailBuilderMockedStatic;

    @Mock
    private Event event;

    @Mock
    private FreeMarkerWrapper freeMarkerWrapper;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private EmailPopulatingBuilder emailPopulatingBuilder;

    @Mock
    private Email email;

    @Mock
    private Mailer mailer;

    @Mock
    private Attachment attachment1;

    @Mock
    private Attachment attachment2;

    @Captor
    private ArgumentCaptor<List<AttachmentResource>> listArgumentCaptor;

    @BeforeEach
    void beforeEach() {
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        mailerBuilderMockedStatic = mockStatic(MailerBuilder.class);
        emailBuilderMockedStatic = mockStatic(EmailBuilder.class);
    }

    @AfterEach
    void afterEach() {
        fileUtilsMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
        mailerBuilderMockedStatic.close();
        emailBuilderMockedStatic.close();
    }

    @Test
    @DisplayName("accept should send an email with the provided attachments interpolating the provided template")
    void accept() {
        final String template = "template";
        final String interpolatedTemplate = "interpolatedTemplate";
        final String name1 = "name1";
        final String name2 = "name2";
        final String file1 = "file1";
        final String file2 = "file2";

        MockedConstruction<FileDataSource> fileDataSourceMockedConstruction = mockConstruction(FileDataSource.class, (mock, context) -> {
            if (context.getCount() == 1) {
                assertEquals(file1, context.arguments().getFirst());
            }
            if (context.getCount() == 2) {
                assertEquals(file2, context.arguments().getFirst());
            }
        });

        MockedConstruction<AttachmentResource> attachmentResourceMockedConstruction = mockConstruction(AttachmentResource.class, (mock, context) -> {
            if (context.getCount() == 1) {
                assertEquals(name1, context.arguments().getFirst());
                assertEquals(fileDataSourceMockedConstruction.constructed().getFirst(), context.arguments().get(1));
            }
            if (context.getCount() == 2) {
                assertEquals(name2, context.arguments().getFirst());
                assertEquals(fileDataSourceMockedConstruction.constructed().get(1), context.arguments().get(1));
            }
        });

        when(FileUtils.getInstance()).thenReturn(fileUtils);
        when(fileUtils.readTemplate("mail.html")).thenReturn(template);

        when(FreeMarkerWrapper.getInstance()).thenReturn(freeMarkerWrapper);
        when(freeMarkerWrapper.interpolate(template, Map.of("event", event))).thenReturn(interpolatedTemplate);

        when(MailerBuilder.buildMailer()).thenReturn(mailer);

        when(EmailBuilder.startingBlank()).thenReturn(emailPopulatingBuilder);
        when(emailPopulatingBuilder.withHTMLText(interpolatedTemplate)).thenReturn(emailPopulatingBuilder);
        when(emailPopulatingBuilder.withAttachments(listArgumentCaptor.capture())).thenReturn(emailPopulatingBuilder);
        when(emailPopulatingBuilder.buildEmail()).thenReturn(email);

        when(attachment1.getName()).thenReturn(name1);
        when(attachment1.getFile()).thenReturn(file1);

        when(attachment2.getName()).thenReturn(name2);
        when(attachment2.getFile()).thenReturn(file2);

        final MailConsumer mailConsumer = new MailConsumer();
        assertEquals(List.of(), Reflections.getFieldValue("attachments", mailConsumer));  // to check the default is an empty list
        Reflections.setField("attachments", mailConsumer, List.of(attachment1, attachment2));
        mailConsumer.accept(event);

        final AttachmentResource attachmentResource1 = attachmentResourceMockedConstruction.constructed().getFirst();
        final AttachmentResource attachmentResource2 = attachmentResourceMockedConstruction.constructed().get(1);

        assertEquals(attachmentResource1, listArgumentCaptor.getValue().getFirst());
        assertEquals(attachmentResource2, listArgumentCaptor.getValue().get(1));
        verify(mailer).sendMail(email);

        attachmentResourceMockedConstruction.close();
        fileDataSourceMockedConstruction.close();
    }
}
