package ca.ulaval.glo4003.ws.infrastructure.communication.email.jakarta;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

public class TransportWrapper {
  public void send(Message mimeMessage) throws MessagingException {
    Transport.send(mimeMessage);
  }
}
