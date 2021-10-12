package ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailContentDto;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JakartaEmailServerTest {
  private static final String A_SENDER_EMAIL = "sender@email.com";
  private static final String A_RECIPIENT_EMAIL = "recipient@email.com";

  @Mock private EmailContentDto emailContentDto;
  @Mock MimeMessage mimeMessage;
  @Mock private MessageFactory messageFactory;
  @Mock private TransportWrapper transportWrapper;

  private JakartaEmailServer jakartaEmailServer;

  @BeforeEach
  public void setUp() {
    jakartaEmailServer = new JakartaEmailServer(messageFactory, transportWrapper);
  }

  @Test
  public void givenMimeMessage_whenSend_thenSendEmailUsingTransportWrapper()
      throws MessagingException {
    // given
    given(messageFactory.create(A_SENDER_EMAIL, A_RECIPIENT_EMAIL, emailContentDto))
        .willReturn(mimeMessage);

    // when
    jakartaEmailServer.send(A_SENDER_EMAIL, A_RECIPIENT_EMAIL, emailContentDto);

    // then
    verify(transportWrapper).send(mimeMessage);
  }

  @Test
  public void givenMessagingException_whenSend_thenDoNotThrow() throws MessagingException {
    // given
    given(messageFactory.create(A_SENDER_EMAIL, A_RECIPIENT_EMAIL, emailContentDto))
        .willReturn(mimeMessage);
    doThrow(MessagingException.class).when(transportWrapper).send(mimeMessage);

    // when
    Executable sendingEmail =
        () -> jakartaEmailServer.send(A_SENDER_EMAIL, A_RECIPIENT_EMAIL, emailContentDto);

    // then
    assertDoesNotThrow(sendingEmail);
  }
}
