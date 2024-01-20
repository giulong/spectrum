package io.github.giulong.spectrum.utils.events;

import io.github.giulong.spectrum.pojos.events.Attachment;
import io.github.giulong.spectrum.pojos.events.Event;
import io.github.giulong.spectrum.utils.FileUtils;
import io.github.giulong.spectrum.utils.FreeMarkerWrapper;
import jakarta.activation.FileDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("MailConsumer")
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
    public void beforeEach() {
        fileUtilsMockedStatic = mockStatic(FileUtils.class);
        freeMarkerWrapperMockedStatic = mockStatic(FreeMarkerWrapper.class);
        mailerBuilderMockedStatic = mockStatic(MailerBuilder.class);
        emailBuilderMockedStatic = mockStatic(EmailBuilder.class);
    }

    @AfterEach
    public void afterEach() {
        fileUtilsMockedStatic.close();
        freeMarkerWrapperMockedStatic.close();
        mailerBuilderMockedStatic.close();
        emailBuilderMockedStatic.close();
    }

    @Test
    @DisplayName("consume should send an email with the provided attachments interpolating the provided template")
    public void consume() {
        final String template = "template";
        final String interpolatedTemplate = "interpolatedTemplate";
        final String name1 = "name1";
        final String name2 = "name2";
        final String file1 = "file1";
        final String file2 = "file2";

        MockedConstruction<FileDataSource> fileDataSourceMockedConstruction = mockConstruction(FileDataSource.class, (mock, context) -> {
            if (context.getCount() == 1) {
                assertEquals(file1, context.arguments().get(0));
            }
            if (context.getCount() == 2) {
                assertEquals(file2, context.arguments().get(0));
            }
        });

        MockedConstruction<AttachmentResource> attachmentResourceMockedConstruction = mockConstruction(AttachmentResource.class, (mock, context) -> {
            if (context.getCount() == 1) {
                assertEquals(name1, context.arguments().get(0));
                assertEquals(fileDataSourceMockedConstruction.constructed().get(0), context.arguments().get(1));
            }
            if (context.getCount() == 2) {
                assertEquals(name2, context.arguments().get(0));
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
        mailConsumer.attachments = List.of(attachment1, attachment2);
        mailConsumer.consumes(event);

        final AttachmentResource attachmentResource1 = attachmentResourceMockedConstruction.constructed().get(0);
        final AttachmentResource attachmentResource2 = attachmentResourceMockedConstruction.constructed().get(1);

        assertEquals(attachmentResource1, listArgumentCaptor.getValue().get(0));
        assertEquals(attachmentResource2, listArgumentCaptor.getValue().get(1));
        verify(mailer).sendMail(email);

        attachmentResourceMockedConstruction.close();
        fileDataSourceMockedConstruction.close();
    }
}
