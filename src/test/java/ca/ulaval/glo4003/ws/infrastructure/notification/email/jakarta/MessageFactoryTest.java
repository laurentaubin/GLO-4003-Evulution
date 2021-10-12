package ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta;

import static com.google.common.truth.Truth.assertThat;

import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailContentDto;
import java.io.IOException;
import java.util.Arrays;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class MessageFactoryTest {
  private static final String A_SENDER_EMAIL = "sender@email.com";
  private static final String A_RECIPIENT_EMAIL = "recipient@email.com";
  private static final EmailContentDto EMAIL_CONTENT_DTO = new EmailContentDto("subject", "body");

  @Mock private Session session;

  private MessageFactory messageFactory;

  @BeforeEach
  public void setUp() {
    messageFactory = new MessageFactory(session);
  }

  @Test
  public void whenCreate_thenReturnMessageWithRightSession() throws MessagingException {
    // when
    Message actualMessage =
        messageFactory.create(A_SENDER_EMAIL, A_RECIPIENT_EMAIL, EMAIL_CONTENT_DTO);

    // then
    assertThat(actualMessage.getSession()).isEqualTo(session);
  }

  @Test
  public void whenCreate_thenReturnMessageWithRightSenderAndRecipient() throws MessagingException {
    // when
    Message actualMessage =
        messageFactory.create(A_SENDER_EMAIL, A_RECIPIENT_EMAIL, EMAIL_CONTENT_DTO);

    // then
    assertThat(Arrays.stream(actualMessage.getFrom()).findFirst().get().toString())
        .matches(A_SENDER_EMAIL);
    assertThat(Arrays.stream(actualMessage.getAllRecipients()).findFirst().get().toString())
        .matches(A_RECIPIENT_EMAIL);
  }

  @Test
  public void whenCreate_thenReturnMessageWithSubjectAndBody()
      throws MessagingException, IOException {
    // when
    Message actualMessage =
        messageFactory.create(A_SENDER_EMAIL, A_RECIPIENT_EMAIL, EMAIL_CONTENT_DTO);

    // then
    assertThat(actualMessage.getSubject()).matches(EMAIL_CONTENT_DTO.getSubject());
    assertThat(actualMessage.getContent().toString()).matches(EMAIL_CONTENT_DTO.getBodyMessage());
  }
}
