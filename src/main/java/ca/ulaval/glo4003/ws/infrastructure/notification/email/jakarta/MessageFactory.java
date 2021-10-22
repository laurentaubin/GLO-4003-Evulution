package ca.ulaval.glo4003.ws.infrastructure.notification.email.jakarta;

import ca.ulaval.glo4003.ws.infrastructure.notification.email.EmailContent;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MessageFactory {

  private final Session session;

  public MessageFactory(Session session) {
    this.session = session;
  }

  public Message create(String sender, String recipient, EmailContent emailContent)
      throws MessagingException {
    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(sender));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
    message.setSubject(emailContent.getSubject());
    message.setText(emailContent.getBodyMessage());

    return message;
  }
}
