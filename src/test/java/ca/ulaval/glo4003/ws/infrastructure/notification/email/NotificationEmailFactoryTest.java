package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.ulaval.glo4003.ws.infrastructure.notification.NotificationType;
import ca.ulaval.glo4003.ws.infrastructure.notification.exception.NotificationContentNotRegisteredException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationEmailFactoryTest {
  private static final String A_SENDER_EMAIL = "sender@email.com";
  private static final String A_RECIPIENT_EMAIL = "recipient@email.com";
  private static final EmailContentDto ASSEMBLY_LINE_DELAY_CONTENT =
      new EmailContentDto("a subject", "a message");
  private static final Map<NotificationType, EmailContentDto> emailContents = new HashMap<>();

  @Mock private EmailServer emailServer;

  private NotificationEmailFactory notificationEmailFactory;

  @BeforeEach
  public void setUp() {
    emailContents.put(NotificationType.ASSEMBLY_LINE_DELAY, ASSEMBLY_LINE_DELAY_CONTENT);

    notificationEmailFactory = new NotificationEmailFactory(emailServer, emailContents);
  }

  @Test
  public void whenCreateAssemblyLineDelayNotificationEmail_thenReturnEmailWithEmailServer() {
    // when
    Email actualEmail =
        notificationEmailFactory.createAssemblyLineDelayNotificationEmail(
            A_SENDER_EMAIL, A_RECIPIENT_EMAIL);

    // then
    assertThat(actualEmail.getEmailServer()).isEqualTo(emailServer);
  }

  @Test
  public void
      whenCreateAssemblyLineDelayNotificationEmail_thenReturnEmailWithRightSenderAndRecipient() {
    // when
    Email actualEmail =
        notificationEmailFactory.createAssemblyLineDelayNotificationEmail(
            A_SENDER_EMAIL, A_RECIPIENT_EMAIL);

    // then
    assertThat(actualEmail.getRecipientAddress()).matches(A_RECIPIENT_EMAIL);
    assertThat(actualEmail.getSenderAddress()).matches(A_SENDER_EMAIL);
  }

  @Test
  public void
      givenEmailContentDtoPresent_whenCreateAssemblyLineDelayNotificationEmail_thenReturnEmailWithRightSubjectAndBody() {
    // when
    Email actualEmail =
        notificationEmailFactory.createAssemblyLineDelayNotificationEmail(
            A_SENDER_EMAIL, A_RECIPIENT_EMAIL);

    // then
    assertThat(actualEmail.getEmailContentDto().getSubject())
        .matches(ASSEMBLY_LINE_DELAY_CONTENT.getSubject());
    assertThat(actualEmail.getEmailContentDto().getBodyMessage())
        .matches(ASSEMBLY_LINE_DELAY_CONTENT.getBodyMessage());
  }

  @Test
  public void
      givenEmailContentDtoNotPresent_whenCreateAssemblyLineDelayNotificationEmail_thenThrowNotificationContentNotRegisteredException() {
    // given
    emailContents.clear();

    // when
    Executable creatingNotificationEmail =
        () ->
            notificationEmailFactory.createAssemblyLineDelayNotificationEmail(
                A_SENDER_EMAIL, A_RECIPIENT_EMAIL);

    // then
    assertThrows(NotificationContentNotRegisteredException.class, creatingNotificationEmail);
  }
}
