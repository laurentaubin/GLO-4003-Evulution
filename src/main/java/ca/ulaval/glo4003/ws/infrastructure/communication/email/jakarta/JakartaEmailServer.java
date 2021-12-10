package ca.ulaval.glo4003.ws.infrastructure.communication.email.jakarta;


import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailContent;
import ca.ulaval.glo4003.ws.infrastructure.communication.email.EmailServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;

public class JakartaEmailServer implements EmailServer {
  private static final Logger LOGGER = LogManager.getLogger();

  private final MessageFactory messageFactory;
  private final TransportWrapper transportWrapper;

  public JakartaEmailServer(MessageFactory messageFactory) {
    this(messageFactory, new TransportWrapper());
  }

  public JakartaEmailServer(MessageFactory messageFactory, TransportWrapper transportWrapper) {
    this.messageFactory = messageFactory;
    this.transportWrapper = transportWrapper;
  }

  @Override
  public void send(String sender, String recipient, EmailContent emailContent) {
    LOGGER.info(
        String.format(
            "Sending email notification to '%s' with subject '%s'",
            recipient, emailContent.getSubject()));
    try {
      Message message = messageFactory.create(sender, recipient, emailContent);
      transportWrapper.send(message);
    } catch (MessagingException exception) {
      LOGGER.error(String.format("Could not send email to %s", recipient), exception);
    }
  }
}
