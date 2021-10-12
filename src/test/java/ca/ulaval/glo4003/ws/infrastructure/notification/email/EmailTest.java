package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailTest {
  private static final String A_SENDER_EMAIL = "sender@email.com";
  private static final String A_RECIPIENT_EMAIL = "recipient@email.com";

  @Mock private EmailContentDto emailContentDto;
  @Mock private EmailServer emailServer;

  @Test
  public void whenSend_thenSendEmailUsingEmailServer() {
    // given
    Email email = new Email(emailServer, A_SENDER_EMAIL, A_RECIPIENT_EMAIL, emailContentDto);

    // when
    email.send();

    // then
    verify(emailServer).send(A_SENDER_EMAIL, A_RECIPIENT_EMAIL, emailContentDto);
  }
}
