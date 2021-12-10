package ca.ulaval.glo4003.ws.infrastructure.notification.email;

import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.warehouse.order.Order;
import ca.ulaval.glo4003.ws.infrastructure.notification.NotificationType;
import ca.ulaval.glo4003.ws.infrastructure.notification.exception.NotificationContentNotRegisteredException;
import java.util.Map;
import java.util.Optional;

public class NotificationEmailFactory {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final EmailServer emailServer;
  private final Map<NotificationType, EmailContent> emailContents;

  public NotificationEmailFactory(Map<NotificationType, EmailContent> emailContents) {
    this(serviceLocator.resolve(EmailServer.class), emailContents);
  }

  public NotificationEmailFactory(
      EmailServer emailServer, Map<NotificationType, EmailContent> emailContents) {
    this.emailServer = emailServer;
    this.emailContents = emailContents;
  }

  public Email createDelayNotificationEmail(
      NotificationType notificationType, Order order, String senderEmail, User recipientUser) {
    EmailContent emailContent = getEmailContent(notificationType);

    formatEmailContent(order, recipientUser, emailContent);
    return new Email(emailServer, senderEmail, recipientUser.getEmail(), emailContent);
  }

  private EmailContent getEmailContent(NotificationType notificationType) {
    return Optional.ofNullable(emailContents.get(notificationType))
        .orElseThrow(() -> new NotificationContentNotRegisteredException(notificationType));
  }

  private void formatEmailContent(Order order, User recipientUser, EmailContent emailContent) {
    emailContent.formatSubject(order.getId());
    emailContent.formatBodyMessage(
        recipientUser.getName(), order.getAssemblyDelay().inWeeks(), order.computeDeliveryDate());
  }
}
