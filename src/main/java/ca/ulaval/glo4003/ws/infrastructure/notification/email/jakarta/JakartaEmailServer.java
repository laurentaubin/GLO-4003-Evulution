package ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta;

import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailContentDto;
import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailServer;
import javax.mail.Message;
import javax.mail.MessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JakartaEmailServer implements EmailServer {
  private static final Logger LOGGER = LogManager.getLogger();

  private final MessageFactory messageFactory;
  private final TransportWrapper transportWrapper;

  public JakartaEmailServer(MessageFactory messageFactory, TransportWrapper transportWrapper) {
    this.messageFactory = messageFactory;
    this.transportWrapper = transportWrapper;
  }

  @Override
  public void send(String sender, String recipient, EmailContentDto emailContentDto) {
    try {
      Message message = messageFactory.create(sender, recipient, emailContentDto);
      transportWrapper.send(message);
    } catch (MessagingException exception) {
      LOGGER.error(String.format("Could not send email to %s", recipient), exception);
    }
  }
}
